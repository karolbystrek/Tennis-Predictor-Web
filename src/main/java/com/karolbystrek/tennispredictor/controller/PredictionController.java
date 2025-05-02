package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.exceptions.PredictionServiceException;
import com.karolbystrek.tennispredictor.model.PredictionRequest;
import com.karolbystrek.tennispredictor.model.PredictionResponse;
import com.karolbystrek.tennispredictor.model.PredictionSaveRequest;
import com.karolbystrek.tennispredictor.service.PredictionApiService;
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

    private final PredictionApiService predictionApiService;
    private final PredictionService predictionService;

    public PredictionController(PredictionApiService predictionApiService,
                                PredictionService predictionService) {
        this.predictionApiService = predictionApiService;
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
        PredictionResponse predictionResponse = (PredictionResponse) model.getAttribute("predictionResponse");
        PredictionRequest predictionRequest = (PredictionRequest) model.getAttribute("predictionRequest");
        if (predictionRequest == null || predictionResponse == null) {
            log.warn("GET /prediction/result - Prediction request or response not found in model. Redirecting.");
            log.debug("Prediction Response found: {}", predictionResponse != null);
            log.debug("Prediction Request found: {}", predictionRequest != null);
            return "redirect:/prediction";
        }

        if (!model.containsAttribute("predictionSaveRequest")) {
            PredictionSaveRequest predictionSaveRequest = new PredictionSaveRequest();
            predictionSaveRequest.setPlayer1Id(predictionRequest.getPlayer1Id());
            predictionSaveRequest.setPlayer2Id(predictionRequest.getPlayer2Id());
            predictionSaveRequest.setTourneyLevel(predictionRequest.getTourneyLevel());
            predictionSaveRequest.setSurface(predictionRequest.getSurface());
            predictionSaveRequest.setBestOf(predictionRequest.getBestOf());
            predictionSaveRequest.setRound(predictionRequest.getRound());

            predictionSaveRequest.setPlayer1WinProbability(predictionResponse.getPlayer1WinProbability());
            predictionSaveRequest.setPlayer2WinProbability(predictionResponse.getPlayer2WinProbability());
            predictionSaveRequest.setConfidence(predictionResponse.getConfidence());
            predictionSaveRequest.setPlayer1Name(predictionResponse.getPlayer1Name());
            predictionSaveRequest.setPlayer2Name(predictionResponse.getPlayer2Name());
            predictionSaveRequest.setWinnerName(predictionResponse.getWinnerName());

            model.addAttribute("predictionSaveRequest", predictionSaveRequest);
            log.debug("Using existing predictionSaveRequest and added to model.");
        } else {
            log.debug("Using existing predictionSaveRequest from model.");
        }

        if (model.containsAttribute("saveSuccess")) {
            log.info("Displaying prediction result page with save success message.");
        }
        if (model.containsAttribute("saveError")) {
            log.warn("Displaying prediction result page with save error message: {}", model.getAttribute("saveError"));
        }
        if (model.containsAttribute("error")) {
            log.warn("Displaying prediction result page with error message: {}", model.getAttribute("error"));
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
            PredictionResponse response = predictionApiService.predict(request);
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
    public String savePredictionResult(@Valid @ModelAttribute("predictionSaveRequest") PredictionSaveRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        PredictionResponse reconstructedResponse = new PredictionResponse();
        reconstructedResponse.setPlayer1Name(request.getPlayer1Name());
        reconstructedResponse.setPlayer2Name(request.getPlayer2Name());
        reconstructedResponse.setPlayer1WinProbability(request.getPlayer1WinProbability());
        reconstructedResponse.setPlayer2WinProbability(request.getPlayer2WinProbability());
        reconstructedResponse.setWinnerName(request.getWinnerName());
        reconstructedResponse.setConfidence(request.getConfidence());
        reconstructedResponse.setWinnerId(request.getPlayer1WinProbability() > request.getPlayer2WinProbability() ? request.getPlayer1Id() : request.getPlayer2Id());

        PredictionRequest reconstructedRequest = new PredictionRequest();
        reconstructedRequest.setPlayer1Id(request.getPlayer1Id());
        reconstructedRequest.setPlayer2Id(request.getPlayer2Id());
        reconstructedRequest.setSurface(request.getSurface());
        reconstructedRequest.setTourneyLevel(request.getTourneyLevel());
        reconstructedRequest.setBestOf(request.getBestOf());
        reconstructedRequest.setRound(request.getRound());
        if (userDetails != null) {
            String username = userDetails.getUsername();
            request.setUsername(username);
            log.info("Attempting to save prediction for user: {}", username);
        } else {
            log.warn("User not authenticated. Cannot save prediction.");
            redirectAttributes.addFlashAttribute("predictionRequest", reconstructedRequest);
            redirectAttributes.addFlashAttribute("predictionResponse", reconstructedResponse);
            redirectAttributes.addFlashAttribute("saveError", "You must be logged in to save prediction.");
            return "redirect:/prediction/result";
        }

        if (bindingResult.hasErrors()) {
            log.error("Validation errors while saving prediction: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.predictionSaveRequest", bindingResult);
            redirectAttributes.addFlashAttribute("predictionSaveRequest", request);
            redirectAttributes.addFlashAttribute("predictionRequest", reconstructedRequest);
            redirectAttributes.addFlashAttribute("predictionResponse", reconstructedResponse);
            redirectAttributes.addFlashAttribute("saveError", "Could not save prediction due to invalid data.");
            return "redirect:/prediction/result";
        }

        try {
            predictionService.save(request);
            log.info("Prediction request saved successfully for user: {}", request.getUsername());
            redirectAttributes.addFlashAttribute("predictionResponse", reconstructedResponse);
            redirectAttributes.addFlashAttribute("predictionRequest", reconstructedRequest);
            redirectAttributes.addFlashAttribute("saveSuccess", "Prediction saved successfully.");
        } catch (PredictionServiceException e) {
            log.error("Prediction service error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("predictionResponse", reconstructedResponse);
            redirectAttributes.addFlashAttribute("predictionRequest", reconstructedRequest);
            redirectAttributes.addFlashAttribute("predictionSaveRequest", request);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error during saving prediction result: {}", request, e);
            redirectAttributes.addFlashAttribute("predictionResponse", reconstructedResponse);
            redirectAttributes.addFlashAttribute("predictionRequest", reconstructedRequest);
            redirectAttributes.addFlashAttribute("predictionSaveRequest", request);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during saving prediction result.");
        }
        return "redirect:/prediction/result";
    }

}
