package com.example.app.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Reservation;
import com.example.app.domain.ReservationRequestView;
import com.example.app.service.ReservationRequestService;
import com.example.app.service.ReservationService;

@Controller
@RequestMapping("/admins/club")
public class AdminReservationRequestController {

    private final ReservationRequestService reservationRequestService;
    private final ReservationService reservationService;

    public AdminReservationRequestController(
            ReservationRequestService reservationRequestService,
            ReservationService reservationService) {
        this.reservationRequestService = reservationRequestService;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation-requests")
    public String reservationRequests(HttpSession session, Model model) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        List<ReservationRequestView> requests =
                reservationRequestService.findPendingRequestViews();

        model.addAttribute("title", "予約申請一覧");
        model.addAttribute("requests", requests);

        return "admins/club/reservation-requests";
    }

    @PostMapping("/reservation-requests/{id}/approve")
    public String approveRequest(
            @PathVariable Integer id,
            @RequestParam(value = "adminComment", required = false) String adminComment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        try {
            reservationRequestService.approveRequest(
                    id,
                    adminComment == null ? "" : adminComment.trim()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "予約申請を承認しました。"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admins/club/reservation-requests";
    }

    @PostMapping("/reservation-requests/{id}/reject")
    public String rejectRequest(
            @PathVariable Integer id,
            @RequestParam(value = "adminComment", required = false) String adminComment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        try {
            reservationRequestService.rejectRequest(
                    id,
                    adminComment == null ? "" : adminComment.trim()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "予約申請を却下しました。"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admins/club/reservation-requests";
    }

    @GetMapping("/reservations/history")
    public String reservationHistory(HttpSession session, Model model) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        List<Reservation> reservations = reservationService.selectHistory();

        model.addAttribute("title", "予約履歴");
        model.addAttribute("reservations", reservations);

        return "admins/club/reservation-history";
    }

    /*
     * 管理者側：予約履歴から削除
     * DBから物理削除はしない。
     * admin_deleted = true にして、管理者側の履歴画面から非表示にする。
     */
    @PostMapping("/reservations/history/{id}/delete")
    public String deleteReservationHistory(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            return "redirect:/admins/adminslogin";
        }

        reservationService.hideFromAdmin(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "予約履歴を削除しました。"
        );

        return "redirect:/admins/club/reservations/history";
    }
}