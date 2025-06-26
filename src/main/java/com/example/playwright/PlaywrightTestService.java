package com.example.playwright;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

@Service
public class PlaywrightTestService {

    public String downloadAndRunTest(String filePathOrUrl) throws Exception {
        File tempDir = Files.createTempDirectory("pwtest").toFile();
        File testsDir = new File(tempDir, "tests");
        testsDir.mkdir();
        File testFile = new File(testsDir, "basic.spec.ts");

        File sourceFile = new File(filePathOrUrl);
        if (sourceFile.exists() && sourceFile.isFile()) {
            // Copy from local file path
            FileUtils.copyFile(sourceFile, testFile);
        } else {
            // Download from URL
            FileUtils.copyURLToFile(new URL(filePathOrUrl), testFile);
        }

        // Add a minimal Playwright config
        File configFile = new File(tempDir, "playwright.config.ts");
        try (PrintWriter out = new PrintWriter(configFile)) {
            out.println("import { defineConfig } from '@playwright/test';");
            out.println("export default defineConfig({ testDir: './tests' });");
        }

        // Symlink /app/node_modules to tempDir/node_modules
        File nodeModulesLink = new File(tempDir, "node_modules");
        File appNodeModules = new File("/app/node_modules");
        if (!nodeModulesLink.exists()) {
            java.nio.file.Files.createSymbolicLink(nodeModulesLink.toPath(), appNodeModules.toPath());
        }

        // Run the test using Node.js Playwright
        ProcessBuilder pb = new ProcessBuilder(
                "npx", "playwright", "test"
        );
        pb.directory(tempDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();

        // Clean up
        testFile.delete();
        configFile.delete();
        testsDir.delete();
        tempDir.delete();

        if (exitCode == 0) {
            return output.toString();
        } else {
            throw new RuntimeException("Test failed:\n" + output);
        }
    }
} 