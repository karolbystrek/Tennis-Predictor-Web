package com.karolbystrek.tennispredictor.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karolbystrek.tennispredictor.exceptions.PlayerNotFoundException;
import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Prediction Service Tests")
public class PredictionServiceTest {

    private final static String TEST_API_KEY = "test-api-key";
    private final static String TEST_PREDICT_PATH = "/predict";

    private static MockWebServer mockWebServer;
    private static ObjectMapper objectMapper;

    private PredictionService predictionService;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        objectMapper = new ObjectMapper();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient.Builder webClientBuilder = WebClient.builder();
        predictionService = new PredictionService(webClientBuilder, baseUrl, TEST_API_KEY, TEST_PREDICT_PATH);
    }

    @Test
    @DisplayName("Should return PredictionResponse when API call is successful (200 OK)")
    void predict_shouldReturnPredictionResponse_whenApiCallIsSuccessful() throws JsonProcessingException, InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        PredictionResponse expectedResponse = new PredictionResponse("Player One", "Player Two", 0.7f, 0.3f, "Player One", 1L, 0.5f);
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(jsonResponse));

        PredictionResponse actualResponse = predictionService.predict(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getPlayer1Name(), actualResponse.getPlayer1Name());
        assertEquals(expectedResponse.getPlayer2Name(), actualResponse.getPlayer2Name());
        assertEquals(expectedResponse.getPlayer1WinProbability(), actualResponse.getPlayer1WinProbability());
        assertEquals(expectedResponse.getPlayer2WinProbability(), actualResponse.getPlayer2WinProbability());
        assertEquals(expectedResponse.getWinnerName(), actualResponse.getWinnerName());
        assertEquals(expectedResponse.getWinnerId(), actualResponse.getWinnerId());
        assertEquals(expectedResponse.getConfidence(), actualResponse.getConfidence());

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @DisplayName("Should throw PredictionServiceException when API returns Bad Request (400)")
    void predict_shouldThrowPredictionServiceException_whenApiReturnsBadRequest() throws InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        String errorBody = "{\"error\":\"Invalid input\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorBody));

        PredictionServiceException exception = assertThrows(
                PredictionServiceException.class,
                () -> predictionService.predict(request)
        );

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Invalid prediction request"));
        assertTrue(exception.getMessage().contains(errorBody));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @DisplayName("Should throw PlayerNotFoundException when API returns Not Found (404)")
    void predict_shouldThrowPredictionServiceException_whenApiReturnsNotFound() throws InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        String errorBody = "{\"error\":\"Player not found\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorBody));

        PlayerNotFoundException exception = assertThrows(
                PlayerNotFoundException.class,
                () -> predictionService.predict(request));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Player not found"));
        assertTrue(exception.getMessage().contains(errorBody));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @DisplayName("Should throw PredictionServiceException when API returns Unsupported Media Type (415)")
    void predict_shouldThrowPredictionServiceException_whenApiReturnsUnsupportedMediaType() throws InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        String errorBody = "{\"error\":\"Unsupported media type\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorBody));

        PredictionServiceException exception = assertThrows(
                PredictionServiceException.class,
                () -> predictionService.predict(request)
        );

        assertNotNull(exception);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Invalid content type"));
        assertTrue(exception.getMessage().contains(errorBody));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @DisplayName("Should throw PredictionServiceException when API returns Service Unavailable (503)")
    void predict_shouldThrowPredictionServiceException_whenApiReturnsServiceUnavailable() throws InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        String errorBody = "{\"error\":\"Service unavailable\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorBody));

        PredictionServiceException exception = assertThrows(
                PredictionServiceException.class,
                () -> predictionService.predict(request)
        );

        assertNotNull(exception);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Prediction service temporarily unavailable"));
        assertTrue(exception.getMessage().contains(errorBody));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @Test
    @DisplayName("Should throw PredictionServiceException when API returns Internal Server Error (500)")
    void predict_shouldThrowPredictionServiceException_whenApiReturnsInternalServerError() throws InterruptedException {
        PredictionRequest request = new PredictionRequest(1L, 2L, "Hard", "G", 3, "F");
        String errorBody = "{\"error\":\"Internal server error\"}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorBody));

        PredictionServiceException exception = assertThrows(
                PredictionServiceException.class,
                () -> predictionService.predict(request)
        );

        assertNotNull(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getStatusCode());
        assertTrue(exception.getMessage().contains("Prediction service internal server error"));
        assertTrue(exception.getMessage().contains(errorBody));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(TEST_PREDICT_PATH, recordedRequest.getPath());
        assertEquals(TEST_API_KEY, recordedRequest.getHeader("X-API-KEY"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE));
    }
}
