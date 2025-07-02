package com.example.playwright;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = PlaywrightTestService.class)
public class PlaywrightTestServiceTest {

    // No dependencies to mock, so instantiate directly
    private final PlaywrightTestService service = new PlaywrightTestService();

    @Test
    void testRunLocalResourceTests() {
        try {
            String result = service.runLocalResourceTests();
            assertNotNull(result);
            assertTrue(result.contains("basic test")); // Should mention the test name in output
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            fail("Exception thrown: " + e.getMessage());
        }
    }
} 