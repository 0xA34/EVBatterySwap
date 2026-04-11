package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.SwapTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SwapTransactionRepository extends JpaRepository<SwapTransaction, Integer>, JpaSpecificationExecutor<SwapTransaction> {
    long countByPaymentStatus(String paymentStatus);

    @Query("SELECT FUNCTION('HOUR', s.createdAt) as hour, COUNT(s) as count " +
            "FROM SwapTransaction s " +
            "GROUP BY FUNCTION('HOUR', s.createdAt) " +
            "ORDER BY FUNCTION('HOUR', s.createdAt) ASC")
    List<Map<String, Object>> countTransactionsByHour();

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SwapTransaction s WHERE s.paymentStatus = 'COMPLETED'")
    BigDecimal getTotalRevenue();
}
