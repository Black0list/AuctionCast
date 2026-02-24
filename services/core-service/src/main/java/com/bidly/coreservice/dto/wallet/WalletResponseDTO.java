package com.bidly.coreservice.dto.wallet;

import com.bidly.common.dto.UserPublicDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponseDTO {
    private UserPublicDTO user;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}