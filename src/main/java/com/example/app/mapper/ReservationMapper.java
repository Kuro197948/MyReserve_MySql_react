package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.Reservation;

@Mapper
public interface ReservationMapper {

    void insert(Reservation reservation);

    List<Reservation> selectAll();

    Reservation selectById(Integer id);

    void update(Reservation reservation);

    /*
     * 管理者側の削除処理。
     * XML側では物理削除ではなく、
     * admin_deleted = true にする。
     */
    void deleteById(@Param("id") Integer id);

    /*
     * 会員側：現在の予約一覧
     */
    List<Reservation> findByMemberId(@Param("memberId") Integer memberId);

    Reservation findByIdAndMemberId(
            @Param("id") Integer id,
            @Param("memberId") Integer memberId
    );

    /*
     * 会員側：予約キャンセルなどで使用
     */
    void updateStatusByIdAndMemberId(
            @Param("id") Integer id,
            @Param("memberId") Integer memberId,
            @Param("status") String status
    );

    /*
     * 管理者側：予約ステータス変更で使用
     */
    void updateStatusById(
            @Param("id") Integer id,
            @Param("status") String status
    );

    /*
     * 管理者側：予約履歴
     */
    List<Reservation> selectHistory();

    /*
     * 会員側：予約履歴
     */
    List<Reservation> findHistoryByMemberId(@Param("memberId") Integer memberId);

    /*
     * 会員側：履歴から削除
     * DBからは消さず、member_deleted = true にする。
     */
    void hideFromMember(
            @Param("id") Integer id,
            @Param("memberId") Integer memberId
    );

    /*
     * 管理者側：履歴から削除
     * DBからは消さず、admin_deleted = true にする。
     */
    void hideFromAdmin(@Param("id") Integer id);
    
    /*
     * 会員側ホーム：次回予約を1件取得
     */
    Reservation findNextReservationByMemberId(@Param("memberId") Integer memberId);
}