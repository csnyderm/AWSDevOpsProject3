package com.skillstorm.financialplanner.repositories;

import com.skillstorm.financialplanner.models.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {

    public Optional<List<Goal>> findAllByEmail(String email);

}
