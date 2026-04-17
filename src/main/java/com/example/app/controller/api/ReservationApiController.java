package com.example.app.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Reservation;
import com.example.app.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member/reservations")
@RequiredArgsConstructor
public class ReservationApiController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getMyReservations(HttpSession session) {
        Object memberIdObj = session.getAttribute("loginMemberId");

        if (memberIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ログインが必要です");
        }

        Integer memberId = ((Number) memberIdObj).intValue();

        List<Reservation> reservationList = reservationService.findByMemberId(memberId);

        List<ReservationResponse> responseList = reservationList.stream()
                .map(reservation -> {
                    ReservationResponse response = new ReservationResponse();
                    response.setId(reservation.getId());
                    response.setPeopleCount(reservation.getPeopleCount());
                    response.setRepresentativeName(reservation.getRepresentativeName());
                    response.setPhoneNumber(reservation.getPhoneNumber());
                    response.setCourseName(reservation.getCourseName());
                    response.setRemarks(reservation.getRemarks());
                    response.setStatus(reservation.getStatus());
                    response.setReservationDate(reservation.getReservationDate());
                    response.setCreatedAt(reservation.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}