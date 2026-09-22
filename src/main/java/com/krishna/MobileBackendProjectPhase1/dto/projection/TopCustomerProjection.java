package com.krishna.MobileBackendProjectPhase1.dto.projection;

import java.math.BigDecimal;

public interface TopCustomerProjection {

    Long getUserId();

    String getName();

    String getEmail();

    Long getTotalOrders();

    BigDecimal getTotalSpent();
}