package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Rental;
import com.ev.batteryswap.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Integer>, JpaSpecificationExecutor<Rental> {
    Rental findRentalByUser(User user);
}
