package com.skillstorm.financialaccounts.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.financialaccounts.models.Accounts;

@Repository
public interface AccountsRepository extends MongoRepository<Accounts, String> {
    public Optional<Accounts> findByEmail(String email);
}
