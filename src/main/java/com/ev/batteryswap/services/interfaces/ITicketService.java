package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ITicketService {
    Page<SupportTicket> filterTickets(String status, String priority, String search, Pageable pageable);
    Map<String, Long> getTicketStatistics();
    SupportTicket save(SupportTicket ticket);
    void deleteById(Integer id);
    SupportTicket findById(Integer id);
}
