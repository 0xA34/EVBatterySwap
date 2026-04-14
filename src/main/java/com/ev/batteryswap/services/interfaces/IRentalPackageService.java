package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.RentalPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface IRentalPackageService {
    Page<RentalPackage> findAll(Pageable pageable);
    Map<String, Number> getStatistics();
    RentalPackage save(RentalPackage rentalPackage);
    void deleteById(Integer id);
    RentalPackage findById(Integer id);

    RentalPackage findRentalPackageByName(String name);

}
