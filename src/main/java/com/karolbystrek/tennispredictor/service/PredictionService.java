package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.exceptions.PlayerNotFoundException;
import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);
    private final WebClient client;
    private final String predictPath;

    public PredictionService(WebClient.Builder webClientBuilder,
                             @Value("${tennis.predictor.api.base-url}") String baseUrl,
                             @Value("${tennis.predictor.api.key}") String apiKey,
                             @Value("${tennis.predictor.api.predict-path}") String predictPath) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${PREDICTION_API_KEY}")) {
            log.error("API key 'tennis.predictor.api.key' is not configured properly in application.properties or environment variables.");
            throw new IllegalStateException("API key not configured for PredictionService");
        }
        if (baseUrl == null || baseUrl.isEmpty()) {
            log.error("API base URL 'tennis.predictor.api.base-url' is not configured properly in application.properties.");
            throw new IllegalStateException("API base URL not configured for PredictionService");
        }
        if (predictPath == null || predictPath.isEmpty()) {
            log.error("API predict path 'tennis.predictor.api.predict-path' is not configured properly in application.properties.");
            throw new IllegalStateException("API predict path not configured for PredictionService");
        }

        this.predictPath = predictPath;

        this.client = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("X-API-KEY", apiKey);
                })
                .build();
    }

    public PredictionResponse predict(PredictionRequest request) {
        Map<String, Object> requestBody = Map.of(
                "player1_id", request.getPlayer1Id(),
                "player2_id", request.getPlayer2Id(),
                "surface", request.getSurface(),
                "tourney_level", request.getTourneyLevel(),
                "best_of", request.getBestOf(),
                "round", request.getRound()
        );

        log.debug("Sending prediction request to {}{} with body: {}", client.mutate().build(), predictPath, requestBody);

        PredictionResponse response = client.post()
                .uri(this.predictPath)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                    return switch (e.getStatusCode()) {
                        case HttpStatus.BAD_REQUEST ->
                                Mono.error(new PredictionServiceException("Invalid prediction request: " + e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST.value()));
                        case HttpStatus.NOT_FOUND ->
                                Mono.error(new PlayerNotFoundException("Player not found: " + e.getResponseBodyAsString()));
                        case HttpStatus.UNSUPPORTED_MEDIA_TYPE ->
                                Mono.error(new PredictionServiceException("Invalid content type: " + e.getResponseBodyAsString(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
                        case HttpStatus.SERVICE_UNAVAILABLE ->
                                Mono.error(new PredictionServiceException("Prediction service temporarily unavailable: " + e.getResponseBodyAsString(), HttpStatus.SERVICE_UNAVAILABLE.value()));
                        default ->
                                Mono.error(new PredictionServiceException("Prediction service internal server error: " + e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
                    };
                })
                .onErrorResume(e -> !(e instanceof PredictionServiceException || e instanceof PlayerNotFoundException), e -> {
                    log.error("Unexpected error during prediction", e);
                    return Mono.error(new PredictionServiceException("Prediction service internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
                })
                .block();

        log.info("Prediction response: {}", response);
        return response;
    }
}
