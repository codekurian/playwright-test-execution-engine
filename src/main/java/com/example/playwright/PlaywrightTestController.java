package com.example.playwright;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlaywrightTestController {

    @Autowired
    private PlaywrightTestHandler handler;

    @PostMapping("/run-test")
    public ResponseEntity<String> runTest() {
        try {
            String result = handler.handleRunTest();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
} 