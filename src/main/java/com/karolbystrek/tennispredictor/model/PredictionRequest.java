package com.karolbystrek.tennispredictor.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PredictionRequest {

    @Min(value = 1, message = "Player IDs must be positive")
    @NotNull(message = "Player 1 ID is required")
    private Long player1Id;

    @Min(value = 1, message = "Player IDs must be positive")
    @NotNull(message = "Player 2 ID is required")
    private Long player2Id;

    @NotBlank(message = "Tournament ID is required")
    @NotNull(message = "Tournament ID is required")
    private String surface;

    @NotBlank(message = "Tournament ID is required")
    @NotNull(message = "Tournament ID is required")
    private String tourneyLevel;

    @Min(value = 1, message = "Best of must be positive")
    @NotBlank(message = "Tournament ID is required")
    @NotNull(message = "Tournament ID is required")
    private Integer bestOf;

    @NotBlank(message = "Tournament ID is required")
    @NotNull(message = "Tournament ID is required")
    private String round;
}
