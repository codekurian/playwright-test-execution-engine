package com.example.playwright;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaywrightTestHandler {

    @Autowired
    private PlaywrightTestService service;

    public String handleRunTest() throws Exception {
        return service.runLocalResourceTests();
    }
} 