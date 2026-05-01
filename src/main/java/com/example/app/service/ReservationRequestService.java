package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Reservation;
import com.example.app.domain.ReservationRequest;
import com.example.app.domain.ReservationRequestView;
import com.example.app.mapper.ReservationMapper;
import com.example.app.mapper.ReservationRequestMapper;

@Service
@Transactional
public class ReservationRequestService {

    private static final String RESERVATION_STATUS_RESERVED = "RESERVED";
    private static final String RESERVATION_STATUS_CANCELED = "CANCELED";

    private static final String REQUEST_TYPE_CANCEL = "CANCEL";

    private static final String REQUEST_STATUS_PENDING = "PENDING";
    private static final String REQUEST_STATUS_APPROVED = "APPROVED";
    private static final String REQUEST_STATUS_REJECTED = "REJECTED";

    private static final String REFUND_STATUS_NONE = "NONE";
    private static final String REFUND_STATUS_WAITING = "WAITING";

    private final ReservationRequestMapper reservationRequestMapper;
    private final ReservationMapper reservationMapper;

    public ReservationRequestService(
            ReservationRequestMapper reservationRequestMapper,
            ReservationMapper reservationMapper) {
        this.reservationRequestMapper = reservationRequestMapper;
        this.reservationMapper = reservationMapper;
    }

    public void createCancelRequest(Integer reservationId, Integer memberId, String requestReason) {

        Reservation reservation = reservationMapper.findByIdAndMemberId(reservationId, memberId);

        if (reservation == null) {
            throw new IllegalArgumentException("対象の予約が見つかりません。");
        }

        /*
         * 予約中の予約だけキャンセル申請可能。
         * CANCELED / COMPLETED などは申請不可。
         */
        if (!RESERVATION_STATUS_RESERVED.equals(reservation.getStatus())) {
            throw new IllegalStateException("この予約はキャンセル申請できません。");
        }

        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        if (existingRequest != null) {
            throw new IllegalStateException("すでに申請中のリクエストがあります。");
        }

        ReservationRequest request = new ReservationRequest();
        request.setReservationId(reservationId);
        request.setMemberId(memberId);
        request.setRequestType(REQUEST_TYPE_CANCEL);
        request.setRequestReason(requestReason);
        request.setStatus(REQUEST_STATUS_PENDING);
        request.setRefundStatus(REFUND_STATUS_NONE);

        reservationRequestMapper.insert(request);

        /*
         * ここでは reservations.status は変更しない。
         * キャンセル申請中かどうかは reservation_requests.status = PENDING で管理する。
         */
    }

    public List<ReservationRequestView> findPendingRequestViews() {
        return reservationRequestMapper.selectPendingRequestViews();
    }

    public void approveCancelRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (!REQUEST_TYPE_CANCEL.equals(request.getRequestType())) {
            throw new IllegalStateException("キャンセル申請ではありません。");
        }

        reservationRequestMapper.updateProcessResult(
                requestId,
                REQUEST_STATUS_APPROVED,
                REFUND_STATUS_WAITING,
                adminComment
        );

        /*
         * 承認されたタイミングで、予約をキャンセル済みにする。
         */
        reservationMapper.updateStatusByIdAndMemberId(
                request.getReservationId(),
                request.getMemberId(),
                RESERVATION_STATUS_CANCELED
        );
    }

    public void rejectCancelRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (!REQUEST_TYPE_CANCEL.equals(request.getRequestType())) {
            throw new IllegalStateException("キャンセル申請ではありません。");
        }

        reservationRequestMapper.updateProcessResult(
                requestId,
                REQUEST_STATUS_REJECTED,
                REFUND_STATUS_NONE,
                adminComment
        );

        /*
         * 却下の場合、予約は有効なまま。
         * 念のため RESERVED に戻しておく。
         */
        reservationMapper.updateStatusByIdAndMemberId(
                request.getReservationId(),
                request.getMemberId(),
                RESERVATION_STATUS_RESERVED
        );
    }
    public boolean hasPendingCancelRequest(Integer reservationId, Integer memberId) {
        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        return existingRequest != null;
    }
}