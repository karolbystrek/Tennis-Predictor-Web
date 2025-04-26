package com.karolbystrek.tennispredictor.controller;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.karolbystrek.tennispredictor.model.LoginRequest;

/**
 * Unit tests for the {@link LoginController}.
 */
@WebMvcTest(LoginController.class)
@DisplayName("Login Controller Tests")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /login - Should return login view with empty LoginRequest")
    void getLoginPage_shouldReturnLoginViewAndModel() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("loginRequest", instanceOf(LoginRequest.class)))
                .andExpect(model().attribute("loginRequest", hasProperty("credential", is(""))))
                .andExpect(model().attribute("loginRequest", hasProperty("password", is(""))));
    }
}
