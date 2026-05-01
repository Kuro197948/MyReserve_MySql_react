package com.example.app.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Data;

@Data
public class Reservation {

    private Integer id;
    private Integer peopleCount;
    private String representativeName;
    private String phoneNumber;
    private String courseName;
    private String remarks;
    private LocalDateTime createdAt;
    private Integer memberId;
    private String status;
    private LocalDateTime reservationDate;

    private boolean memberDeleted;
    private boolean adminDeleted;

    public String getStatusLabel() {
        if (status == null) {
            return "予約中";
        }

        switch (status) {
            case "RESERVED":
                return "予約中";
            case "CANCELED":
                return "キャンセル済み";
            case "COMPLETED":
                return "完了";
            default:
                return status;
        }
    }

    public boolean isReserved() {
        return "RESERVED".equals(status);
    }

    public boolean isCanceled() {
        return "CANCELED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /*
     * 予約日までの日数を返す。
     * 今日なら 0、明日なら 1、昨日なら -1。
     */
    public long getDaysUntilReservation() {
        if (reservationDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate reservationLocalDate = reservationDate.toLocalDate();

        return ChronoUnit.DAYS.between(today, reservationLocalDate);
    }

    /*
     * 会員側画面に表示する予約日までの案内文。
     */
    public String getDaysUntilReservationLabel() {
        if (reservationDate == null) {
            return "予約日時が未設定です";
        }

        long days = getDaysUntilReservation();

        if (days == 0) {
            return "予約日は本日です";
        }

        if (days == 1) {
            return "予約日は明日です";
        }

        if (days > 1) {
            return "予約日まであと" + days + "日です";
        }

        return "予約日は過ぎています";
    }

    /*
     * 画面表示用：予約日までの状態をCSSで分けたい場合に使う。
     */
    public String getDaysUntilReservationClass() {
        if (reservationDate == null) {
            return "reservation-countdown-muted";
        }

        long days = getDaysUntilReservation();

        if (days == 0) {
            return "reservation-countdown-today";
        }

        if (days == 1) {
            return "reservation-countdown-tomorrow";
        }

        if (days > 1) {
            return "reservation-countdown-upcoming";
        }

        return "reservation-countdown-past";
    }
}