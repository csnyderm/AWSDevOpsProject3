package com.skillstorm.financialplanner.repositories;

import com.skillstorm.financialplanner.models.Planner;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannerRepository extends MongoRepository<Planner, String> {

  public Optional<Planner> findByEmail(String email);

}
