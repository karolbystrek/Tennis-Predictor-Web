package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.exceptions.UserAlreadyExistsException;
import com.karolbystrek.tennispredictor.model.CustomUser;
import com.karolbystrek.tennispredictor.model.RegisterRequest;
import com.karolbystrek.tennispredictor.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private CustomUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest("user", "test@example.com", "userPassword");
    }

    @Test
    @DisplayName("Should create user successfully when username and email do not exist")
    void createUser_shouldCreateUser_whenUsernameAndEmailDoNoExist() {
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        ArgumentCaptor<CustomUser> userCaptor = ArgumentCaptor.forClass(CustomUser.class);

        userService.createUser(request);

        verify(userRepository, times(1)).existsByUsername(request.getUsername());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(userRepository, times(1)).save(userCaptor.capture());

        CustomUser user = userCaptor.getValue();
        assertNotNull(user);
        assertEquals(request.getUsername(), user.getUsername());
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when username already exists")
    void createUser_shouldThrowUserAlreadyExistsException_whenUsernameExists() {
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Account with this username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(request.getUsername());
        verify(userRepository, never()).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void createUser_shouldThrowUserAlreadyExistsException_whenEmailExists() {
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Account with this email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(request.getUsername());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
    }
}
