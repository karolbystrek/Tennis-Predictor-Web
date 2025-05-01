package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import com.karolbystrek.tennispredictor.model.PredictionSaveRequest;
import com.karolbystrek.tennispredictor.service.PredictionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        boolean responseFound = model.containsAttribute("predictionResponse");
        boolean requestFound = model.containsAttribute("predictionRequest");
        if (!responseFound) {
            log.warn("GET /prediction/result - Prediction response not found in model.");
            log.debug("Prediction Response: {}", model.getAttribute("predictionResponse"));
            return "redirect:/prediction";
        } else if (!requestFound) {
            log.warn("GET /prediction/result - Prediction request");
            log.debug("Prediction Request: {}", model.getAttribute("predictionRequest"));
            return "redirect:/prediction";
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
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.predictionRequest", bindingResult);
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction";
        }

        try {
            PredictionResponse response = predictionService.predict(request);
            log.info("Prediction successful for request: {}", request);
            redirectAttributes.addFlashAttribute("predictionResponse", response);
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction/result";
        } catch (PredictionServiceException e) {
            log.error("Prediction service error (Status {}): {}", e.getStatusCode(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction";
        } catch (Exception e) {
            log.error("Error during prediction for request: {}", request, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during prediction.");
            redirectAttributes.addFlashAttribute("predictionRequest", request);
            return "redirect:/prediction";
        }
    }

    @PostMapping("/save")
    public String savePredictionResult(@Valid @ModelAttribute("predictionResultRequest") PredictionSaveRequest request,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            request.setUsername(username);
            log.info("Attempting to save prediction for user: {}", username);
        } else {
            log.warn("User not authenticated. Cannot save prediction.");
            redirectAttributes.addFlashAttribute("saveError", "You must be logged in to save prediction.");
            return "redirect:/prediction/result";
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation errors while saving prediction: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("saveError", "Could not save prediction due to invalid data.");
            return "redirect:/prediction/result";
        }

        try {
            predictionService.save(request);
            log.info("Prediction request saved successfully for user: {}", request.getUsername());
            redirectAttributes.addFlashAttribute("saveSuccess", "Prediction saved successfully.");
        } catch (PredictionServiceException e) {
            log.error("Prediction service error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/prediction/result";
        } catch (Exception e) {
            log.error("Error during saving prediction result: {}", request, e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during saving prediction result.");
            return "redirect:/prediction/result";
        }
        return "redirect:/prediction/result";
    }

}
