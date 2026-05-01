package com.example.app.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationRequest {

    private Integer id;

    private Integer reservationId;
    private Integer memberId;

    private String requestType;
    private String requestReason;

    private LocalDate requestedReservationDate;
    private Integer requestedPeopleCount;
    private String requestedCourseName;
    private String requestedRemarks;

    private String status;
    private String refundStatus;

    private String adminComment;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public LocalDate getRequestedReservationDate() {
        return requestedReservationDate;
    }

    public void setRequestedReservationDate(LocalDate requestedReservationDate) {
        this.requestedReservationDate = requestedReservationDate;
    }

    public Integer getRequestedPeopleCount() {
        return requestedPeopleCount;
    }

    public void setRequestedPeopleCount(Integer requestedPeopleCount) {
        this.requestedPeopleCount = requestedPeopleCount;
    }

    public String getRequestedCourseName() {
        return requestedCourseName;
    }

    public void setRequestedCourseName(String requestedCourseName) {
        this.requestedCourseName = requestedCourseName;
    }

    public String getRequestedRemarks() {
        return requestedRemarks;
    }

    public void setRequestedRemarks(String requestedRemarks) {
        this.requestedRemarks = requestedRemarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}