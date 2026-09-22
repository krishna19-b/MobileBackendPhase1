package com.krishna.MobileBackendProjectPhase1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheMaintenanceJob {

    private static final Logger log =
            LoggerFactory.getLogger(CacheMaintenanceJob.class);

    private final CacheManager cacheManager;

    public CacheMaintenanceJob(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void maintainCache() {

        log.info("Cache maintenance started");

        Cache productsCache = cacheManager.getCache("products");
        Cache categoriesCache = cacheManager.getCache("categories");

        if (productsCache != null) {
            log.info("Products cache is available");
        }

        if (categoriesCache != null) {
            log.info("Categories cache is available");
        }

        log.info("Cache maintenance completed");
    }
}