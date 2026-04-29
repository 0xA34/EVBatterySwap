package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.SwapTransaction;
import com.ev.batteryswap.repositories.BatteryRepository;
import com.ev.batteryswap.repositories.SwapTransactionRepository;
import com.ev.batteryswap.services.interfaces.ITransactionService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService implements ITransactionService {
    @Autowired
    private SwapTransactionRepository transactionRepository;
    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public Page<SwapTransaction> filterTransactions(Integer stationId, String paymentStatus, String search, Pageable pageable) {
        return transactionRepository.findAll((Specification<SwapTransaction>) (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("station", JoinType.LEFT);
                root.fetch("batteryIn", JoinType.LEFT);
                root.fetch("batteryOut", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();
            if (stationId != null) {
                predicates.add(cb.equal(root.get("station").get("id"), stationId));
            }
            if (paymentStatus != null && !paymentStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("paymentStatus"), paymentStatus));
            }
            if (search != null && !search.trim().isEmpty()) {
                try {
                    Integer transactionId = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("id"), transactionId));
                } catch (NumberFormatException e) {
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Map<String, Long> getTransactionStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_transactions", transactionRepository.count());
        stats.put("completed_transactions", transactionRepository.countByPaymentStatus("COMPLETED"));
        stats.put("pending_transactions", transactionRepository.countByPaymentStatus("PENDING"));
        stats.put("failed_transactions", transactionRepository.countByPaymentStatus("FAILED"));
        return stats;
    }

    @Override
    public void deleteTransaction(Integer transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    @Override
    public void createTransaction(SwapTransaction transaction) {
        // Lấy thông tin đầy đủ của 2 viên pin từ DB
        Battery batteryIn = batteryRepository.findById(transaction.getBatteryIn().getId())
                .orElse(null);
        if (batteryIn == null) {
            throw new RuntimeException("Pin trả vào không tồn tại!");
        }

        Battery batteryOut = batteryRepository.findById(transaction.getBatteryOut().getId())
                .orElse(null);
        if (batteryOut == null) {
            throw new RuntimeException("Pin lấy ra không tồn tại!");
        }

        // 1. Kiểm tra logic: Pin lấy ra phải đang "AVAILABLE"
        if (!"AVAILABLE".equals(batteryOut.getStatus())) {
            throw new RuntimeException("Logic lỗi: Pin lấy ra " +
                    "(" + batteryOut.getSerialNumber() + ") không ở trạng thái Sẵn sàng!");
        }

        // 2. Tự động cập nhật trạng thái của các viên pin
        batteryIn.setStatus("CHARGING");
        batteryIn.setStation(transaction.getStation()); // Cập nhật vị trí mới cho pin cũ về trạm hiện tại
        batteryIn.setUser(null);

        batteryOut.setStatus("RENTED"); // Pin mới giờ đã được cho thuê
        batteryOut.setStation(null);// Pin mới không còn ở trạm nào nữa
        batteryOut.setUser(transaction.getUser());

        // 3. Lưu lại trạng thái mới của 2 viên pin
        batteryRepository.saveAndFlush(batteryIn);
        batteryRepository.save(batteryOut);

        transactionRepository.save(transaction);
    }

    @Override
    public SwapTransaction getTransactionById(Integer id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Map<String, Object>> getHourlySwapReport() {
        return transactionRepository.countTransactionsByHour();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return transactionRepository.getTotalRevenue();
    }

    @Override
    public void updateTransactionDetails(SwapTransaction transaction) {
        // Hàm này chỉ lưu các thay đổi của bản thân giao dịch
        // (ví dụ: thay đổi paymentStatus, notes, amount)
        // và KHÔNG thực hiện logic thay đổi trạng thái pin.
        transactionRepository.save(transaction);
    }
}
