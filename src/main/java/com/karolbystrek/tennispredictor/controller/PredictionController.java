package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import com.karolbystrek.tennispredictor.service.PredictionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/prediction")
public class PredictionController {

    private static final Logger log = LoggerFactory.getLogger(PredictionController.class);
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public String getPredictionPage(Model model) {
        log.info("GET /prediction - Displaying prediction form");
        if (!model.containsAttribute("predictionRequest")) {
            model.addAttribute("predictionRequest", new PredictionRequest());
        }
        return "prediction";
    }

    @GetMapping("/result")
    public String getPredictionResult(Model model) {
        if (!model.containsAttribute("predictionResponse")) {
            log.warn("GET /prediction/result - Prediction response not found in model, redirecting might have occurred without data.");
            model.addAttribute("predictionResponse", new PredictionResponse());
        } else {
            log.info("GET /prediction/result - Displaying prediction result");
        }
        return "prediction-result";
    }

    @PostMapping
    public String makePrediction(@Valid @ModelAttribute("predictionRequest") PredictionRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        log.info("POST /prediction - Received prediction request: {}", request);
        if (bindingResult.hasErrors()) {
            log.warn("POST /prediction - Validation errors found: {}", bindingResult.getAllErrors());
            return "redirect:/prediction";
        }

        try {
            PredictionResponse response = predictionService.predict(request);
            log.info("Prediction successful for request: {}", request);
            redirectAttributes.addFlashAttribute("predictionResponse", response);
            return "redirect:/prediction/result";
        } catch (PredictionServiceException e) {
            log.error("Prediction service error (Status {}): {}", e.getStatusCode(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction";
        } catch (Exception e) {
            log.error("Error during prediction for request: {}", request, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction";
        }
    }
}
