package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Reservation;
import com.example.app.mapper.ReservationMapper;

@Service
@Transactional
public class ReservationService {

    private static final String STATUS_RESERVED = "RESERVED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String STATUS_COMPLETED = "COMPLETED";

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

    /*
     * 管理者側：予約一覧から削除
     * 物理削除はしない。
     * status を CANCELED にして、予約履歴へ移動させる。
     */
    public void deleteById(Integer id) {
        reservationMapper.deleteById(id);
    }

    /*
     * 管理者側：予約を完了にする
     */
    public void completeById(Integer id) {
        reservationMapper.updateStatusById(id, STATUS_COMPLETED);
    }

    /*
     * 管理者側：予約をキャンセルにする
     */
    public void cancelByAdmin(Integer id) {
        reservationMapper.updateStatusById(id, STATUS_CANCELED);
    }

    /*
     * 管理者側：予約履歴から削除
     * DBからは消さず、管理者側だけ非表示にする。
     */
    public void hideFromAdmin(Integer id) {
        reservationMapper.hideFromAdmin(id);
    }

    /*
     * 会員側：現在の予約一覧
     */
    public List<Reservation> findByMemberId(Integer memberId) {
        return reservationMapper.findByMemberId(memberId);
    }

    /*
     * 会員側ホーム：次回予約を1件取得
     */
    public Reservation findNextReservationByMemberId(Integer memberId) {
        return reservationMapper.findNextReservationByMemberId(memberId);
    }

    public Reservation findByIdAndMemberId(Integer id, Integer memberId) {
        return reservationMapper.findByIdAndMemberId(id, memberId);
    }

    /*
     * 会員側：予約キャンセル
     * 物理削除はしない。
     * status を CANCELED にして、予約履歴へ移動させる。
     */
    public void cancelByMember(Integer id, Integer memberId) {
        reservationMapper.updateStatusByIdAndMemberId(id, memberId, STATUS_CANCELED);
    }

    /*
     * 会員側：予約履歴から削除
     * DBからは消さず、会員側だけ非表示にする。
     */
    public void hideFromMember(Integer id, Integer memberId) {
        reservationMapper.hideFromMember(id, memberId);
    }

    public List<Reservation> selectHistory() {
        return reservationMapper.selectHistory();
    }

    public List<Reservation> findHistoryByMemberId(Integer memberId) {
        return reservationMapper.findHistoryByMemberId(memberId);
    }

    /*
     * 必要になった場合用：管理者側ステータス更新
     */
    public void updateStatusById(Integer id, String status) {
        reservationMapper.updateStatusById(id, status);
    }

    /*
     * 必要になった場合用：会員本人の予約ステータス更新
     */
    public void updateStatusByIdAndMemberId(Integer id, Integer memberId, String status) {
        reservationMapper.updateStatusByIdAndMemberId(id, memberId, status);
    }
}