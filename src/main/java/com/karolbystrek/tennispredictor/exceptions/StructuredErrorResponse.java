package com.karolbystrek.tennispredictor.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class StructuredErrorResponse {

    private Map<String, String> errors;
}
