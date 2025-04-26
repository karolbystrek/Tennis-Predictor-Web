package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.model.CustomUser;
import com.karolbystrek.tennispredictor.model.Role;
import com.karolbystrek.tennispredictor.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Custom User Details Service Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private CustomUserRepository customUserRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private CustomUser testUser;
    private final String username = "testuser";
    private final String email = "test@example.com";
    private final String password = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = new CustomUser(username, email, password);
        testUser.setId(1L);
        testUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should load user successfully when found by username")
    void loadUserByUsername_shouldLoadUser_whenFoundByUsername() {
        when(customUserRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.USER.name())));

        verify(customUserRepository, times(1)).findByUsername(username);
        verify(customUserRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should load user successfully when found by email")
    void loadUserByUsername_shouldLoadUser_whenFoundByEmail() {
        when(customUserRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.USER.name())));

        verify(customUserRepository, times(1)).findByUsername(email);
        verify(customUserRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found by username or email")
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenNotFound() {
        String credential = "nonexistent";
        when(customUserRepository.findByUsername(credential)).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(credential)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(credential)
        );

        assertEquals("User not found with credential: " + credential, exception.getMessage());
        verify(customUserRepository, times(1)).findByUsername(credential);
        verify(customUserRepository, times(1)).findByEmail(credential);
    }
}
