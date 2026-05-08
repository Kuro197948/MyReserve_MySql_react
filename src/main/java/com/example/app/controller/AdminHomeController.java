package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.app.service.MemberService;
import com.example.app.service.ReservationRequestService;
import com.example.app.service.ReservationService;

@Controller
public class AdminHomeController {

    private final ReservationService reservationService;
    private final ReservationRequestService reservationRequestService;
    private final MemberService memberService;

    public AdminHomeController(
            ReservationService reservationService,
            ReservationRequestService reservationRequestService,
            MemberService memberService) {
        this.reservationService = reservationService;
        this.reservationRequestService = reservationRequestService;
        this.memberService = memberService;
    }

    @GetMapping("/admins/club/home")
    public String home(Model model) {

        int todayReservationCount = reservationService.countTodayReserved();
        int pendingRequestCount = reservationRequestService.countPendingRequests();
        int monthlyReservationCount = reservationService.countMonthlyReserved();
        int memberCount = memberService.countAllMembers();

        model.addAttribute("title", "管理者ホーム");
        model.addAttribute("todayReservationCount", todayReservationCount);
        model.addAttribute("pendingRequestCount", pendingRequestCount);
        model.addAttribute("monthlyReservationCount", monthlyReservationCount);
        model.addAttribute("memberCount", memberCount);

        return "admins/club/home";
    }
}