package com.bidly.coreservice.mapper;

import com.bidly.common.dto.UserPublicDTO;
import com.bidly.coreservice.dto.wallet.WalletResponseDTO;
import com.bidly.coreservice.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public static WalletResponseDTO toResponseDto(Wallet wallet, UserPublicDTO userDto) {
        return WalletResponseDTO.builder()
                .user(userDto)
                .availableBalance(wallet.getAvailableBalance())
                .reservedBalance(wallet.getReservedBalance())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}