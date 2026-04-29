package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>, JpaSpecificationExecutor<Reservation> {
    Page<Reservation> findByStationIdAndStatus(Integer stationId, String status, Pageable pageable);
    List<Reservation> findByUserIdOrderByReservationTimeDesc(Integer userId);
    boolean existsByUserIdAndStatus(Integer userId, String status);
}
