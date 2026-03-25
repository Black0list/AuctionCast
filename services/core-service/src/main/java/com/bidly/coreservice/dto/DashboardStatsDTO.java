package com.bidly.coreservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalUsers;
    private long activeProducts;
    private long ongoingAuctions;
    private BigDecimal totalRevenue;
}
