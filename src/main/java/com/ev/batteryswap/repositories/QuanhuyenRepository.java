package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Quanhuyen;
import com.ev.batteryswap.pojo.Tinhthanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuanhuyenRepository extends JpaRepository<Quanhuyen, Integer> {
    List<Quanhuyen> findByIdtinhthanh(Tinhthanh tinhthanh);
}
