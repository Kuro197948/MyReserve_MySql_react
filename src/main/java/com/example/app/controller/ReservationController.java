package com.example.app.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Reservation;
import com.example.app.service.ReservationService;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // 会員側：予約入力画面を開く
    @GetMapping("/members/club/reservation/form")
    public String showReservationForm(Model model, HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        model.addAttribute("reservation", new Reservation());
        model.addAttribute("title", "予約フォーム");

        return "members/club/reservationForm";
    }

    // 会員側：予約登録
    @PostMapping("/members/club/reservation/create")
    public String createReservation(Reservation reservation, HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        reservation.setMemberId(memberId);

        /*
         * insert SQL 側でも status = 'RESERVED' を入れているが、
         * Java側でも明示しておくと意図が分かりやすい。
         */
        reservation.setStatus("RESERVED");

        reservationService.insert(reservation);

        return "redirect:/members/club/reservation/complete";
    }

    // 会員側：予約完了画面
    @GetMapping("/members/club/reservation/complete")
    public String showReservationComplete(Model model, HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        model.addAttribute("title", "予約完了");

        return "members/club/reservationComplete";
    }

    // 管理側：予約一覧
    @GetMapping("/admins/club/reservations")
    public String showReservationList(
            Model model,
            HttpSession session) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        model.addAttribute("title", "予約一覧");
        model.addAttribute("reservations", reservationService.selectAll());

        return "admins/club/reservationList";
    }

    // 管理側：予約詳細
    @GetMapping("/admins/club/reservations/{id}")
    public String showReservationDetail(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        Reservation reservation = reservationService.selectById(id);

        if (reservation == null) {
            return "redirect:/admins/club/reservations";
        }

        model.addAttribute("title", "予約詳細");
        model.addAttribute("reservation", reservation);

        return "admins/club/reservationDetail";
    }

    // 管理側：編集画面表示
    @GetMapping("/admins/club/reservations/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        Reservation reservation = reservationService.selectById(id);

        if (reservation == null) {
            return "redirect:/admins/club/reservations";
        }

        model.addAttribute("title", "予約編集");
        model.addAttribute("reservation", reservation);

        return "admins/club/reservationEdit";
    }

    // 管理側：更新処理
    @PostMapping("/admins/club/reservations/update")
    public String updateReservation(
            Reservation reservation,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        reservationService.update(reservation);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "予約情報を更新しました。"
        );

        return "redirect:/admins/club/reservations/" + reservation.getId();
    }

    // 管理側：予約一覧から削除
    // 物理削除ではなく、status = CANCELED にして履歴へ移動する
    @PostMapping("/admins/club/reservations/delete/{id}")
    public String deleteReservation(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        reservationService.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "予約をキャンセル済みにしました。予約履歴から確認できます。"
        );

        return "redirect:/admins/club/reservations";
    }
}