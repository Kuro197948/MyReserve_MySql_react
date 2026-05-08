package com.example.app.service;

import java.util.List;

import com.example.app.domain.Reservation;

public interface ReservationService {

    void insert(Reservation reservation);

    List<Reservation> selectAll();

    Reservation selectById(Integer id);

    void update(Reservation reservation);

    void deleteById(Integer id);

    void completeById(Integer id);

    void cancelByAdmin(Integer id);

    void hideFromAdmin(Integer id);

    List<Reservation> findByMemberId(Integer memberId);

    Reservation findNextReservationByMemberId(Integer memberId);

    Reservation findByIdAndMemberId(Integer id, Integer memberId);

    void cancelByMember(Integer id, Integer memberId);

    void hideFromMember(Integer id, Integer memberId);

    List<Reservation> selectHistory();

    List<Reservation> findHistoryByMemberId(Integer memberId);

    void updateStatusById(Integer id, String status);

    void updateStatusByIdAndMemberId(Integer id, Integer memberId, String status);

    int countTodayReserved();

    int countMonthlyReserved();
}