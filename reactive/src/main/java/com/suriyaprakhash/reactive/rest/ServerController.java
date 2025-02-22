package com.suriyaprakhash.reactive.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequestMapping("/server")

@RestController
public class ServerController {

    @Autowired
    private ListService listService;

    // NOTE:- MediaType.TEXT_EVENT_STREAM_VALUE is being used to stream the events as text
    @GetMapping(value = "nio", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getNio() {
        Flux<String> updatedFlux = listService.getListNio();
        log.info("Server Ctrl - Netty collected {}", updatedFlux.count());
//        return updatedFlux.delayElements(Duration.ofSeconds(1));
        return updatedFlux;
    }

}