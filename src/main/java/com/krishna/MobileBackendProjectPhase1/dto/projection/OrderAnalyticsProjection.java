package com.krishna.MobileBackendProjectPhase1.dto.projection;

import java.math.BigDecimal;

public interface OrderAnalyticsProjection {

    Long getTotalOrders();

    BigDecimal getTotalRevenue();

    BigDecimal getAverageOrderValue();
}