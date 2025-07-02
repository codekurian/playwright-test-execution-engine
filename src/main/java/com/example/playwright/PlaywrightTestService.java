package com.example.playwright;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;

@Service
public class PlaywrightTestService {

    public String runLocalResourceTests() throws Exception {
        // Use a relative directory for Playwright test discovery
        File projectRoot = new File(System.getProperty("user.dir"));
        File tmpTestDir = new File(projectRoot, "pwtest-tmp/tests");
        tmpTestDir.mkdirs();
        File testFile = new File(tmpTestDir, "basic.spec.ts");
        // Copy the test file from resources to the tmpTestDir
        try (InputStream in = getClass().getResourceAsStream("/basic.spec.ts")) {
            if (in == null) throw new FileNotFoundException("Resource basic.spec.ts not found");
            Files.copy(in, testFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // Add minimal Playwright config to pwtest-tmp/
        File configFile = new File(projectRoot, "pwtest-tmp/playwright.config.ts");
        try (PrintWriter out = new PrintWriter(configFile)) {
            out.println("import { defineConfig } from '@playwright/test';");
            out.println("export default defineConfig({ testDir: './tests' });");
        }

        // Debug prints
        System.err.println("Running Playwright test with command: npx playwright test pwtest-tmp/tests/");
        System.err.println("Working directory: " + projectRoot.getAbsolutePath());

        // Run Playwright from project root, passing the relative test directory
        ProcessBuilder pb = new ProcessBuilder("npx", "playwright", "test", "pwtest-tmp/tests/");
        pb.directory(projectRoot);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        System.err.println("Playwright process exit code: " + exitCode);

        // Clean up (optional)
        // ...

        if (exitCode == 0) {
            return output.toString();
        } else {
            System.err.println("Playwright process output:\n" + output);
            throw new RuntimeException("Test failed:\n" + output);
        }
    }
} 