package com.bidly.coreservice.repository;

import com.bidly.coreservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    void deleteByUserId(String userId);
}