package com.bidly.catalogservice.client;

import ch.qos.logback.core.spi.ConfigurationEvent;
import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.security.feign.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "user-service", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @GetMapping("/users/{userId}/is-seller")
    ApiResponse<Boolean> isSeller(@PathVariable("userId") String userId);

    @GetMapping("/users/batch-public-profiles")
    ApiResponse<List<UserPublicDTO>> getBatchPublicProfiles(List<String> missing);
}
