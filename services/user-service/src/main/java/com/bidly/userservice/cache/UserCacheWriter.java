package com.bidly.userservice.cache;

import com.bidly.common.cache.CacheKeys;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UserCacheWriter {

    private final RedisTemplate<String, Object> redisTemplate;

    public void putPublicProfile(User user) {
        UserPublicDTO dto = UserPublicDTO.builder()
                .id(user.getKeycloakId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

        redisTemplate.opsForValue().set(CacheKeys.userPublicProfile(user.getKeycloakId()), dto);
    }

    public void evictPublicProfile(String userId) {
        redisTemplate.delete(CacheKeys.userPublicProfile(userId));
    }
}