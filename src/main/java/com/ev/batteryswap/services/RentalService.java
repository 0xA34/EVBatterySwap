package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.Rental;
import com.ev.batteryswap.repositories.RentalRepository;
import com.ev.batteryswap.services.interfaces.IRentalService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RentalService implements IRentalService {
    @Autowired
    private RentalRepository rentalRepository;

    @Override
    public Page<Rental> filterRentals(String search, Pageable pageable) {
        return rentalRepository.findAll((Specification<Rental>) (root, query, cb) -> {

            // Tối ưu hóa: Giống như TransactionService, chúng ta fetch các quan hệ
            // để tránh lỗi LazyInitializationException và N+1 query
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("user", JoinType.LEFT);
                root.fetch("packageField", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";

                // Tìm theo tên User hoặc tên Gói
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("user").get("fullName")), likePattern),
                        cb.like(cb.lower(root.get("packageField").get("name")), likePattern)
                ));
            }

            // Sắp xếp theo ngày tạo mới nhất
            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public void deleteRental(Integer id) {
        rentalRepository.deleteById(id);
    }
}
