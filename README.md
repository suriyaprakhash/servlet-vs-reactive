# Demystifying Blocking vs. Non-Blocking Web Requests

This corresponds to the medium post [here](https://medium.com/p/ef95ca9f02b7/edit)

## Pre-requiste

- Java 21
- Maven
- [Glowroot](https://glowroot.org/)
- [Gatling](https://gatling.io/)
- [toxiproxy](https://github.com/Shopify/toxiproxy)
- Make sure you place *150MB.csv* file into the **ignore-test-files** directory in root folder, used for **hugefile** testing

## Build & Run

### Maven

Start the build in the parent root folder,
```
./mvnw clean package
```
builds both **servlet** and **reactive**

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

## Test

Check the respective project on how to test them


# TODO

- Add DB
- Write gatling simulation to test volumes
