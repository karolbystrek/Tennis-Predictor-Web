package com.karolbystrek.tennispredictor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karolbystrek.tennispredictor.model.Prediction;

public interface PredictionRepository extends JpaRepository<Prediction, Long>{

}
