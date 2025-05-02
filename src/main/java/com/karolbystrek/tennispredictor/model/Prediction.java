package com.karolbystrek.tennispredictor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "predictions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull(message = "Player 1 ID is required")
    @Column(name = "player_1_id", nullable = false)
    private Long player1Id;

    @NotNull(message = "Player 2 ID is required")
    @Column(name = "player_2_id", nullable = false)
    private Long player2Id;

    @DecimalMin(value = "0.0", message = "Player 1 probability must be non-negative")
    @DecimalMax(value = "1.0", message = "Player 1 probability must not exceed 1.0")
    @NotNull(message = "Player 1 probability is required")
    @Column(name = "player_1_win_probability", nullable = false)
    private Float player1WinProbability;

    @DecimalMin(value = "0.0", message = "Player 2 probability must be non-negative")
    @DecimalMax(value = "1.0", message = "Player 2 probability must not exceed 1.0")
    @NotNull(message = "Player 2 probability is required")
    @Column(name = "player_2_win_probability", nullable = false)
    private Float player2WinProbability;

    @DecimalMin(value = "0.0", message = "Confidence must be non-negative")
    @DecimalMax(value = "1.0", message = "Confidence must not exceed 1.0")
    @NotNull(message = "Confidence is required")
    @Column(name = "confidence", nullable = false)
    private Float confidence;

    @NotBlank(message = "Tournament level is required")
    @NotNull(message = "Tournament level is required")
    @Column(name = "tourney_level", nullable = false)
    private String tourneyLevel;

    @NotBlank(message = "Surface is required")
    @NotNull(message = "Surface is required")
    @Column(name = "surface", nullable = false)
    private String surface;

    @Min(value = 1, message = "Best of must be positive")
    @NotNull(message = "Best of is required")
    @Column(name = "best_of", nullable = false)
    private Integer bestOf;

    @NotBlank(message = "Round is required")
    @NotNull(message = "Round is required")
    @Column(name = "round", nullable = false)
    private String round;

    public Prediction(PredictionSaveRequest request) {
        this.username = request.getUsername();
        this.player1Id = request.getPlayer1Id();
        this.player2Id = request.getPlayer2Id();
        this.player1WinProbability = request.getPlayer1WinProbability();
        this.player2WinProbability = request.getPlayer2WinProbability();
        this.confidence = request.getConfidence();
        this.tourneyLevel = request.getTourneyLevel();
        this.surface = request.getSurface();
        this.bestOf = request.getBestOf();
        this.round = request.getRound();
    }
}
