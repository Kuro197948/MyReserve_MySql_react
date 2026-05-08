package com.example.app.domain;

import java.time.LocalDateTime;

public class ReservationRequestView {

    private Integer id;

    private Integer reservationId;
    private Integer memberId;

    private String requestType;
    private String requestReason;

    private LocalDateTime requestedReservationDate;
    private Integer requestedPeopleCount;
    private String requestedCourseName;
    private String requestedRemarks;

    private String status;
    private String refundStatus;

    private String adminComment;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    private LocalDateTime reservationDate;
    private Integer peopleCount;
    private String representativeName;
    private String phoneNumber;
    private String courseName;
    private String reservationStatus;

    private String memberName;
    private String memberEmail;

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

    public LocalDateTime getRequestedReservationDate() {
        return requestedReservationDate;
    }

    public void setRequestedReservationDate(LocalDateTime requestedReservationDate) {
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

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getRequestTypeLabel() {
        if ("CANCEL".equals(requestType)) {
            return "キャンセル申請";
        }

        if ("CHANGE".equals(requestType)) {
            return "変更申請";
        }

        return requestType;
    }

    public String getStatusLabel() {
        if ("PENDING".equals(status)) {
            return "申請中";
        }

        if ("APPROVED".equals(status)) {
            return "承認済み";
        }

        if ("REJECTED".equals(status)) {
            return "却下";
        }

        return status;
    }
}