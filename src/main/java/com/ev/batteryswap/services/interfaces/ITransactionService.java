package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.SwapTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ITransactionService {
    Page<SwapTransaction> filterTransactions(Integer stationId, String paymentStatus, String search, Pageable pageable);
    Map<String, Long> getTransactionStatistics();
    void deleteTransaction(Integer transactionId);
    void createTransaction(SwapTransaction transaction);
    void updateTransactionDetails(SwapTransaction transaction);
    List<Map<String, Object>> getHourlySwapReport();
    SwapTransaction getTransactionById(Integer id);
    BigDecimal getTotalRevenue();
}
