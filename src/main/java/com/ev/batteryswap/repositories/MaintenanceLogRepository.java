package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.MaintenanceLog;
import com.ev.batteryswap.services.interfaces.IMaintenanceLogService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Integer> {

}
