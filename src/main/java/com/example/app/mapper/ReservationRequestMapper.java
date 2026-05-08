package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.ReservationRequest;
import com.example.app.domain.ReservationRequestView;

@Mapper
public interface ReservationRequestMapper {

    void insert(ReservationRequest reservationRequest);

    ReservationRequest selectById(@Param("id") Integer id);

    List<ReservationRequest> selectAll();

    List<ReservationRequest> selectPending();
    

    ReservationRequest findPendingByReservationIdAndMemberId(
            @Param("reservationId") Integer reservationId,
            @Param("memberId") Integer memberId
    );

    List<ReservationRequestView> selectPendingRequestViews();
    
    int countPending();
    int countPendingCancelRequests();
    int countPendingRequests();
    void updateProcessResult(
            @Param("id") Integer id,
            @Param("status") String status,
            @Param("refundStatus") String refundStatus,
            @Param("adminComment") String adminComment
    );
}