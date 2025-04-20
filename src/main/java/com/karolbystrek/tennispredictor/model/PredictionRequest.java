package com.karolbystrek.tennispredictor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionRequest {

    private Long player1Id;
    private Long player2Id;
    private String surface;
    private String tourneyLevel;
    private Integer bestOf;
    private String round;
}
