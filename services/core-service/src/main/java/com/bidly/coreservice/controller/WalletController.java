package com.bidly.coreservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.wallet.WalletRechargeDTO;
import com.bidly.coreservice.dto.wallet.WalletResponseDTO;
import com.bidly.coreservice.service.WalletService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public ApiResponse<WalletResponseDTO> me(@AuthenticationPrincipal Jwt jwt) {
        return walletService.getMyWallet(jwt.getSubject());
    }

    @PostMapping("/me/recharge")
    public ApiResponse<WalletResponseDTO> recharge(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody WalletRechargeDTO dto
    ) {
        return walletService.recharge(jwt.getSubject(), dto.getAmount());
    }
}