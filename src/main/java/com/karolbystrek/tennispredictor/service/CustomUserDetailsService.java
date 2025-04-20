package com.karolbystrek.tennispredictor.service;

import com.karolbystrek.tennispredictor.model.CustomUser;
import com.karolbystrek.tennispredictor.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final CustomUserRepository customUserRepository;

    public CustomUserDetailsService(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        log.debug("Attempting to load user by credential: {}", credential);
        Optional<CustomUser> userOptional = customUserRepository.findByUsername(credential)
                .or(() -> {
                    log.debug("User not found by username, trying email: {}", credential);
                    return customUserRepository.findByEmail(credential);
                });

        CustomUser user = userOptional
                .orElseThrow(() -> {
                    log.warn("User not found with credential: {}", credential);
                    return new UsernameNotFoundException("User not found with credential: " + credential);
                });

        log.info("User found with credential: {}", credential);
        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
