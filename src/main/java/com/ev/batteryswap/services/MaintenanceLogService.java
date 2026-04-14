package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.MaintenanceLog;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.BatteryRepository;
import com.ev.batteryswap.repositories.MaintenanceLogRepository;
import com.ev.batteryswap.services.interfaces.IMaintenanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MaintenanceLogService implements IMaintenanceLogService {

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Override
    @Transactional
    public void reportIssue(Integer batteryId, String reason, User reporter) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy pin!"));

        battery.setStatus("MAINTENANCE");
        batteryRepository.save(battery);

        MaintenanceLog log = new MaintenanceLog();
        log.setBattery(battery);
        log.setDescription(reason);
        log.setMaintenanceType("REPORT_ISSUE");
        log.setTechnician(reporter.getFullName());
        log.setMaintenanceDate(LocalDate.now());
        log.setCost(BigDecimal.ZERO);

        maintenanceLogRepository.save(log);
    }
}
