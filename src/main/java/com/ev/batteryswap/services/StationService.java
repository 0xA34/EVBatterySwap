package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.repositories.StationRepository;
import com.ev.batteryswap.services.interfaces.IStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class StationService implements IStationService {
    @Autowired
    private StationRepository stationRepository;

    @Override
    public List<Station> getActiveStations() {
        return stationRepository.findByStatus("ACTIVE");
    }

    @Override
    public Page<Station> filterStations(String search, Pageable pageable) {
        return stationRepository.findAll((Specification<Station>) (root, query, cb) -> {
            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), likePattern),
                        cb.like(cb.lower(root.get("address")), likePattern)
                );
            }
            return cb.conjunction();
        }, pageable);
    }

    @Override
    public Page<Station> filterStationsByLocation(Integer tinhId, Integer huyenId, Integer xaId, Pageable pageable) {
        return stationRepository.findAll((Specification<Station>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (tinhId != null) {
                predicates.add(cb.equal(root.get("province").get("id"), tinhId));
            }
            if (huyenId != null) {
                predicates.add(cb.equal(root.get("quan").get("id"), huyenId));
            }
            if (xaId != null) {
                predicates.add(cb.equal(root.get("phuongxa").get("id"), xaId));
            }
            // Mặc định chỉ lấy trạm ACTIVE
            predicates.add(cb.equal(root.get("status"), "ACTIVE"));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Map<String, Long> getStationStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_stations", stationRepository.count());
        stats.put("active_stations", stationRepository.countByStatus("ACTIVE"));
        stats.put("maintenance_stations", stationRepository.countByStatus("MAINTENANCE"));
        stats.put("inactive_stations", stationRepository.countByStatus("INACTIVE"));
        return stats;
    }

    @Override
    public Station saveStation(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public Station getStationById(Integer id) {
        return stationRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteStation(Integer id) {
        stationRepository.deleteById(id);
    }
}
