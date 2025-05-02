package com.karolbystrek.tennispredictor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.Prediction;
import com.karolbystrek.tennispredictor.model.PredictionSaveRequest;
import com.karolbystrek.tennispredictor.repository.PredictionRepository;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);
    private final PredictionRepository predictionRepository;

    public PredictionService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    public void save(PredictionSaveRequest request) {
        if (request == null) {
            log.error("Prediction Save Request cannot be empty");
            throw new PredictionServiceException("Prediction Save Request cannot be empty");
        }
        Prediction prediction = new Prediction(request);
        predictionRepository.save(prediction);
        log.info("Successfully saved prediction result from user: {}", request.getUsername());
    }
}
