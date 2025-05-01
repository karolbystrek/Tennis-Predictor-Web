package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.model.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        log.info("GET /login - Displaying login page");
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest("", ""));
        }
        return "login";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        log.info("GET /logout - Displaying logout page");
        return "logout";
    }

}
