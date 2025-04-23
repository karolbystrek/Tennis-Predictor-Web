package com.karolbystrek.tennispredictor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;

    @NotBlank(message = "First name cannot be blank")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Date of birth cannot be null")
    @NotBlank(message = "Date of birth cannot be blank")
    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Country code cannot be blank")
    @Column(name = "ioc")
    private String ioc;

    @Min(value = 0, message = "Height must be a positive number")
    @Column(name = "height")
    private Integer height;

    @Min(value = 0, message = "Age must be a positive number")
    @Column(name = "age")
    private Integer age;

    @NotBlank(message = "Hand cannot be blank")
    @Column(name = "hand")
    private String hand;

    @Min(value = 0, message = "Rank must be a positive number")
    @Column(name = "rank")
    private Integer rank;

    @Min(value = 0, message = "Rank points must be a positive number")
    @Column(name = "rank_points")
    private Integer rankPoints;

    @Min(value = 0, message = "Elo must be a positive number")
    @Column(name = "elo")
    private Integer elo;

    @Min(value = 0, message = "Elo hard must be a positive number")
    @Column(name = "elo_hard")
    private Integer eloHard;

    @Min(value = 0, message = "Elo grass must be a positive number")
    @Column(name = "elo_grass")
    private Integer eloGrass;

    @Min(value = 0, message = "Elo carpet must be a positive number")
    @Column(name = "elo_carpet")
    private Integer eloCarpet;

    @Min(value = 0, message = "Elo clay must be a positive number")
    @Column(name = "elo_clay")
    private Integer eloClay;
}
