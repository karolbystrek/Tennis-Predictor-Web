package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import com.karolbystrek.tennispredictor.service.PredictionApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the {@link PredictionController}.
 */
@WebMvcTest(PredictionController.class)
@Import(PredictionControllerTest.TestSecurityConfig.class)
class PredictionControllerTest {

    private static final String PREDICTION_URL = "/prediction";
    private static final String PREDICTION_VIEW = "prediction";
    private static final String PREDICTION_REQUEST_ATTRIBUTE = "predictionRequest";
    private static final String PREDICTION_RESULT_URL = "/prediction/result";
    private static final String PREDICTION_RESULT_VIEW = "prediction-result";
    private static final String PREDICTION_RESPONSE_ATTRIBUTE = "predictionResponse";
    private static final String PREDICTION_ERROR_ATTRIBUTE = "error";
    private static final Long TEST_PLAYER1_ID = 104745L;
    private static final String TEST_PLAYER1_NAME = "Novak Djokovic";
    private static final Float TEST_PLAYER1_WIN_PROBABILITY = 0.65f;
    private static final Long TEST_PLAYER2_ID = 126774L;
    private static final String TEST_PLAYER2_NAME = "Jannik Sinner";
    private static final Float TEST_PLAYER2_WIN_PROBABILITY = 0.35f;
    private static final String TEST_SURFACE = "Hard";
    private static final String TEST_TOURNEY_LEVEL = "G";
    private static final Integer TEST_BEST_OF = 5;
    private static final String TEST_ROUND = "F";
    private static final Float TEST_CONFIDENCE = 0.8f;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PredictionApiService predictionApiService;
    private PredictionResponse validResponse;

    @BeforeEach
    void setUp() {
        validResponse = new PredictionResponse();
        validResponse.setPlayer1Name(TEST_PLAYER1_NAME);
        validResponse.setPlayer2Name(TEST_PLAYER2_NAME);
        validResponse.setPlayer1WinProbability(TEST_PLAYER1_WIN_PROBABILITY);
        validResponse.setPlayer2WinProbability(TEST_PLAYER2_WIN_PROBABILITY);
        validResponse.setWinnerId(TEST_PLAYER1_ID);
        validResponse.setWinnerName(TEST_PLAYER1_NAME);
        validResponse.setConfidence(TEST_CONFIDENCE);
    }

    @Test
    @DisplayName("GET /prediction - Should return prediction view with empty PredictionRequest")
    @WithAnonymousUser
    void getPredictionPage_ShouldReturnPredictionViewAndModel() throws Exception {
        mockMvc.perform(get(PREDICTION_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(PREDICTION_VIEW))
                .andExpect(model().attributeExists(PREDICTION_REQUEST_ATTRIBUTE))
                .andExpect(model().attribute(PREDICTION_REQUEST_ATTRIBUTE, instanceOf(PredictionRequest.class)))
                .andExpect(model().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player1Id", nullValue())))
                .andExpect(model().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player2Id", nullValue())))
                .andExpect(model().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("surface", nullValue())));
    }

    @Test
    @DisplayName("GET /prediction/result - Should return prediction-result view with empty PredictionResponse")
    @WithAnonymousUser
    void getPredictionResult_ShouldReturnPredictionResultViewAndModel() throws Exception {
        mockMvc.perform(get(PREDICTION_RESULT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(PREDICTION_RESULT_VIEW))
                .andExpect(model().attributeExists(PREDICTION_RESPONSE_ATTRIBUTE))
                .andExpect(model().attribute(PREDICTION_RESPONSE_ATTRIBUTE, instanceOf(PredictionResponse.class)))
                .andExpect(model().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("winnerName", nullValue())))
                .andExpect(model().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("winnerId", nullValue())));
    }

    @Test
    @DisplayName("POST /prediction - Should predict successfully and redirect to /prediction/result")
    @WithAnonymousUser
    void makePrediction_WithValidData_ShouldPredictAndRedirectToPredictionResult() throws Exception {
        when(predictionApiService.predict(any(PredictionRequest.class))).thenReturn(validResponse);

        ArgumentCaptor<PredictionRequest> requestCaptor = ArgumentCaptor.forClass(PredictionRequest.class);

        mockMvc.perform(post(PREDICTION_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("player1Id", String.valueOf(TEST_PLAYER1_ID))
                        .param("player2Id", String.valueOf(TEST_PLAYER2_ID))
                        .param("surface", TEST_SURFACE)
                        .param("tourneyLevel", TEST_TOURNEY_LEVEL)
                        .param("bestOf", String.valueOf(TEST_BEST_OF))
                        .param("round", TEST_ROUND)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PREDICTION_RESULT_URL))
                .andExpect(flash().attributeExists(PREDICTION_RESPONSE_ATTRIBUTE))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("player1Name", is(validResponse.getPlayer1Name()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("player2Name", is(validResponse.getPlayer2Name()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("player1WinProbability", is(validResponse.getPlayer1WinProbability()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("player2WinProbability", is(validResponse.getPlayer2WinProbability()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("winnerName", is(validResponse.getWinnerName()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("winnerId", is(validResponse.getWinnerId()))))
                .andExpect(flash().attribute(PREDICTION_RESPONSE_ATTRIBUTE, hasProperty("confidence", is(validResponse.getConfidence()))));

        verify(predictionApiService, times(1)).predict(requestCaptor.capture());

        PredictionRequest capturedRequest = requestCaptor.getValue();
        assertEquals(TEST_PLAYER1_ID, capturedRequest.getPlayer1Id());
        assertEquals(TEST_PLAYER2_ID, capturedRequest.getPlayer2Id());
        assertEquals(TEST_SURFACE, capturedRequest.getSurface());
    }

    @Test
    @DisplayName("POST /prediction - Should redirect back on validation errors")
    @WithAnonymousUser
    void makePrediction_WithInvalidData_ShouldRedirectToPredictionWithError() throws Exception {
        mockMvc.perform(post(PREDICTION_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("player2Id", String.valueOf(TEST_PLAYER2_ID))
                        .param("surface", TEST_SURFACE)
                        .param("tourneyLevel", TEST_TOURNEY_LEVEL)
                        .param("bestOf", String.valueOf(TEST_BEST_OF))
                        .param("round", TEST_ROUND)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PREDICTION_URL))
                .andExpect(flash().attributeExists(PREDICTION_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player1Id", nullValue())))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player2Id", is(TEST_PLAYER2_ID))))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("surface", is(TEST_SURFACE))));

        verify(predictionApiService, never()).predict(any(PredictionRequest.class));
    }

    @Test
    @DisplayName("POST /prediction - Should redirect back on prediction service error")
    @WithAnonymousUser
    void makePrediction_WhenServiceThrowsError_ShouldRedirectToPredictionWithError() throws Exception {
        String errorMessage = "External prediction API unavailable";
        when(predictionApiService.predict(any(PredictionRequest.class))).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post(PREDICTION_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("player1Id", String.valueOf(TEST_PLAYER1_ID))
                        .param("player2Id", String.valueOf(TEST_PLAYER2_ID))
                        .param("surface", TEST_SURFACE)
                        .param("tourneyLevel", TEST_TOURNEY_LEVEL)
                        .param("bestOf", String.valueOf(TEST_BEST_OF))
                        .param("round", TEST_ROUND)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PREDICTION_URL))
                .andExpect(flash().attributeExists(PREDICTION_ERROR_ATTRIBUTE))
                .andExpect(flash().attribute(PREDICTION_ERROR_ATTRIBUTE, is(errorMessage)))
                .andExpect(flash().attributeExists(PREDICTION_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player1Id", is(TEST_PLAYER1_ID))))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("player2Id", is(TEST_PLAYER2_ID))))
                .andExpect(flash().attribute(PREDICTION_REQUEST_ATTRIBUTE, hasProperty("surface", is(TEST_SURFACE))));

        verify(predictionApiService, times(1)).predict(any(PredictionRequest.class));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers(HttpMethod.GET, "/prediction", "/prediction/result").permitAll()
                            .requestMatchers(HttpMethod.POST, "/prediction").permitAll()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }
}
