package com.bidly.coreservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.coreservice.dto.wallet.WalletResponseDTO;
import com.bidly.coreservice.entity.Wallet;
import com.bidly.coreservice.mapper.WalletMapper;
import com.bidly.coreservice.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public ApiResponse<WalletResponseDTO> getMyWallet(String userId) {
        Wallet wallet = walletRepository.findById(userId).orElseGet(() ->
                walletRepository.save(Wallet.builder()
                        .userId(userId)
                        .availableBalance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .build())
        );

        return ApiResponse.success(WalletMapper.toResponseDto(wallet, getUserDto()), "Wallet retrieved successfully");
    }

    @Transactional
    public ApiResponse<WalletResponseDTO> recharge(String userId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Recharge amount must be greater than 0");
        }

        Wallet wallet = walletRepository.findById(userId).orElseGet(() ->
                Wallet.builder()
                        .userId(userId)
                        .availableBalance(BigDecimal.ZERO)
                        .reservedBalance(BigDecimal.ZERO)
                        .build()
        );

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        walletRepository.save(wallet);

        return ApiResponse.success(WalletMapper.toResponseDto(wallet, getUserDto()), "Wallet recharged successfully");
    }

    private static UserPublicDTO getUserDto() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        return UserPublicDTO.builder()
                .id(jwt.getSubject())
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .build();
    }
}