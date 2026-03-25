package com.bidly.coreservice.controller;

import com.bidly.coreservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalCoreController {

    private final WalletService walletService;

    @DeleteMapping("/{userId}")
    public void deleteUserData(@PathVariable("userId") String userId) {
        walletService.deleteUserData(userId);
    }
}
