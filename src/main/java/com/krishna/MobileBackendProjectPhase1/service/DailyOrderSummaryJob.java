package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DailyOrderSummaryJob {

    private static final Logger log =
            LoggerFactory.getLogger(DailyOrderSummaryJob.class);

    private final OrderRepository orderRepository;

    public DailyOrderSummaryJob(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyOrderSummary() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();

        long totalOrders =
                orderRepository.countByCreatedAtBetween(
                        startOfDay,
                        startOfNextDay
                );

        BigDecimal totalRevenue =
                orderRepository.sumTotalAmountByCreatedAtBetween(
                        startOfDay,
                        startOfNextDay
                );

        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        log.info(
                "Daily order summary. Date: {}, Orders: {}, Revenue: {}",
                today,
                totalOrders,
                totalRevenue
        );
    }
}