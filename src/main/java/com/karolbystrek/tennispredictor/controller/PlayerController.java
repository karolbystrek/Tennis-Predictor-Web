package com.karolbystrek.tennispredictor.controller;

import com.karolbystrek.tennispredictor.model.PlayerDTO;
import com.karolbystrek.tennispredictor.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<PlayerDTO> players = playerService.getAllPlayers();
        log.info("GET /players - Response with {} players", players.size());
        return ResponseEntity.ok(players);
    }
}
