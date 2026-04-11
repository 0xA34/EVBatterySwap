package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.SupportTicket;
import com.ev.batteryswap.repositories.SupportTicketRepository;
import com.ev.batteryswap.services.interfaces.ITicketService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService implements ITicketService {
    @Autowired
    private SupportTicketRepository ticketRepository;

    @Override
    public Page<SupportTicket> filterTickets(String status, String priority, String search, Pageable pageable) {
        return ticketRepository.findAll((Specification<SupportTicket>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) predicates.add(cb.equal(root.get("status"), status));
            if (priority != null && !priority.isEmpty()) predicates.add(cb.equal(root.get("priority"), priority));
            if (search != null && !search.trim().isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("subject")), likePattern));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Map<String, Long> getTicketStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", ticketRepository.count());
        stats.put("open", ticketRepository.countByStatus("OPEN"));
        stats.put("in_progress", ticketRepository.countByStatus("IN_PROGRESS"));
        stats.put("urgent", ticketRepository.countByPriority("URGENT"));
        return stats;
    }

    @Override
    public SupportTicket findById(Integer id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    public SupportTicket save(SupportTicket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteById(Integer id) {
        ticketRepository.deleteById(id);
    }
}
