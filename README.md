# Demystifying Blocking vs. Non-Blocking Web Requests

This corresponds to the medium post [here](https://medium.com/p/ef95ca9f02b7/edit)


## Pre-requiste

- Java 21
- Maven
- [Glowroot](https://glowroot.org/)
- toxiproxy - to simulate network latency
- Make sure you place *150MB.csv* file into the **ignore-test-files** directory in root folder, used for **hugefile** testing

## Build

### Maven


### Docker

Build for **servlet** app,
```
docker build --build-arg  PROJECT=servlet -f Dockerfile -t servlet-web-application:1.0 .
```

Build for **reactive** app,
```
docker build --build-arg  PROJECT=reactive -f Dockerfile -t reactive-web-application:1.0 .
```

## Test

Check the respective project on how to test them


# TODO

- Compose Dockerfile & volume in the 150MB file & run both
- Add DB
- Write gatling simulation to test volumes
