package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.app.domain.ReservationRequestView;

public interface ReservationRequestService {

    void createCancelRequest(Integer reservationId, Integer memberId, String requestReason);

    void createChangeRequest(
            Integer reservationId,
            Integer memberId,
            String requestReason,
            LocalDateTime requestedReservationDate,
            Integer requestedPeopleCount,
            String requestedCourseName,
            String requestedRemarks
            
    );

    List<ReservationRequestView> findPendingRequestViews();

    int countPendingCancelRequests();

    void approveRequest(Integer requestId, String adminComment);

    void rejectRequest(Integer requestId, String adminComment);

    void approveCancelRequest(Integer requestId, String adminComment);

    void rejectCancelRequest(Integer requestId, String adminComment);

    void approveChangeRequest(Integer requestId, String adminComment);

    void rejectChangeRequest(Integer requestId, String adminComment);

    boolean hasPendingCancelRequest(Integer reservationId, Integer memberId);

    boolean hasPendingChangeRequest(Integer reservationId, Integer memberId);

    boolean hasPendingRequest(Integer reservationId, Integer memberId);
    
    int countPendingRequests();
    
    int countPending();
}