package com.example.app.controller.api;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationResponse {
    private Integer id;
    private Integer peopleCount;
    private String representativeName;
    private String phoneNumber;
    private String courseName;
    private String remarks;
    private String status;
    private LocalDateTime reservationDate;
    private LocalDateTime createdAt;
}