package reactive.simulation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class HugeFileSimulation extends Simulation {

    public HugeFileSimulation() {

        ChainBuilder nioStream = exec(
                http("NIO Stream")
                        .get("/hugefile/nio")
        );

        ScenarioBuilder user = scenario("User").exec(
                nioStream,
                pause(1)
        );

        setUp(
                user.injectOpen(atOnceUsers(1))
        ).protocols(reactiveHttp);
    }

    private static HttpProtocolBuilder reactiveHttp =
            http.baseUrl("http://localhost:8081")
                    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .acceptLanguageHeader("en-US,en;q=0.5")
                    .acceptEncodingHeader("gzip, deflate")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
                    );
}