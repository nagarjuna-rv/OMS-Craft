package com.intuit.product.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ControllerExceptionHandlerTest {

    @Mock
    private ResourceNotFoundException resourceNotFoundException;

    @Mock
    private BadRequestException badRequestException;

    @InjectMocks
    private ControllerExceptionHandler controllerExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        WebRequest request = mock(WebRequest.class);
        ErrorMessage expectedErrorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), Instant.now(), ex.getMessage(), request.getDescription(false));

        ResponseEntity<ErrorMessage> responseEntity = controllerExceptionHandler.resourceNotFoundException(ex, request);
        assertEquals(expectedErrorMessage.getStatusCode(), responseEntity.getStatusCode().value());
        assertEquals(expectedErrorMessage.getDescription(), Objects.requireNonNull(responseEntity.getBody()).getDescription());
        assertEquals(expectedErrorMessage.getMessage(), Objects.requireNonNull(responseEntity.getBody()).getMessage());
        assertTrue(expectedErrorMessage.getTimestamp().isBefore(Objects.requireNonNull(responseEntity.getBody()).getTimestamp()));

    }

    @Test
    void testBadRequestException() {
        BadRequestException ex = new BadRequestException("Bad request");
        WebRequest request = mock(WebRequest.class);
        ErrorMessage expectedErrorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), Instant.now(), ex.getMessage(), request.getDescription(false));

        ResponseEntity<ErrorMessage> responseEntity = controllerExceptionHandler.badRequestException(ex, request);

        assertEquals(expectedErrorMessage.getStatusCode(), responseEntity.getStatusCode().value());
    }

    @Test
    void testGlobalExceptionHandler() {
        Exception ex = new Exception("Internal server error");
        WebRequest request = mock(WebRequest.class);
        ErrorMessage expectedErrorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now(), ex.getMessage(), request.getDescription(false));

        ResponseEntity<ErrorMessage> responseEntity = controllerExceptionHandler.globalExceptionHandler(ex, request);

        assertEquals(expectedErrorMessage.getStatusCode(), responseEntity.getStatusCode().value());
    }
}
