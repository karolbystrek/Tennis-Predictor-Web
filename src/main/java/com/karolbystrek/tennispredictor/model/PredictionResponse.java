package com.karolbystrek.tennispredictor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponse {

    private String player1Name;
    private String player2Name;
    private Float player1WinProbability;
    private Float player2WinProbability;
    private String winnerName;
    private Long winnerId;
    private Float confidence;
}
