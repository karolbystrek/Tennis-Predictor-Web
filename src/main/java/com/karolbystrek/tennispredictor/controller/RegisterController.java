package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.exceptions.UserAlreadyExistsException;
import com.karolbystrek.tennispredictor.model.RegisterRequest;
import com.karolbystrek.tennispredictor.service.UserService;
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
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        log.info("POST /register - Attempting registration for username: {}", request.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("POST /register - Validation errors found for username {}: {}", request.getUsername(), bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }
        try {
            userService.createUser(request);
            log.info("Registration successful for username: {}", request.getUsername());
            redirectAttributes.addFlashAttribute("registered", true);
            return "redirect:/login?registered=true";
        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed for username {}: {}", request.getUsername(), e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("username")) {
                bindingResult.rejectValue("username", "error.user", e.getMessage());
            } else if (e.getMessage() != null && e.getMessage().contains("email")) {
                bindingResult.rejectValue("email", "error.user", e.getMessage());
            } else {
                bindingResult.reject("error.user", e.getMessage());
            }
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration for username: {}", request.getUsername(), e);
            redirectAttributes.addFlashAttribute("unexpectedError", "An unexpected error occurred during registration.");
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return "redirect:/register";
        }
    }
}
