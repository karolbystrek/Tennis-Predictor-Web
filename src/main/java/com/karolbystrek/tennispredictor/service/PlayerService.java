package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.model.Player;
import com.karolbystrek.tennispredictor.model.PlayerDTO;
import com.karolbystrek.tennispredictor.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Cacheable("playersCache")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        log.info("Fetching all players from repository (cacheable)");
        List<Player> players = playerRepository.findAll();
        List<PlayerDTO> playerDTOs = players.stream().map(PlayerDTO::new).toList();
        log.debug("Found {} players in repository", players.size());
        return ResponseEntity.ok(playerDTOs);
    }
}
