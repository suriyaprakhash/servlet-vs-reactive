package com.suriyaprakhash.learn.reactive_web.biovsnio.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@RequestMapping("client")
@RestController
public class ClientController {

    String NIO_URL = "http://localhost:8081/server/nio";
    String BIO_URL = "http://localhost:8081/server/bio";
    WebClient webClient = WebClient.create();
    RestTemplate restTemplate = new RestTemplate();

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    @GetMapping("mvc")
    public String mvc() {
        log.info("MVC client request entered controller");
        ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<>() {};
        List<String> mvc = restTemplate.exchange(BIO_URL, org.springframework.http.HttpMethod.GET,
                null, typeReference).getBody();
        assert mvc != null;
        log.info("Mono client response received {}", mvc.toString());
        return "Check the client logs for 'Client - MVC blocked and collected'";
    }

    @GetMapping("mono")
    public String mono() {
        log.info("Mono client request entered controller");
        Mono<List<String>> mono = webClient.get().uri(BIO_URL).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });

//        Flux<List<String>> flux = webClient.get().uri(BIO_URL).accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToFlux(new ParameterizedTypeReference<>() {
//                });

        // i) using block - blocks and uses the same http thread
//        List<String> dataList = mono.block();
        // the following is printed only after dataList is available
//        log.info("i) Client - Netty collected (blocked) {}", dataList);

        // ii) using subscribe - spins a async forkJoin thread
        mono.subscribe(data -> {
            log.info("ii) Client - Mono collected {}", data);
        });

        // iii) JSON to flux fails - org.springframework.core.codec.DecodingException: JSON decoding error: Cannot construct instance of `java.util.ArrayList`
//        flux.subscribe(data -> {
//            log.info("ii) Client - Netty collected {}", data);
//        });

        return "Check the client logs for 'Client - Mono initiated'";
    }

    @GetMapping("flux")
    @ResponseBody
    public String flux() {
        String uuid = UUID.randomUUID().toString();
        log.info("{} Flux client request entered controller", uuid);
        Flux<String> updatedFlux = webClient.get().uri(NIO_URL).accept(MediaType.TEXT_EVENT_STREAM)
                .exchangeToFlux(response -> response.bodyToFlux(String.class));
//        updatedFlux.log().buffer(1).subscribe(data -> {
//            log.info("Client - Flux collected {}", data);
//        });
        updatedFlux.subscribe(data -> {
            log.info("{} Client - Flux collected {}", uuid,  data);
        });
        return "Check the client logs for 'Client - Flux initiated'";


//        return updatedFlux.map(data -> {
//            log.info("{} Client - Netty collected {}", uuid,  data);
//            return data;
//        });

    }



    @GetMapping("/forkjoin")
    public String forkJoin() {
        String uuid = UUID.randomUUID().toString();
        log.info("{} ForkJoin client request entered controller", uuid);
        // Submit an asynchronous task to the ForkJoinPool
        forkJoinPool.submit(() -> {
            // Perform some time-consuming operation here
            log.info("ForkJoin Task executed by: {} {}", Thread.currentThread().getName(), uuid);
        });

        return "Check the client logs for 'Client - ForkJoin initiated'";
    }

}
