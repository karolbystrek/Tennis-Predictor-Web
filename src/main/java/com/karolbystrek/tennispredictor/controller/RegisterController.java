package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.exceptions.UserAlreadyExistsException;
import com.karolbystrek.tennispredictor.model.RegisterRequest;
import com.karolbystrek.tennispredictor.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegisterPage(Model model) {
        log.info("GET /register - Displaying registration page");
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest("", "", ""));
        }
        return "register";
    }

    @PostMapping
    public String register(@ModelAttribute("registerRequest") RegisterRequest request,
                           RedirectAttributes redirectAttributes) {
        log.info("POST /register - Attempting registration for username: {}", request.getUsername());
        try {
            userService.createUser(request);
            log.info("Registration successful for username: {}", request.getUsername());
            redirectAttributes.addFlashAttribute("registered", true); // Add success flag for login page
            return "redirect:/login?registered=true";
        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed for username {}: {}", request.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", request); // Keep form data on redirect
            return "redirect:/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration for username: {}", request.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred during registration.");
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }
    }
}
