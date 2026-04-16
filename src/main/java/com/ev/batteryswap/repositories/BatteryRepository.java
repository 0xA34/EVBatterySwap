package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BatteryRepository
    extends JpaRepository<Battery, Integer>, JpaSpecificationExecutor<Battery>
{
    long countByStatus(String status);

    // lấy dữ liệu pin cho cho station
    long countByStation(Station station);
    long countByStationAndStatus(Station station, String status);

    List<Battery> findByStationIdAndStatus(Integer stationId, String status);

    // tìm kiếm pin đang cho thuê
    @Query(
        "SELECT COUNT(b) > 0 FROM Battery b WHERE b.id = :id AND b.status = 'RENTED'"
    )
    boolean existsByIdAndStatusRented(Integer battery_rented_id);

    // tìm kiếm pin trống chưa cho thuê
    @Query(
        "SELECT COUNT(b) > 0 FROM Battery b WHERE b.id = :id AND b.status = 'AVAILABLE'"
    )
    boolean existsByIdAndStatusAvailable(Integer battery_empty_id);

    // cập nhật trạng thái pin bằng id
    @Transactional
    @Modifying
    @Query("UPDATE Battery b SET b.status = :status WHERE b.id = :id")
    void updateStatusById(Integer id, String status);

    @Transactional
    @Modifying
    @Query("UPDATE Battery b SET b.user = :user WHERE b.id = :id")
    int updateCurrentUser(User user, Integer id);

    Battery getBatteryByUser(User user);
}
