package com.example.app.service;

import java.time.LocalDateTime;
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
public class ReservationRequestServiceImpl implements ReservationRequestService {

    private static final String RESERVATION_STATUS_RESERVED = "RESERVED";
    private static final String RESERVATION_STATUS_CANCELED = "CANCELED";

    private static final String REQUEST_TYPE_CANCEL = "CANCEL";
    private static final String REQUEST_TYPE_CHANGE = "CHANGE";

    private static final String REQUEST_STATUS_PENDING = "PENDING";
    private static final String REQUEST_STATUS_APPROVED = "APPROVED";
    private static final String REQUEST_STATUS_REJECTED = "REJECTED";

    private static final String REFUND_STATUS_NONE = "NONE";
    private static final String REFUND_STATUS_WAITING = "WAITING";

    private final ReservationRequestMapper reservationRequestMapper;
    private final ReservationMapper reservationMapper;

    public ReservationRequestServiceImpl(
            ReservationRequestMapper reservationRequestMapper,
            ReservationMapper reservationMapper) {
        this.reservationRequestMapper = reservationRequestMapper;
        this.reservationMapper = reservationMapper;
    }

    @Override
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

    @Override
    public void createChangeRequest(
            Integer reservationId,
            Integer memberId,
            String requestReason,
            LocalDateTime requestedReservationDate,
            Integer requestedPeopleCount,
            String requestedCourseName,
            String requestedRemarks) {

        Reservation reservation = reservationMapper.findByIdAndMemberId(reservationId, memberId);

        if (reservation == null) {
            throw new IllegalArgumentException("対象の予約が見つかりません。");
        }

        /*
         * 予約中の予約だけ変更申請可能。
         * CANCELED / COMPLETED などは申請不可。
         */
        if (!RESERVATION_STATUS_RESERVED.equals(reservation.getStatus())) {
            throw new IllegalStateException("この予約は変更申請できません。");
        }

        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        if (existingRequest != null) {
            throw new IllegalStateException("すでに申請中のリクエストがあります。");
        }

        ReservationRequest request = new ReservationRequest();
        request.setReservationId(reservationId);
        request.setMemberId(memberId);
        request.setRequestType(REQUEST_TYPE_CHANGE);
        request.setRequestReason(requestReason);
        request.setRequestedReservationDate(requestedReservationDate);
        request.setRequestedPeopleCount(requestedPeopleCount);
        request.setRequestedCourseName(requestedCourseName);
        request.setRequestedRemarks(requestedRemarks);
        request.setStatus(REQUEST_STATUS_PENDING);
        request.setRefundStatus(REFUND_STATUS_NONE);

        reservationRequestMapper.insert(request);

        /*
         * ここでは reservations の内容は変更しない。
         * 管理者が変更申請を承認したタイミングで reservations を更新する。
         */
    }

    @Override
    public List<ReservationRequestView> findPendingRequestViews() {
        return reservationRequestMapper.selectPendingRequestViews();
    }

    @Override
    public int countPendingCancelRequests() {
        return reservationRequestMapper.countPendingCancelRequests();
    }
    
    @Override
    public int countPendingRequests() {
        return reservationRequestMapper.countPendingRequests();
    }
    
    @Override
    public int countPending() {
        return reservationRequestMapper.countPending();
    }

    /*
     * 管理者側：申請承認。
     * CANCEL / CHANGE を requestType で判定して処理する。
     */
    @Override
    public void approveRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (REQUEST_TYPE_CANCEL.equals(request.getRequestType())) {
            approveCancelRequest(requestId, adminComment);
            return;
        }

        if (REQUEST_TYPE_CHANGE.equals(request.getRequestType())) {
            approveChangeRequest(requestId, adminComment);
            return;
        }

        throw new IllegalStateException("不明な申請種別です。");
    }

    /*
     * 管理者側：申請却下。
     * CANCEL / CHANGE を requestType で判定して処理する。
     */
    @Override
    public void rejectRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (REQUEST_TYPE_CANCEL.equals(request.getRequestType())) {
            rejectCancelRequest(requestId, adminComment);
            return;
        }

        if (REQUEST_TYPE_CHANGE.equals(request.getRequestType())) {
            rejectChangeRequest(requestId, adminComment);
            return;
        }

        throw new IllegalStateException("不明な申請種別です。");
    }

    @Override
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

    @Override
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

    @Override
    public void approveChangeRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (!REQUEST_TYPE_CHANGE.equals(request.getRequestType())) {
            throw new IllegalStateException("変更申請ではありません。");
        }

        Reservation reservation = reservationMapper.findByIdAndMemberId(
                request.getReservationId(),
                request.getMemberId()
        );

        if (reservation == null) {
            throw new IllegalArgumentException("対象の予約が見つかりません。");
        }

        if (!RESERVATION_STATUS_RESERVED.equals(reservation.getStatus())) {
            throw new IllegalStateException("この予約は変更できません。");
        }

        /*
         * 変更申請の内容を予約に反映する。
         * null の項目は既存値を維持する。
         */
        if (request.getRequestedReservationDate() != null) {
            reservation.setReservationDate(request.getRequestedReservationDate());
        }

        if (request.getRequestedPeopleCount() != null) {
            reservation.setPeopleCount(request.getRequestedPeopleCount());
        }

        if (request.getRequestedCourseName() != null) {
            reservation.setCourseName(request.getRequestedCourseName());
        }

        if (request.getRequestedRemarks() != null) {
            reservation.setRemarks(request.getRequestedRemarks());
        }

        reservationMapper.update(reservation);

        reservationRequestMapper.updateProcessResult(
                requestId,
                REQUEST_STATUS_APPROVED,
                REFUND_STATUS_NONE,
                adminComment
        );
    }

    @Override
    public void rejectChangeRequest(Integer requestId, String adminComment) {

        ReservationRequest request = reservationRequestMapper.selectById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("申請が見つかりません。");
        }

        if (!REQUEST_STATUS_PENDING.equals(request.getStatus())) {
            throw new IllegalStateException("すでに処理済みの申請です。");
        }

        if (!REQUEST_TYPE_CHANGE.equals(request.getRequestType())) {
            throw new IllegalStateException("変更申請ではありません。");
        }

        /*
         * 却下の場合、予約内容は変更しない。
         */
        reservationRequestMapper.updateProcessResult(
                requestId,
                REQUEST_STATUS_REJECTED,
                REFUND_STATUS_NONE,
                adminComment
        );
    }

    @Override
    public boolean hasPendingCancelRequest(Integer reservationId, Integer memberId) {
        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        return existingRequest != null
                && REQUEST_TYPE_CANCEL.equals(existingRequest.getRequestType());
    }

    @Override
    public boolean hasPendingChangeRequest(Integer reservationId, Integer memberId) {
        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        return existingRequest != null
                && REQUEST_TYPE_CHANGE.equals(existingRequest.getRequestType());
    }

    @Override
    public boolean hasPendingRequest(Integer reservationId, Integer memberId) {
        ReservationRequest existingRequest =
                reservationRequestMapper.findPendingByReservationIdAndMemberId(reservationId, memberId);

        return existingRequest != null;
    }
}