package com.krishna.MobileBackendProjectPhase1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificationServiceImpl  {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);


    @Async("notificationExecutor")
    public CompletableFuture<Void> sendOrderCreatedNotification(
            Long orderId,
            Long userId
    ) {

        log.info("Async notification started. OrderId: {}, UserId: {}", orderId, userId);

        try {

            Thread.sleep(1000);

            log.info(
                    "Order notification completed. OrderId: {}",
                    orderId
            );

            return CompletableFuture.completedFuture(null);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            log.error(
                    "Order notification interrupted. OrderId: {}",
                    orderId,
                    exception
            );

            return CompletableFuture.failedFuture(exception);
        }
    }
}