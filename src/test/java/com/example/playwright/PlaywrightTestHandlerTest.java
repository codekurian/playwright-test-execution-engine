package com.example.playwright;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaywrightTestHandlerTest {

    @Mock
    private PlaywrightTestService service;

    @InjectMocks
    private PlaywrightTestHandler handler;

    @Test
    void testHandleRunTest() throws Exception {
        String expected = "Test output";
        when(service.runLocalResourceTests()).thenReturn(expected);

        String result = handler.handleRunTest();
        assertEquals(expected, result);
        verify(service, times(1)).runLocalResourceTests();
    }
} 