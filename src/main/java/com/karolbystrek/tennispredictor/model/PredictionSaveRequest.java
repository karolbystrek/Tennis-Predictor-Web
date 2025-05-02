package com.karolbystrek.tennispredictor.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PredictionSaveRequest {

    String username;

    @NotNull(message = "Player 1 ID is required")
    private Long player1Id;

    @NotBlank(message = "Player 1 Name is required")
    private String player1Name;

    @NotNull(message = "Player 2 ID is required")
    private Long player2Id;

    @NotBlank(message = "Player 2 Name is required")
    private String player2Name;

    @DecimalMin(value = "0.0", message = "Player 1 probability must be non-negative")
    @DecimalMax(value = "1.0", message = "Player 1 probability must not exceed 1.0")
    @NotNull(message = "Player 1 probability is required")
    private Float player1WinProbability;

    @DecimalMin(value = "0.0", message = "Player 2 probability must be non-negative")
    @DecimalMax(value = "1.0", message = "Player 2 probability must not exceed 1.0")
    @NotNull(message = "Player 2 probability is required")
    private Float player2WinProbability;

    @NotBlank(message = "Winner Name is required")
    private String winnerName;

    @DecimalMin(value = "0.0", message = "Confidence must be non-negative")
    @DecimalMax(value = "1.0", message = "Confidence must not exceed 1.0")
    @NotNull(message = "Confidence is required")
    private Float confidence;

    @NotBlank(message = "Tournament level is required")
    private String tourneyLevel;

    @NotBlank(message = "Surface is required")
    private String surface;

    @Min(value = 1, message = "Best of must be positive")
    @NotNull(message = "Best of is required")
    private Integer bestOf;

    @NotBlank(message = "Round is required")
    private String round;
}
