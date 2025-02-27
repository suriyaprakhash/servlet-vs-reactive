package com.suriyaprakhash.reactive.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RequestMapping("client")
@RestController
public class ClientController {


    String NIO_URL = "http://localhost:8081/server/nio";
    WebClient webClient = WebClient.create();

    @GetMapping("nio")
    @ResponseBody
    public String getNio() {
        log.info("nio client request entered controller");
        Flux<String> updatedFlux = webClient.get().uri(NIO_URL).accept(MediaType.TEXT_EVENT_STREAM)
                .exchangeToFlux(response -> {
                    return response.bodyToFlux(String.class);
                });
//        updatedFlux.log().buffer(1).subscribe(data -> {
//            log.info("Client - Netty collected {}", data);
//        });

        updatedFlux.subscribe(data -> {
            log.info("Client - Netty collected {}", data);
        });
        return "Check the client logs for 'Client - Netty collected'";
    }

}