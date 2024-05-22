package com.skillstorm.investments.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.investments.models.MockData;
import java.util.Optional;


@Repository
public interface MockDataRepository extends MongoRepository<MockData, String>{
    
    public Optional<MockData> findBySymbol(String symbol);
}
