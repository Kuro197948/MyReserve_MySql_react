package com.example.app.controller.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.ReservationRequestView;
import com.example.app.service.ReservationRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reservation-requests")
@RequiredArgsConstructor
public class AdminReservationRequestApiController {

    private final ReservationRequestService reservationRequestService;

    @GetMapping
    public ResponseEntity<List<ReservationRequestResponse>> list(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ReservationRequestResponse> responseList =
                reservationRequestService.findPendingRequestViews()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Integer id,
            @RequestBody(required = false) ProcessRequest request,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String adminComment = request == null || request.adminComment() == null
                ? ""
                : request.adminComment().trim();

        try {
            reservationRequestService.approveRequest(id, adminComment);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Integer id,
            @RequestBody(required = false) ProcessRequest request,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String adminComment = request == null || request.adminComment() == null
                ? ""
                : request.adminComment().trim();

        try {
            reservationRequestService.rejectRequest(id, adminComment);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private ReservationRequestResponse toResponse(ReservationRequestView request) {
        return new ReservationRequestResponse(
                request.getId(),
                request.getReservationId(),
                request.getMemberId(),
                request.getRequestType(),
                toRequestTypeLabel(request.getRequestType()),
                request.getStatus(),
                request.getStatusLabel(),
                request.getMemberName(),
                request.getMemberEmail(),
                request.getReservationDate(),
                request.getPeopleCount(),
                request.getRepresentativeName(),
                request.getPhoneNumber(),
                request.getCourseName(),
                request.getRequestedReservationDate(),
                request.getRequestedPeopleCount(),
                request.getRequestedCourseName(),
                request.getRequestedRemarks(),
                request.getRequestReason(),
                request.getCreatedAt()
        );
    }

    private String toRequestTypeLabel(String requestType) {
        if ("CANCEL".equals(requestType)) {
            return "キャンセル申請";
        }

        if ("CHANGE".equals(requestType)) {
            return "変更申請";
        }

        return requestType;
    }

    public record ProcessRequest(
            String adminComment
    ) {
    }

    public record ReservationRequestResponse(
            Integer id,
            Integer reservationId,
            Integer memberId,
            String requestType,
            String requestTypeLabel,
            String status,
            String statusLabel,
            String memberName,
            String memberEmail,
            LocalDateTime reservationDate,
            Integer peopleCount,
            String representativeName,
            String phoneNumber,
            String courseName,
            LocalDateTime requestedReservationDate,
            Integer requestedPeopleCount,
            String requestedCourseName,
            String requestedRemarks,
            String requestReason,
            LocalDateTime createdAt
    ) {
    }
}