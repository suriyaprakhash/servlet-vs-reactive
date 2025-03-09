package com.suriyaprakhash.learn.reactive_web.simulation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ClientServer extends Simulation  {


    // Http Configuration
    private HttpProtocolBuilder httpProtocolBuilder = http
            .baseUrl("http://localhost:8080/");

    // Scenario Definition
    private ChainBuilder flux =
            exec(http("Flux call to receive items")
                    .get("client/forkjoin"));


    private ScenarioBuilder loadTestUser = scenario("load test user").exec(flux);

    // Load simulation
    {
        setUp(
                loadTestUser.injectOpen(rampUsers(60).during(Duration.ofSeconds(30)))
        ).protocols(httpProtocolBuilder);
    }
}
