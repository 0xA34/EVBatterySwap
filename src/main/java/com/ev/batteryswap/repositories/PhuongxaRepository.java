package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Phuongxa;
import com.ev.batteryswap.pojo.Quanhuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhuongxaRepository extends JpaRepository<Phuongxa, Integer> {
    List<Phuongxa> findByIdquanhuyen(Quanhuyen quanhuyen);
}
