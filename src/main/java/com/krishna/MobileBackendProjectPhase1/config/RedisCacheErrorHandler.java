package com.krishna.MobileBackendProjectPhase1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log =
            LoggerFactory.getLogger(RedisCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(
            RuntimeException exception,
            Cache cache,
            Object key
    ) {
        log.warn(
                "Redis GET failed. Cache: {}, Key: {}",
                cache.getName(),
                key,
                exception
        );
    }

    @Override
    public void handleCachePutError(
            RuntimeException exception,
            Cache cache,
            Object key,
            Object value
    ) {
        log.warn(
                "Redis PUT failed. Cache: {}, Key: {}",
                cache.getName(),
                key,
                exception
        );
    }

    @Override
    public void handleCacheEvictError(
            RuntimeException exception,
            Cache cache,
            Object key
    ) {
        log.warn(
                "Redis EVICT failed. Cache: {}, Key: {}",
                cache.getName(),
                key,
                exception
        );
    }

    @Override
    public void handleCacheClearError(
            RuntimeException exception,
            Cache cache
    ) {
        log.warn(
                "Redis CLEAR failed. Cache: {}",
                cache.getName(),
                exception
        );
    }
}