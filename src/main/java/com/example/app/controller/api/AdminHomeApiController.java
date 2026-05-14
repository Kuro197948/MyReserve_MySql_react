package com.example.app.controller.api;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.service.MemberService;
import com.example.app.service.ReservationRequestService;
import com.example.app.service.ReservationService;

@RestController
public class AdminHomeApiController {

    private final ReservationService reservationService;
    private final ReservationRequestService reservationRequestService;
    private final MemberService memberService;

    public AdminHomeApiController(
            ReservationService reservationService,
            ReservationRequestService reservationRequestService,
            MemberService memberService) {
        this.reservationService = reservationService;
        this.reservationRequestService = reservationRequestService;
        this.memberService = memberService;
    }

    @GetMapping("/api/admin/home/summary")
    public ResponseEntity<?> summary(HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        return ResponseEntity.ok(Map.of(
                "todayReservationCount", reservationService.countTodayReserved(),
                "pendingRequestCount", reservationRequestService.countPendingRequests(),
                "monthlyReservationCount", reservationService.countMonthlyReserved(),
                "memberCount", memberService.countAllMembers()
        ));
    }
}