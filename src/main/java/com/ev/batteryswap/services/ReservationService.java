package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.*;
import com.ev.batteryswap.repositories.*;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SwapTransactionRepository transactionRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;


    String generateTransactionId() {
        long number = ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L);
        return String.valueOf(number);
    }

    public Page<Reservation> getReservationsByStation(
        Integer stationId,
        String status,
        Pageable pageable
    ) {
        return reservationRepository.findByStationIdAndStatus(
            stationId,
            status,
            pageable
        );
    }

    @Transactional
    public void createReservation(
        Integer userId,
        Integer stationId,
        Integer batteryId,
        String pickupTimeStr
    ) {
        //validate
        User user = userRepository
            .findById(userId)
            .orElseThrow(() ->
                new RuntimeException("Người dùng không tồn tại")
            );

        if ("DRIVER".equals(user.getRole())) {
            if (reservationRepository.existsByUserIdAndStatus(userId, "PENDING")) {
                throw new RuntimeException("Bạn đã có một lịch hẹn hoặc đang đổi pin. Vui lòng hoàn thành giao dịch hiện tại trước khi đặt mới!");
            }
        }
        Station station = stationRepository
            .findById(stationId)
            .orElseThrow(() -> new RuntimeException("Trạm không tồn tại"));
        Battery battery = batteryRepository
            .findById(batteryId)
            .orElseThrow(() -> new RuntimeException("Pin không tồn tại"));

        if (!"AVAILABLE".equals(battery.getStatus())) {
            throw new RuntimeException(
                "Pin này đã bị người khác đặt hoặc không sẵn sàng!"
            );
        }

        // Kiểm tra và trừ tiền ví
        final BigDecimal RESERVATION_FEE = new BigDecimal("10000");
        BigDecimal batteryPrice = battery.getAmount() != null ? battery.getAmount() : BigDecimal.ZERO;
        BigDecimal totalCharge = batteryPrice.add(RESERVATION_FEE);

        if (user.getWalletBalance() == null || user.getWalletBalance().compareTo(totalCharge) < 0) {
            throw new RuntimeException(
                "Số dư ví không đủ. Cần " + totalCharge.toPlainString() + " VNĐ, hiện có " +
                (user.getWalletBalance() != null ? user.getWalletBalance().toPlainString() : "0") + " VNĐ."
            );
        }
        user.setWalletBalance(user.getWalletBalance().subtract(totalCharge));
        userRepository.save(user);

        //đổi thời gian từ input sang Instant
        Instant pickupTime = LocalDateTime.parse(pickupTimeStr)
            .atZone(ZoneId.systemDefault())
            .toInstant();

        //khách đặt pin rồi
        battery.setStatus("RESERVED");
        batteryRepository.save(battery);

        //tạo đối tượng để lưu db
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStation(station);
        reservation.setBattery(battery);
        reservation.setReservationTime(pickupTime);
        reservation.setExpiresAt(pickupTime.plus(30, ChronoUnit.MINUTES)); //hết hạn sau 30p so với giờ hẹn
        reservation.setStatus("PENDING");
        reservationRepository.save(reservation);


        // Lưu lịch sử giao dịch
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setUser(user);
        paymentInfo.setAmount(totalCharge);
        paymentInfo.setTransactionId(generateTransactionId());
        paymentInfo.setPaymentMethod("Thuê gói");
        paymentInfo.setNote(battery.getModel());

        paymentInfoRepository.save(paymentInfo);
    }

    @Transactional
    public void fulfillReservation(
        Integer reservationId,
        Integer batteryInId,
        String paymentMethod
    ) {
        //đơn đặt
        Reservation reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() ->
                new RuntimeException("Không tìm thấy đơn đặt lịch")
            );

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new RuntimeException(
                "Đơn đặt lịch này không hợp lệ hoặc đã xử lý xong."
            );
        }

        //pin cũ khách trả
        Battery batteryIn = batteryRepository
            .findById(batteryInId)
            .orElseThrow(() ->
                new RuntimeException("Không tìm thấy pin trả lại")
            );

        //pin mới (đã được giữ cho đơn này)
        Battery batteryOut = reservation.getBattery();

        SwapTransaction transaction = new SwapTransaction();
        transaction.setStation(reservation.getStation());
        transaction.setUser(reservation.getUser());
        transaction.setBatteryIn(batteryIn);
        transaction.setBatteryOut(batteryOut);
        transaction.setAmount(batteryOut.getAmount() != null ? batteryOut.getAmount() : java.math.BigDecimal.ZERO);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setPaymentStatus("COMPLETED"); // Giả sử thanh toán luôn tại quầy
        transaction.setNotes("Giao dịch từ đơn đặt lịch #" + reservationId);

        transactionRepository.save(transaction);

        //cập nhật trạng thái pin
        batteryIn.setStatus("CHARGING");
        batteryIn.setStation(reservation.getStation());
        batteryIn.setUser(null);
        batteryRepository.saveAndFlush(batteryIn);

        // pin giao cho khách
        batteryOut.setStatus("RENTED");
        batteryOut.setStation(null);
        batteryOut.setUser(reservation.getUser());
        batteryRepository.save(batteryOut);

        reservation.setStatus("COMPLETED");
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Integer reservationId) {
        Reservation reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt"));

        //trả pin về kho
        Battery battery = reservation.getBattery();
        battery.setStatus("AVAILABLE");
        batteryRepository.save(battery);

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    public Page<Reservation> filterReservations(
        Integer stationId,
        String status,
        String search,
        Pageable pageable
    ) {
        return reservationRepository.findAll(
            (Specification<Reservation>) (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // lọc theo Trạm
                if (stationId != null) {
                    predicates.add(
                        cb.equal(root.get("station").get("id"), stationId)
                    );
                }

                //lọc theo trạng thái (PENDING, COMPLETED, CANCELLED, EXPIRED)
                if (status != null && !status.isEmpty()) {
                    predicates.add(cb.equal(root.get("status"), status));
                }

                // tìm kiếm theo tên user hoặc mã pin
                if (search != null && !search.trim().isEmpty()) {
                    String likePattern = "%" + search.toLowerCase() + "%";
                    predicates.add(
                        cb.or(
                            cb.like(
                                cb.lower(root.get("user").get("username")),
                                likePattern
                            ),
                            cb.like(
                                cb.lower(root.get("user").get("fullName")),
                                likePattern
                            ),
                            cb.like(
                                cb.lower(
                                    root.get("battery").get("serialNumber")
                                ),
                                likePattern
                            )
                        )
                    );
                }

                //xếp theo thứ tự mới nhất
                query.orderBy(cb.desc(root.get("reservationTime")));

                return cb.and(predicates.toArray(new Predicate[0]));
            },
            pageable
        );
    }
}
