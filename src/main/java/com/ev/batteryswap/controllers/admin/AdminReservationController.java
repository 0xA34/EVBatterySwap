package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.Reservation;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.services.ReservationService;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private IBatteryService batteryService;

    @GetMapping
    public String listReservations(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(required = false) Integer stationId,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String search) {

        Page<Reservation> reservationPage = reservationService.filterReservations(
                stationId, status, search, PageRequest.of(page, 15)
        );

        List<Station> stations = batteryService.getAllStations();

        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("stations", stations);

        model.addAttribute("selectedStationId", stationId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);

        return "admin/reservations";
    }
}
