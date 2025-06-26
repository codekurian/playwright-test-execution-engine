package com.example.playwright;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlaywrightTestController {

    @Autowired
    private PlaywrightTestService service;

    @PostMapping("/run-test")
    public ResponseEntity<String> runTest(@RequestBody TestRequest request) {
        try {
            String result = service.downloadAndRunTest(request.getGithubFileUrl());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    public static class TestRequest {
        private String githubFileUrl;
        public String getGithubFileUrl() { return githubFileUrl; }
        public void setGithubFileUrl(String githubFileUrl) { this.githubFileUrl = githubFileUrl; }
    }
} 