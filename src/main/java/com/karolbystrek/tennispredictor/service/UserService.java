package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.exceptions.UserAlreadyExistsException;
import com.karolbystrek.tennispredictor.model.CustomUser;
import com.karolbystrek.tennispredictor.model.RegisterRequest;
import com.karolbystrek.tennispredictor.repository.CustomUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final CustomUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(CustomUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(RegisterRequest request) throws UserAlreadyExistsException {
        log.info("Attempting to create user with username: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username {} already exists.", request.getUsername());
            throw new UserAlreadyExistsException("Account with this username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email {} already exists.", request.getEmail());
            throw new UserAlreadyExistsException("Account with this email already exists");
        }

        CustomUser newUser = new CustomUser(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(newUser);
        log.info("Successfully created user with username: {}", request.getUsername());
    }
}
