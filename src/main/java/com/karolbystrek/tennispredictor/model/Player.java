package com.karolbystrek.tennispredictor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Column(name = "ioc")
    private String ioc;

    @Column(name = "height")
    private Integer height;

    @Column(name = "age")
    private Integer age;

    @Column(name = "hand")
    private String hand;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "rank_points")
    private Integer rankPoints;

    @Column(name = "elo")
    private Integer elo;

    @Column(name = "elo_hard")
    private Integer eloHard;

    @Column(name = "elo_grass")
    private Integer eloGrass;

    @Column(name = "elo_carpet")
    private Integer eloCarpet;

    @Column(name = "elo_clay")
    private Integer eloClay;
}
