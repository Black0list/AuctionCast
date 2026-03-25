package com.bidly.userservice.client;

import com.bidly.security.feign.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "core-service", configuration = FeignClientInterceptor.class)
public interface CoreClient {

    @DeleteMapping("/internal/users/{userId}")
    void deleteUserData(@PathVariable("userId") String userId);
}
