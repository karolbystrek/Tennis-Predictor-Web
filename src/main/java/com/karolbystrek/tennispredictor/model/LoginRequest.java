package com.karolbystrek.tennispredictor.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Credential is required")
    private String credential;

    @NotBlank(message = "Password is required")
    private String password;
}
