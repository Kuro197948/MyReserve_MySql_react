package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Reservation;
import com.example.app.mapper.ReservationMapper;

@Service
@Transactional
public class ReservationService {

    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    public void insert(Reservation reservation) {
        reservationMapper.insert(reservation);
    }

    public List<Reservation> selectAll() {
        return reservationMapper.selectAll();
    }

    public Reservation selectById(Integer id) {
        return reservationMapper.selectById(id);
    }

    public void update(Reservation reservation) {
        reservationMapper.update(reservation);
    }

    public void deleteById(Integer id) {
        reservationMapper.deleteById(id);
    }

    public List<Reservation> findByMemberId(Integer memberId) {
        return reservationMapper.findByMemberId(memberId);
    }
}