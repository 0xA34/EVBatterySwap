package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IStationService {
    Page<Station> filterStations(String search, Pageable pageable);
    Page<Station> filterStationsByLocation(Integer tinhId, Integer huyenId, Integer xaId, Pageable pageable);
    Map<String, Long> getStationStatistics();
    Station saveStation(Station station);
    void deleteStation(Integer id);
    Station getStationById(Integer id);
    List<Station> getActiveStations();
}
