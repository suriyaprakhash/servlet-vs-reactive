# Blocking vs Non-blocking behavior

This corresponds to the medium post [here](https://medium.com/p/ef95ca9f02b7/edit)

## Run

- Run the SERVER at
```
java -Dserver.port=8081  -jar .\target\servlet-vs-reactive.jar
```

- Run the CLIENT at
```
java -Dserver.port=8081  -jar .\target\servlet-vs-reactive.jar
```

## How to test

### Servlet Tomcat vs Reactor Netty

- The project has client and server controller **(flip the web and webflux in pom to test the combinations)**
- Run the client at 8080 and server at 8081
- Make the browser call to the client APIs - which inturn calls the server API
- Comment the desired Web container (tomcat/netty) based on your testing to test the client's __mvc__, __mono__ and __flux__

## Testing requests with Gatling

The test only runs one endpoint - Change it to run the desired endpoint

```
mvnw gatling:test
```