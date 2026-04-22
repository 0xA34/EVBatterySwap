package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.Tinhthanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer>, JpaSpecificationExecutor<Station> {
    long countByStatus(String status);
    List<Station> findByStatus(String status);
    List<Station> findByProvinceAndStatus(Tinhthanh province, String status);
}
