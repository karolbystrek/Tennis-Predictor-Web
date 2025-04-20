package com.karolbystrek.tennispredictor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDTO {

    private Long playerId;
    private String firstName;
    private String lastName;
    private String ioc;

    public PlayerDTO(Player player) {
        this.playerId = player.getPlayerId();
        this.firstName = player.getFirstName();
        this.lastName = player.getLastName();
        this.ioc = player.getIoc();
    }
}
