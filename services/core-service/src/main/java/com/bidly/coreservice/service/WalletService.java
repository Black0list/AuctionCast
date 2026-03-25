package com.bidly.coreservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.wallet.WalletResponseDTO;
import com.bidly.coreservice.entity.Wallet;
import com.bidly.coreservice.mapper.WalletMapper;
import com.bidly.coreservice.repository.AuctionRepository;
import com.bidly.coreservice.repository.BidRepository;
import com.bidly.coreservice.repository.OrderRepository;
import com.bidly.coreservice.repository.WalletHoldRepository;
import com.bidly.coreservice.repository.WalletRepository;
import com.bidly.coreservice.util.Util;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;
    private final WalletHoldRepository walletHoldRepository;

    public ApiResponse<WalletResponseDTO> getMyWallet(String userId) {
        Wallet wallet = walletRepository.findById(userId).orElseGet(() ->
                walletRepository.save(Wallet.builder()
                        .userId(userId)
                        .availableBalance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .build())
        );

        return ApiResponse.success(WalletMapper.toResponseDto(wallet, Util.getUserDto()), "Wallet retrieved successfully");
    }

    @Transactional
    public ApiResponse<WalletResponseDTO> recharge(String userId, BigDecimal amount) {
        addFunds(userId, amount);
        Wallet wallet = walletRepository.findById(userId).orElseThrow();
        return ApiResponse.success(WalletMapper.toResponseDto(wallet, Util.getUserDto()), "Wallet recharged successfully");
    }

    @Transactional
    public void addFunds(String userId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findById(userId).orElseGet(() ->
                walletRepository.save(Wallet.builder()
                        .userId(userId)
                        .availableBalance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .build())
        );

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Transactional
    public void deleteUserData(String userId) {
        bidRepository.deleteByBidderId(userId);
        walletHoldRepository.deleteByUserId(userId);
        orderRepository.deleteByBuyerId(userId);
        orderRepository.deleteBySellerId(userId);
        auctionRepository.deleteBySellerId(userId);
        walletRepository.deleteByUserId(userId);
    }
}