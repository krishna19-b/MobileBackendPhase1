package com.krishna.MobileBackendProjectPhase1.dto.projection;

import java.math.BigDecimal;

public interface BestSellingProductProjection {

    Long getProductId();

    String getProductName();

    Long getTotalQuantitySold();

    BigDecimal getTotalRevenue();
}