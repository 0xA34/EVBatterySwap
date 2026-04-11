package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer>, JpaSpecificationExecutor<Station> {
    long countByStatus(String status);
}
