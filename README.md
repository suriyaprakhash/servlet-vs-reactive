# Blocking vs Non-blocking benchmark

This corresponds to the medium post [here](https://medium.com/p/ef95ca9f02b7/edit), making it as a bechmarking tool for Spring boot's web and webflux. 

## Pre-requiste

- Java 21
- Maven
- [Postgres](https://www.postgresql.org/)
- [Gatling](https://gatling.io/)
- Make sure you place *150MB.csv* file into the **ignore-test-files** directory in root folder, used for **hugefile** testing
- Gatling conf - client timeout set to 120 seconds in servlet 
- [Glowroot](https://glowroot.org/) (not-mandatory)
- [toxiproxy](https://github.com/Shopify/toxiproxy) (not-mandatory)

## Build & Run

### Idea

In the run config,

**servlet** app, **add VM option**,
```
-Xmx4g -javaagent:C:\Users\suriy\main\Softwares\Installed\glowroot-0.14.2-dist-1\glowroot\glowroot.jar -Dglowroot.agent.port=4001
```

**reactive** app, **add VM option**,
```
-Xmx4g -javaagent:C:\Users\suriy\main\Softwares\Installed\glowroot-0.14.2-dist-2\glowroot\glowroot.jar -Dglowroot.agent.port=4002
```
here,
- -Xmx - for max java mem to 4g
- Glowroot **javaagent jar** location for it pick and **port** it needs to run on

set the right **working directory** - **_set it to the child and not to the parent_**.

### Maven

Start the build in the parent root folder,
```
./mvnw clean package
```
builds both **servlet** and **reactive**. 

Alternatively, pass **--projects=servlet** to build only one specific module or pass in both,
```
./mvnw --projects=servlet,reactive clean package
```

Run **servlet**,

```
java -XX:SharedArchiveFile=application.jsa -D"hugefile.path"=ignore-test-files/  -jar servlet/target/ServletWebApplication.jar 
```

Run **reactive**,

```
java -XX:SharedArchiveFile=application.jsa -D"hugefile.path"=ignore-test-files/  -jar reactive/target/ReactiveWebApplication.jar 
```

### Docker

Build for **servlet** app,
```
docker build --build-arg  PROJECT=servlet -f Dockerfile -t servlet-web-application:1.0 .
```

Build for **reactive** app,
```
docker build --build-arg  PROJECT=reactive -f Dockerfile -t reactive-web-application:1.0 .
```

We are using **bind mount**, to bind *C:\Users\suriy\main\ws\Projects\servlet-vs-reactive\ignore-test-files* into the */application/test* directory within the container,
so when the app runs from the **application** folder - it will just need ./test/

Run for **servlet** app,
```
docker run -d --name servlet-app-env -m 4g --cpus=2 -p 8080:8080 -v C:\Users\suriy\main\ws\Projects\servlet-vs-reactive\ignore-test-files:/application/test -e HUGEFILE_PATH=./test/ -e HUGEFILE_BUFFER_SIZE=16000 servlet-web-application:1.0
```

Run for **reactive** app,
```
docker run -d --name reactive-app-env -m 4g --cpus=2 -p 8081:8081 -v C:\Users\suriy\main\ws\Projects\servlet-vs-reactive\ignore-test-files:/application/test -e HUGEFILE_PATH=./test/ -e HUGEFILE_BUFFER_SIZE=16000 reactive-web-application:1.0
```
here,

- -d - detached mode
- --name - name of the container
- -m - memory limit set
- --cpus - max. no.of cpu cores to use
- -p - host_port:container_port
- -v - bind mount - host_path:container_path
- -e - Spring @Value property for path in the application

## Swagger

Access **swagger**,

Servlet app,
```
http://localhost:8080/swagger-ui/index.html
```

Reactive app,
```
http://localhost:8081/webjars/swagger-ui/index.html
```

## Gatling tests 

Check the respective project on how to test them

```
.\mvnw.cmd --projects=servlet,reactive gatling:test
```

### Observation

#### Dataabse IO

#### Hugefile IO

Just with web servlet,

| HTTP                                     | SUGGESTION                                                                              | OBSERVATION                                                                                            |
|------------------------------------------|-----------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| Servlet ResponseOutputStream - Read All  | Don't use if you have concurrent calls or memory constraint                             | Reading all bytes will not be able to allocate memory for concurrent calls                             | 
| Servlet ResponseOutputStream - Buffered  | Does not make use of spring boot capabilities, runs on main thread clogging new request | Write is slow for concurrent calls - might timeout for a file 150MB given 120 sec for concurrent calls |
| Servlet StreamingResponseBody - Buffered | Creates async response thread, so the new req will not clog                             | Write is slow for concurrent calls - might timeout for a file 150MB given 120 sec for concurrent calls |
| Servlet Resource                         | TODO - test with toxiproxy                                                              | TODO - test with toxiproxy                                                                             |

Comparing servlet stream against the reactive, 

| HTTP                                    | SUGGESTION                                    | OBSERVATION                                   |
|-----------------------------------------|-----------------------------------------------|-----------------------------------------------|
| Servlet Resource                        | TODO - test with toxiproxy                    | TODO - test with toxiproxy                    | 
| Servlet ResponseOutputStream - Buffered | TODO - test against reactive                  | TODO - test against reactive                  |
| Reactive Stream                         | TODO - test against servlet buffered/resource | TODO - test against servlet buffered/resource |


## Known Issues

**Servlet app**,
- When using Glowroot in debug mode - https://youtrack.jetbrains.com/issue/IDEA-360896/java.lang.IncompatibleClassChangeError-Class-ch.qos.logback.classic.Level-does-not-implement-the-requested-interface

**Reactive app**,
- Swagger somehow is not working with **2.8.x** with spring webflux starter **3.4.x**

# TODO

- Calculate DB perf
- Try adding glowroot into docker 

