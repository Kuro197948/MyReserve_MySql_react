package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.Reservation;

@Mapper
public interface ReservationMapper {

    void insert(Reservation reservation);

    List<Reservation> selectAll();

    Reservation selectById(Integer id);

    void update(Reservation reservation);

    void deleteById(Integer id);

    List<Reservation> findByMemberId(Integer memberId);
}