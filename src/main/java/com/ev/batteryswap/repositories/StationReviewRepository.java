package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.StationReview;
import com.ev.batteryswap.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StationReviewRepository extends JpaRepository<StationReview, Integer>, JpaSpecificationExecutor<StationReview> {

    boolean existsByUserAndStation(User user, Station station);
}
