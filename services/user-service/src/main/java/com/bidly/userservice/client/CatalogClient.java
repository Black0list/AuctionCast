package com.bidly.userservice.client;

import com.bidly.security.feign.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", configuration = FeignClientInterceptor.class)
public interface CatalogClient {

    @DeleteMapping("/internal/users/{userId}")
    void deleteUserProducts(@PathVariable("userId") String userId);
}
