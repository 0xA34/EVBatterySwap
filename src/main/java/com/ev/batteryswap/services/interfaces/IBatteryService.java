package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IBatteryService {
    Page<Battery> filterBatteries(Integer stationId, String status, String nameKeyword, Pageable pageable);
    List<Station> getAllStations();
    List<Battery> getAvailableBatteriesByStation(Integer stationId);
    void saveBattery(Battery battery);
    void deleteBattery(Integer id);
    Map<String, Long> getBatteryStatistics();
    Map<String, Long> getBatteryStatisticsForStation(Station station);
    Battery getBatteryById(Integer id);
    Battery getBatteryByCurrentUser(User user);


    boolean existsByIdAndStatusRented(Integer battery_rented_id); // tìm kiếm pin đang cho thuê
    boolean existsByIdAndStatusAvailable(Integer battery_empty_id); // tìm kiếm pin trống chưa cho thuê
    void updateStatusById(Integer id, String status); // cập nhật trạng thái pin bằng id
    void updateCurrentUser(User user,  Integer id);
}
