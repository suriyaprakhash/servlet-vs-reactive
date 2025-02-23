package servlet.simulation;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class HugeFileSimulation extends Simulation {

    public HugeFileSimulation() {
        ChainBuilder bioStream = exec(
                http("BIO Stream")
                        .get("/hugefile/bio/stream")
        );

        ScenarioBuilder user = scenario("User").exec(
                bioStream,
                pause(1)
        );

        setUp(
//                user.injectOpen(atOnceUsers(1)),
                user.injectOpen(rampUsersPerSec(2).to(10).during(Duration.ofSeconds(10)))
        ).protocols(servletHttp);
    }


    private static HttpProtocolBuilder servletHttp =
            http.baseUrl("http://localhost:8080")
                    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .acceptLanguageHeader("en-US,en;q=0.5")
                    .acceptEncodingHeader("gzip, deflate")
                    .userAgentHeader(
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
                    );

}