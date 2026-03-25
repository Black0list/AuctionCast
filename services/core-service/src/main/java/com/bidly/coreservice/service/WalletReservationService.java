package com.bidly.coreservice.service;

import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.entity.Wallet;
import com.bidly.coreservice.entity.WalletHold;
import com.bidly.coreservice.repository.WalletHoldRepository;
import com.bidly.coreservice.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class WalletReservationService {

    private final WalletRepository walletRepository;
    private final WalletHoldRepository walletHoldRepository;

    @Transactional
    public void reserveForLeader(String userId, Auction auction, BigDecimal newAmount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseGet(() -> walletRepository.save(Wallet.builder()
                        .userId(userId)
                        .availableBalance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .build()));

        WalletHold hold = walletHoldRepository.findByAuctionIdAndUserId(auction.getId(), userId).orElse(null);

        BigDecimal existing = (hold == null) ? BigDecimal.ZERO : hold.getAmount();
        BigDecimal delta = newAmount.subtract(existing);

        if (delta.signum() <= 0) {
            if (hold != null && newAmount.compareTo(existing) != 0) {
                hold.setAmount(newAmount);
                walletHoldRepository.save(hold);
            }
            return;
        }

        if (wallet.getAvailableBalance().compareTo(delta) < 0) {
            throw new IllegalArgumentException("INSUFFICIENT_FUNDS");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(delta));
        wallet.setReservedBalance(wallet.getReservedBalance().add(delta));
        walletRepository.save(wallet);

        if (hold == null) {
            hold = WalletHold.builder()
                    .auction(auction)
                    .userId(userId)
                    .amount(newAmount)
                    .build();
        } else {
            hold.setAmount(newAmount);
        }
        walletHoldRepository.save(hold);
    }

    @Transactional
    public void releaseForOutbid(String userId, Auction auction) {
        WalletHold hold = walletHoldRepository.findByAuctionIdAndUserId(auction.getId(), userId).orElse(null);
        if (hold == null) return;

        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        BigDecimal amount = hold.getAmount();
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet.setReservedBalance(wallet.getReservedBalance().subtract(amount));
        walletRepository.save(wallet);

        walletHoldRepository.delete(hold);
    }

    @Transactional
    public void chargeReservedFunds(String userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        if (wallet.getReservedBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient reserved funds");
        }

        wallet.setReservedBalance(wallet.getReservedBalance().subtract(amount));
        walletRepository.save(wallet);
    }
}