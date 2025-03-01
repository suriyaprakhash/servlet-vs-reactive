package com.suriyaprakhash.servlet.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequestMapping("client")
@RestController
public class ClientController {

    String BIO_URL = "http://localhost:8080/server/bio";
    WebClient webClient = WebClient.create();

    @GetMapping("bio")
    public String getBio() {
        log.info("bio client request entered controller");
        Mono<List<String>> retreivedMono = webClient.get().uri(BIO_URL).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });

        // i) using block
        List<String> dataList = retreivedMono.block();
        // the following is printed only after dataList is available
        log.info("i) Client - Netty collected (blocked) {}", dataList);

        // ii) using subscribe
        retreivedMono.subscribe(data -> {
            log.info("ii) Client - Netty collected {}", data);
        });

        return "Check the client logs for 'Client - Netty collected'";
    }

}