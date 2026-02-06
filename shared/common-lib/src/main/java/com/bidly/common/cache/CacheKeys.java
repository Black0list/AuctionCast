package com.bidly.common.cache;

public final class CacheKeys {
    private CacheKeys() {}

    public static String userPublicProfile(String userId) {
        return "user:public:" + userId;
    }
}