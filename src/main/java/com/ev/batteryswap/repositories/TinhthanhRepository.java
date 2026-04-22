package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Tinhthanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinhthanhRepository extends JpaRepository<Tinhthanh, Integer> {
}
