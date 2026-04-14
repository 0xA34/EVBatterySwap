package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.User;

public interface IMaintenanceLogService {
    void reportIssue(Integer batteryId, String reason, User reporter);
}
