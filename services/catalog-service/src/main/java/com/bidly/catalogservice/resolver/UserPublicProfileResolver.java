package com.bidly.catalogservice.resolver;

import com.bidly.common.cache.CacheKeys;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.catalogservice.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserPublicProfileResolver {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserClient userClient;

    public Map<String, UserPublicDTO> resolve(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();

        List<String> distinctIds = userIds.stream().distinct().toList();
        List<String> keys = distinctIds.stream()
                .map(CacheKeys::userPublicProfile)
                .toList();

        List<Object> cached = redisTemplate.opsForValue().multiGet(keys);
        Map<String, UserPublicDTO> result = new HashMap<>();
        List<String> missing = new ArrayList<>();

        for (int i = 0; i < distinctIds.size(); i++) {
            Object obj = cached.get(i);
            String id = distinctIds.get(i);

            if (obj instanceof UserPublicDTO dto) {
                result.put(id, dto);
            } else {
                missing.add(id);
            }
        }

        if (!missing.isEmpty()) {
            List<UserPublicDTO> fetched = userClient.getBatchPublicProfiles(missing).getData();

            for (UserPublicDTO dto : fetched) {
                result.put(dto.getId(), dto);
                redisTemplate.opsForValue().set(CacheKeys.userPublicProfile(dto.getId()), dto);
            }
        }

        return result;
    }
}