package com.tus.individual.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Test
    void testHandleValidationException() {
        // Mock BindingResult and FieldError
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(
                Collections.singletonList(new FieldError("field", "email", "Invalid email format"))
        );

        // Create exception mock
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Call the handler method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<Map<String, String>> response = handler.handleValidationException(exception);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("email"));
        assertEquals("Invalid email format", response.getBody().get("email"));
    }
}
