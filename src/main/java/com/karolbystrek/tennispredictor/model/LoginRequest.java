package com.karolbystrek.tennispredictor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

    private String credential;

    private String password;
}
