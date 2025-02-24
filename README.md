# Demystifying Blocking vs. Non-Blocking Web Requests

This corresponds to the medium post [here](https://medium.com/p/ef95ca9f02b7/edit)

## Pre-requiste

- Java 21
- Maven
- [Glowroot](https://glowroot.org/) (not-mandatory)
- [Gatling](https://gatling.io/)
- [toxiproxy](https://github.com/Shopify/toxiproxy) (not-mandatory)
- Make sure you place *150MB.csv* file into the **ignore-test-files** directory in root folder, used for **hugefile** testing
- Gatling conf - client timeout set to 120 seconds in servlet 

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

## Gatling tests 

Check the respective project on how to test them

```
.\mvnw.cmd --projects=servlet,reactive gatling:test
```

### Observation

TODO:
- Compare normal web request
- Hugefile

# TODO

- Toxiproxy steps
- Try adding glowroot into docker 
- Add DB
