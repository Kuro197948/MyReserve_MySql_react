package com.example.app.controller.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Reservation;
import com.example.app.service.ReservationRequestService;
import com.example.app.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationApiController {

    private final ReservationService reservationService;
    private final ReservationRequestService reservationRequestService;

    @GetMapping
    public ResponseEntity<List<AdminReservationResponse>> list(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<Integer, String> pendingRequestTypeMap =
                reservationRequestService.findPendingRequestViews()
                        .stream()
                        .collect(Collectors.toMap(
                                request -> request.getReservationId(),
                                request -> request.getRequestType(),
                                (first, second) -> first
                        ));

        List<AdminReservationResponse> responseList =
                reservationService.selectAll()
                        .stream()
                        .map(reservation -> toResponse(reservation, pendingRequestTypeMap))
                        .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AdminReservationResponse>> history(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<AdminReservationResponse> responseList =
                reservationService.selectHistory()
                        .stream()
                        .map(reservation -> toResponse(reservation, Map.of()))
                        .toList();

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminReservationResponse> detail(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Reservation reservation = reservationService.selectById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDetailResponse(reservation));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(
            @PathVariable Integer id,
            @RequestBody AdminReservationUpdateRequest request,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Reservation reservation = reservationService.selectById(id);

        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }

        reservation.setRepresentativeName(request.representativeName());
        reservation.setReservationDate(request.reservationDate());
        reservation.setPeopleCount(request.peopleCount());
        reservation.setPhoneNumber(request.phoneNumber());
        reservation.setCourseName(request.courseName());

        reservationService.update(reservation);

        Reservation updatedReservation = reservationService.selectById(id);

        return ResponseEntity.ok(toDetailResponse(updatedReservation));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reservationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> hideHistory(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reservationService.hideFromAdmin(id);

        return ResponseEntity.noContent().build();
    }
    
    public record AdminReservationUpdateRequest(
            LocalDateTime reservationDate,
            Integer peopleCount,
            String representativeName,
            String phoneNumber,
            String courseName
    ) {
    }

    private AdminReservationResponse toResponse(
            Reservation reservation,
            Map<Integer, String> pendingRequestTypeMap) {

        String pendingRequestType = pendingRequestTypeMap.get(reservation.getId());

        boolean hasPendingCancelRequest = "CANCEL".equals(pendingRequestType);
        boolean hasPendingChangeRequest = "CHANGE".equals(pendingRequestType);
        boolean hasPendingRequest = pendingRequestType != null;

        return buildResponse(
                reservation,
                pendingRequestType,
                hasPendingCancelRequest,
                hasPendingChangeRequest,
                hasPendingRequest
        );
    }

    private AdminReservationResponse toDetailResponse(Reservation reservation) {
        boolean hasPendingCancelRequest = false;
        boolean hasPendingChangeRequest = false;
        boolean hasPendingRequest = false;

        if (reservation.getMemberId() != null) {
            hasPendingCancelRequest =
                    reservationRequestService.hasPendingCancelRequest(
                            reservation.getId(),
                            reservation.getMemberId()
                    );

            hasPendingChangeRequest =
                    reservationRequestService.hasPendingChangeRequest(
                            reservation.getId(),
                            reservation.getMemberId()
                    );

            hasPendingRequest =
                    reservationRequestService.hasPendingRequest(
                            reservation.getId(),
                            reservation.getMemberId()
                    );
        }

        String pendingRequestType = null;

        if (hasPendingCancelRequest) {
            pendingRequestType = "CANCEL";
        } else if (hasPendingChangeRequest) {
            pendingRequestType = "CHANGE";
        }

        return buildResponse(
                reservation,
                pendingRequestType,
                hasPendingCancelRequest,
                hasPendingChangeRequest,
                hasPendingRequest
        );
    }

    private AdminReservationResponse buildResponse(
            Reservation reservation,
            String pendingRequestType,
            boolean hasPendingCancelRequest,
            boolean hasPendingChangeRequest,
            boolean hasPendingRequest) {

        String status = reservation.getStatus();

        boolean reserved = "RESERVED".equals(status);
        boolean canceled = "CANCELED".equals(status);
        boolean completed = "COMPLETED".equals(status);
        boolean isCancelableReservation = reserved;

        return new AdminReservationResponse(
                reservation.getId(),
                reservation.getReservationDate(),
                reservation.getPeopleCount(),
                reservation.getRepresentativeName(),
                reservation.getPhoneNumber(),
                reservation.getCourseName(),
                reservation.getRemarks(),
                status,
                toStatusLabel(status),
                reservation.getCreatedAt(),
                pendingRequestType,
                isCancelableReservation,
                hasPendingCancelRequest,
                hasPendingChangeRequest,
                hasPendingRequest,
                reserved,
                canceled,
                completed
        );
    }

    private String toStatusLabel(String status) {
        if ("RESERVED".equals(status)) {
            return "予約中";
        }

        if ("CANCELED".equals(status)) {
            return "キャンセル済み";
        }

        if ("COMPLETED".equals(status)) {
            return "完了";
        }

        return status;
    }

    public record AdminReservationResponse(
            Integer id,
            LocalDateTime reservationDate,
            Integer peopleCount,
            String representativeName,
            String phoneNumber,
            String courseName,
            String remarks,
            String status,
            String statusLabel,
            LocalDateTime createdAt,
            String pendingRequestType,
            Boolean isCancelableReservation,
            Boolean hasPendingCancelRequest,
            Boolean hasPendingChangeRequest,
            Boolean hasPendingRequest,
            Boolean reserved,
            Boolean canceled,
            Boolean completed
    ) {
    }
}