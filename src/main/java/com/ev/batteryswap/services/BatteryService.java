package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.BatteryRepository;
import com.ev.batteryswap.repositories.StationRepository;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BatteryService implements IBatteryService {

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private StationRepository stationRepository;

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public List<Battery> getAvailableBatteriesByStation(Integer stationId) {
        return batteryRepository.findByStationIdAndStatus(stationId, "AVAILABLE");
    }

    @Override
    public Page<Battery> filterBatteries(
        Integer stationId,
        String status,
        String nameKeyword,
        Pageable pageable
    ) {
        return batteryRepository.findAll(
            (Specification<Battery>) (root, query, criteriaBuilder) -> {
                if (
                    query.getResultType() != Long.class &&
                    query.getResultType() != long.class
                ) {
                    root.fetch("station", JoinType.LEFT);

                    if ("RENTED".equals(status)) {
                        root.fetch("user", JoinType.LEFT);
                    }
                }

                List<Predicate> predicates = new ArrayList<>();
                // Điều kiện 1: Lọc theo Trạm (stationId)
                if (stationId != null) {
                    predicates.add(
                        criteriaBuilder.equal(
                            root.get("station").get("id"),
                            stationId
                        )
                    );
                }
                // Điều kiện 2: Lọc theo Trạng thái (status)
                if (status != null && !status.isEmpty()) {
                    predicates.add(
                        criteriaBuilder.equal(root.get("status"), status)
                    );
                }
                // Điều kiện 3: Lọc theo Tên pin (serialNumber)
                if (nameKeyword != null && !nameKeyword.trim().isEmpty()) {
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("serialNumber")),
                            "%" + nameKeyword.toLowerCase() + "%"
                        )
                    );
                }
                return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
                );
            },
            pageable
        );
    }

    // get thông tin PIN bằng id
    @Override
    public Battery getBatteryById(Integer id) {
        return batteryRepository.findById(id).orElse(null);
    }

    // thêm thông tin giá trị Pin mới
    @Override
    public void saveBattery(Battery battery) {
        batteryRepository.save(battery);
    }

    // xóa Pin bằng id
    @Override
    public void deleteBattery(Integer id) {
        batteryRepository.deleteById(id);
    }

    @Override
    public Battery getBatteryByCurrentUser(User user) {
        return batteryRepository.getBatteryByUser(user);
    }

    @Override
    public Map<String, Long> getBatteryStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", batteryRepository.count());
        stats.put("available", batteryRepository.countByStatus("AVAILABLE"));
        stats.put("charging", batteryRepository.countByStatus("CHARGING"));
        stats.put(
            "maintenance",
            batteryRepository.countByStatus("MAINTENANCE")
        );
        stats.put("empty", batteryRepository.countByStatus("EMPTY"));
        stats.put("rented", batteryRepository.countByStatus("RENTED"));
        return stats;
    }

    //Dùng để đưa thông tin cho phần station
    @Override
    public Map<String, Long> getBatteryStatisticsForStation(Station station) {
        Map<String, Long> stats = new HashMap<>();
        if (station == null) return stats;

        stats.put("total", batteryRepository.countByStation(station));
        stats.put(
            "available",
            batteryRepository.countByStationAndStatus(station, "AVAILABLE")
        );
        stats.put(
            "charging",
            batteryRepository.countByStationAndStatus(station, "CHARGING")
        );
        stats.put(
            "maintenance",
            batteryRepository.countByStationAndStatus(station, "MAINTENANCE")
        );
        stats.put(
            "empty",
            batteryRepository.countByStationAndStatus(station, "EMPTY")
        );
        // rent rồi thì không còn ở trạm nữa
        stats.put(
            "rented",
            batteryRepository.countByStationAndStatus(station, "RENTED")
        );
        return stats;
    }

    // tìm kiếm pin đang cho thuê
    public boolean existsByIdAndStatusRented(Integer battery_rented_id) {
        return batteryRepository.existsByIdAndStatusRented(battery_rented_id);
    }

    // tìm kiếm pin trống chưa cho thuê
    public boolean existsByIdAndStatusAvailable(Integer battery_available_id) {
        return batteryRepository.existsByIdAndStatusAvailable(
            battery_available_id
        );
    }

    // cập nhật trạng thái pin bằng id
    public void updateStatusById(Integer id, String status) {
        batteryRepository.updateStatusById(id, status);
    }

    @Override
    public void updateCurrentUser(User user, Integer id) {
        batteryRepository.updateCurrentUser(user, id);
    }
}
