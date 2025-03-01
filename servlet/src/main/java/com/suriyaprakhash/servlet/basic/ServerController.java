package com.suriyaprakhash.servlet.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/server")

@RestController
public class ServerController {

    @Autowired
    private ListService listService;

    @GetMapping(value = "bio", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getBio() {
        List<String> updatedList = listService.getListBio();
        // the following is printed only after updateList is fully available
        log.info("Server Ctrl - Tomcat collected {}", updatedList);
        return updatedList;
    }

}