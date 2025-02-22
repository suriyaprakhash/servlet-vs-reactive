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


## Test

Check the respective project on how to test them


# TODO

- Compose Dockerfile & volume in the 150MB file & run both
- Add DB
- Write gatling simulation to test volumes
