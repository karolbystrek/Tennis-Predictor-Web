package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.model.Player;
import com.karolbystrek.tennispredictor.model.PlayerDTO;
import com.karolbystrek.tennispredictor.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;


    @Test
    void getAllPlayers_shouldReturnPlayerDTOList_whenPlayersExist() {
        Player player1 = new Player();
        player1.setPlayerId(1L);
        player1.setFirstName("Player One");
        Player player2 = new Player();
        player2.setPlayerId(1L);
        player2.setFirstName("Player Two");
        List<Player> mockPlayers = Arrays.asList(player1, player2);

        when(playerRepository.findAll()).thenReturn(mockPlayers);

        List<PlayerDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Player One", result.get(0).getFirstName());
        assertEquals("Player Two", result.get(1).getFirstName());

        verify(playerRepository, times(1)).findAll();
    }

    @Test
    void getAllPlayers_shouldReturnEmptyList_whenNoPlayersExist() {
        when(playerRepository.findAll()).thenReturn(Collections.emptyList());

        List<PlayerDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(playerRepository, times(1)).findAll();
    }
}
