package com.skillstorm.investments.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.investments.models.Investment;

@Repository
public interface InvestmentRepository extends MongoRepository<Investment, String> {

    public Optional<List<Investment>> findByEmail(String email); 
    public Optional<Investment> findByEmailAndSymbol(String email, String symbol);   
}
