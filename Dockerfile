## docker build --build-arg  PROJECT=servlet -f Dockerfile -t servlet-web-application:1.0 .

# Perform the build in a separate package container
#FROM bellsoft/liberica-openjdk-debian:21-cds AS package
FROM maven:3.9.8-eclipse-temurin-21 AS package
LABEL authors="Suriya Prakhash Deenadayalan"
# the buildarg to be passed from the build command
ARG PROJECT
# the current docker folder is /package
WORKDIR /package
# copy files from the current working dir to the /package folder
COPY . /package
# Run the following to use .mvnw - if needed - by default using mvn here.
#RUN apt-get update && apt-get install dos2unix
#RUN chmod +x mvnw
#RUN dos2unix ./mvnw
# maven package jar
RUN mvn --projects ${PROJECT} clean package -DskipTests

# Perform the extraction in a separate builder container
FROM bellsoft/liberica-openjre-debian:21-cds AS builder
LABEL authors="Suriya Prakhash Deenadayalan"
# the current docker folder is /builder
WORKDIR /builder
# the buildarg to be passed from the build command
ARG PROJECT
# copiying the target folder from the package stage into the current stage's builder directory
COPY --from=package /package/$PROJECT/target .
# rename the jar from target to application.jar - for easyness accross all apps
RUN mv *.jar application.jar
# Extract the jar file using an efficient layout
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# This is the runtime container
FROM bellsoft/liberica-openjre-debian:21-cds
LABEL authors="Suriya Prakhash Deenadayalan"
# the current docker folder is /application
WORKDIR /application

# Add a non-root user - add the group appgroup(GID 101) - and addes the appuser to 101
RUN addgroup --system --group appgroup && \
    adduser --system appuser --gid 101
# Set file ownership
RUN chown -R appuser:appgroup /application
USER appuser

# Copy the extracted jar contents from the builder container into the working directory in the runtime container
# Every copy step creates a new docker layer
# This allows docker to only pull the changes it really needs
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Execute the CDS training run. Archives the Class Data Sharing file .jsa so in used in subsequent JVM runs to speed up
# class loading after the exit - avoiding saves around 40MB
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -jar application.jar
# Start the application jar - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and CDS friendly
ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa","-Dhugefile.path=${HUGEFILE_PATH}","-Dhugefile.bufferByteSize=${HUGEFILE_BUFFER_SIZE}","-jar", "application.jar" ]
EXPOSE 8080