package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRentalService {
    Page<Rental> filterRentals(String search, Pageable pageable);
    void deleteRental(Integer id);
}
