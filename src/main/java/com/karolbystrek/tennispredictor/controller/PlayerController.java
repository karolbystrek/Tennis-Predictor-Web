package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.model.PlayerDTO;
import com.karolbystrek.tennispredictor.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        log.info("GET /players - Request received for all players");
        ResponseEntity<List<PlayerDTO>> response = playerService.getAllPlayers();
        log.info("GET /players - Responding with {} players", response.getBody() != null ? response.getBody().size() : 0);
        return response;
    }
}
