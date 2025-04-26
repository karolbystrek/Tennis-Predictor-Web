package com.karolbystrek.tennispredictor.controller;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import com.karolbystrek.tennispredictor.exceptions.UserAlreadyExistsException;
import com.karolbystrek.tennispredictor.model.RegisterRequest;
import com.karolbystrek.tennispredictor.service.UserService;

/**
 * Unit tests for the {@link RegisterController}.
 */
@WebMvcTest(RegisterController.class)
class RegisterControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/register").permitAll()
                    .anyRequest().authenticated()
                )
                .csrf(Customizer.withDefaults());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static final String REGISTER_VIEW = "register";
    private static final String LOGIN_URL_SUCCESS = "/login?registered=true";
    private static final String REGISTER_URL = "/register";
    private static final String REGISTER_REQUEST_ATTRIBUTE = "registerRequest";
    private static final String BINDING_RESULT_ATTRIBUTE = "org.springframework.validation.BindingResult.registerRequest";

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "passwordPassword";
    private static final String INVALID_EMAIL = "not-an-email";
    private static final String INVALID_PASSWORD = "invalid";

    @Test
    @DisplayName("GET /register - Should return register view with empty RegisterRequest")
    @WithAnonymousUser
    void getRegisterPage_ShouldReturnRegisterViewAndModel() throws Exception {
        mockMvc.perform(get(REGISTER_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTER_VIEW))
                .andExpect(model().attributeExists(REGISTER_REQUEST_ATTRIBUTE))
                .andExpect(model().attribute(REGISTER_REQUEST_ATTRIBUTE, instanceOf(RegisterRequest.class)))
                .andExpect(model().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("username", is(""))))
                .andExpect(model().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("email", is(""))))
                .andExpect(model().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("password", is(""))));
    }

    @Test
    @DisplayName("POST /register - Should register user successfully and redirect to login")
    @WithAnonymousUser
    void register_WithValidData_ShouldRegisterUserAndRedirectToLogin() throws Exception {
        doNothing().when(userService).createUser(any(RegisterRequest.class));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_USERNAME)
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL_SUCCESS))
                .andExpect(flash().attribute("registered", is(true)));

        verify(userService, times(1)).createUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /register - Should redirect back to register on validation error")
    @WithAnonymousUser
    void register_WithInvalidData_ShouldRedirectToRegisterWithErrors() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("email", INVALID_EMAIL)
                        .param("password", INVALID_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REGISTER_URL))
                .andExpect(flash().attributeExists(BINDING_RESULT_ATTRIBUTE))
                .andExpect(flash().attributeExists(REGISTER_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("username" , is(""))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("email" , is(INVALID_EMAIL))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("password" , is(INVALID_PASSWORD))));

        verify(userService, never()).createUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /register - Should redirect back to register if username already exists")
    @WithAnonymousUser
    void register_WithUsernameExists_ShouldRedirectToRegisterWithError() throws Exception {
        String errorMessage = "Username 'testuser' already exists";
        doThrow(new UserAlreadyExistsException(errorMessage)).when(userService).createUser(any(RegisterRequest.class));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_USERNAME)
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REGISTER_URL))
                .andExpect(flash().attributeExists(BINDING_RESULT_ATTRIBUTE))
                .andExpect(flash().attributeExists(REGISTER_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("username" , is(TEST_USERNAME))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("email" , is(TEST_EMAIL))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("password" , is(TEST_PASSWORD))));
    }

    @Test
    @DisplayName("POST /register - Should redirect back to register if email already exists")
    @WithAnonymousUser
    void register_WhenEmailExists_ShouldRedirectToRegisterWithError() throws Exception {
        String errorMessage = "Email 'test@example.com' already exists";
        doThrow(new UserAlreadyExistsException(errorMessage)).when(userService).createUser(any(RegisterRequest.class));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_USERNAME)
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REGISTER_URL))
                .andExpect(flash().attributeExists(BINDING_RESULT_ATTRIBUTE))
                .andExpect(flash().attributeExists(REGISTER_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("username" , is(TEST_USERNAME))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("email" , is(TEST_EMAIL))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("password" , is(TEST_PASSWORD))));


        verify(userService, times(1)).createUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /register - Should redirect back to register on unexpected service error")
    @WithAnonymousUser
    void register_WhenUnexpectedErrorOccurs_ShouldRedirectToRegisterWithError() throws Exception {
        String errorMessage = "Unexpected database error";
        doThrow(new RuntimeException(errorMessage)).when(userService).createUser(any(RegisterRequest.class));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", TEST_USERNAME)
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REGISTER_URL))
                .andExpect(flash().attributeExists("unexpectedError"))
                .andExpect(flash().attribute("unexpectedError", "An unexpected error occurred during registration."))
                .andExpect(flash().attributeExists(REGISTER_REQUEST_ATTRIBUTE))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("username" , is(TEST_USERNAME))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("email" , is(TEST_EMAIL))))
                .andExpect(flash().attribute(REGISTER_REQUEST_ATTRIBUTE, hasProperty("password" , is(TEST_PASSWORD))));

        verify(userService, times(1)).createUser(any(RegisterRequest.class));
    }
}
