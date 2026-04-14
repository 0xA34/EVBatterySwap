package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.RentalPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalPackageRepository extends JpaRepository<RentalPackage, Integer>, JpaSpecificationExecutor<RentalPackage> {
    long countByStatus(String status);

    @Query("SELECT AVG(rp.price) FROM RentalPackage rp")
    Double getAveragePrice();

    @Query("SELECT AVG(rp.durationDays) FROM RentalPackage rp")
    Double getAverageDuration();

    RentalPackage findRentalPackageByName(String name);
}
