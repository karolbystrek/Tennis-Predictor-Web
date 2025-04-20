package com.karolbystrek.tennispredictor.repository;

import com.karolbystrek.tennispredictor.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<CustomUser> findByUsername(String username);

    Optional<CustomUser> findByEmail(String email);
}
