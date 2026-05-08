package com.example.app.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
import com.example.app.service.ReservationRequestService;
import com.example.app.service.ReservationService;

@Controller
@RequestMapping("/members/club")
public class MemberReservationController {

    private static final String STATUS_RESERVED = "RESERVED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final ReservationService reservationService;
    private final ReservationRequestService reservationRequestService;

    public MemberReservationController(
            ReservationService reservationService,
            ReservationRequestService reservationRequestService) {
        this.reservationService = reservationService;
        this.reservationRequestService = reservationRequestService;
    }

    @GetMapping("/reservations")
    public String reservations(HttpSession session, Model model) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        List<Reservation> reservations = reservationService.findByMemberId(memberId);

        model.addAttribute("title", "予約確認");
        model.addAttribute("reservations", reservations);

        return "members/club/reservations";
    }

    @GetMapping("/reservations/{id}")
    public String reservationDetail(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations";
        }

        boolean hasPendingCancelRequest =
                reservationRequestService.hasPendingCancelRequest(id, memberId);

        boolean hasPendingChangeRequest =
                reservationRequestService.hasPendingChangeRequest(id, memberId);

        boolean hasPendingRequest =
                hasPendingCancelRequest || hasPendingChangeRequest;

        model.addAttribute("title", "予約詳細");
        model.addAttribute("reservation", reservation);
        model.addAttribute("hasPendingCancelRequest", hasPendingCancelRequest);
        model.addAttribute("hasPendingChangeRequest", hasPendingChangeRequest);
        model.addAttribute("hasPendingRequest", hasPendingRequest);
        model.addAttribute("isCancelableReservation", isCancelableReservation(reservation));

        return "members/club/reservation-detail";
    }

    @GetMapping("/reservations/{id}/cancel-request")
    public String cancelRequestForm(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations";
        }

        /*
         * 予約中かつ予約日が今日以降の予約だけキャンセル申請可能。
         * CANCELED / COMPLETED / 過去予約はキャンセル申請不可。
         */
        if (!isCancelableReservation(reservation)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "この予約はキャンセル申請できません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        boolean hasPendingCancelRequest =
                reservationRequestService.hasPendingCancelRequest(id, memberId);

        boolean hasPendingChangeRequest =
                reservationRequestService.hasPendingChangeRequest(id, memberId);

        if (hasPendingCancelRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでにキャンセル申請中です。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        if (hasPendingChangeRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでに変更申請中のため、キャンセル申請はできません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        model.addAttribute("title", "キャンセル申請");
        model.addAttribute("reservation", reservation);

        return "members/club/cancel-request";
    }

    @PostMapping("/reservations/{id}/cancel-request")
    public String submitCancelRequest(
            @PathVariable Integer id,
            @RequestParam("requestReason") String requestReason,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations";
        }

        /*
         * 予約中かつ予約日が今日以降の予約だけキャンセル申請可能。
         */
        if (!isCancelableReservation(reservation)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "この予約はキャンセル申請できません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        boolean hasPendingChangeRequest =
                reservationRequestService.hasPendingChangeRequest(id, memberId);

        if (hasPendingChangeRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでに変更申請中のため、キャンセル申請はできません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        if (requestReason == null || requestReason.trim().isEmpty()) {
            model.addAttribute("title", "キャンセル申請");
            model.addAttribute("reservation", reservation);
            model.addAttribute("errorMessage", "キャンセル理由を入力してください。");
            model.addAttribute("requestReason", requestReason);

            return "members/club/cancel-request";
        }

        try {
            reservationRequestService.createCancelRequest(
                    id,
                    memberId,
                    requestReason.trim()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "キャンセル申請を送信しました。管理者の確認をお待ちください。"
            );

            return "redirect:/members/club/reservations/" + id;

        } catch (IllegalStateException e) {
            model.addAttribute("title", "キャンセル申請");
            model.addAttribute("reservation", reservation);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("requestReason", requestReason);

            return "members/club/cancel-request";

        } catch (IllegalArgumentException e) {
            return "redirect:/members/club/reservations";
        }
    }

    @GetMapping("/reservations/{id}/change-request")
    public String changeRequestForm(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations";
        }

        /*
         * 予約中かつ予約日が今日以降の予約だけ変更申請可能。
         */
        if (!isCancelableReservation(reservation)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "この予約は変更申請できません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        boolean hasPendingCancelRequest =
                reservationRequestService.hasPendingCancelRequest(id, memberId);

        boolean hasPendingChangeRequest =
                reservationRequestService.hasPendingChangeRequest(id, memberId);

        if (hasPendingCancelRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでにキャンセル申請中のため、変更申請はできません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        if (hasPendingChangeRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでに変更申請中です。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        model.addAttribute("title", "変更申請");
        model.addAttribute("reservation", reservation);

        return "members/club/change-request";
    }

    @PostMapping("/reservations/{id}/change-request")
    public String submitChangeRequest(
            @PathVariable Integer id,
            @RequestParam("requestedReservationDate") String requestedReservationDate,
            @RequestParam("requestedPeopleCount") Integer requestedPeopleCount,
            @RequestParam(value = "requestedCourseName", required = false) String requestedCourseName,
            @RequestParam(value = "requestedRemarks", required = false) String requestedRemarks,
            @RequestParam("requestReason") String requestReason,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations";
        }

        if (!isCancelableReservation(reservation)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "この予約は変更申請できません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        boolean hasPendingCancelRequest =
                reservationRequestService.hasPendingCancelRequest(id, memberId);

        boolean hasPendingChangeRequest =
                reservationRequestService.hasPendingChangeRequest(id, memberId);

        if (hasPendingCancelRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでにキャンセル申請中のため、変更申請はできません。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        if (hasPendingChangeRequest) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "すでに変更申請中です。"
            );

            return "redirect:/members/club/reservations/" + id;
        }

        if (requestedReservationDate == null || requestedReservationDate.trim().isEmpty()) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    "変更希望日時を入力してください。",
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";
        }

        if (requestedPeopleCount == null || requestedPeopleCount <= 0) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    "変更希望人数は1名以上で入力してください。",
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";
        }

        if (requestReason == null || requestReason.trim().isEmpty()) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    "変更理由を入力してください。",
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";
        }

        LocalDateTime parsedRequestedReservationDate;

        try {
            parsedRequestedReservationDate =
                    LocalDateTime.parse(requestedReservationDate.trim());

        } catch (DateTimeParseException e) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    "変更希望日時の形式が正しくありません。",
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";
        }

        if (parsedRequestedReservationDate.toLocalDate().isBefore(LocalDate.now())) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    "過去の日付には変更申請できません。",
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";
        }

        try {
            reservationRequestService.createChangeRequest(
                    id,
                    memberId,
                    requestReason.trim(),
                    parsedRequestedReservationDate,
                    requestedPeopleCount,
                    trimToNull(requestedCourseName),
                    trimToNull(requestedRemarks)
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "変更申請を送信しました。管理者の確認をお待ちください。"
            );

            return "redirect:/members/club/reservations/" + id;

        } catch (IllegalStateException e) {
            setChangeRequestFormAttributes(
                    model,
                    reservation,
                    e.getMessage(),
                    requestedReservationDate,
                    requestedPeopleCount,
                    requestedCourseName,
                    requestedRemarks,
                    requestReason
            );

            return "members/club/change-request";

        } catch (IllegalArgumentException e) {
            return "redirect:/members/club/reservations";
        }
    }

    @GetMapping("/reservations/history")
    public String reservationHistory(HttpSession session, Model model) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        List<Reservation> reservations = reservationService.findHistoryByMemberId(memberId);

        model.addAttribute("title", "予約履歴");
        model.addAttribute("reservations", reservations);

        return "members/club/reservation-history";
    }

    /*
     * 会員側：予約履歴から削除
     * DBから物理削除はしない。
     * member_deleted = true にして、会員側の履歴画面から非表示にする。
     */
    @PostMapping("/reservations/history/{id}/delete")
    public String deleteReservationHistory(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        Reservation reservation = reservationService.findByIdAndMemberId(id, memberId);

        if (reservation == null) {
            return "redirect:/members/club/reservations/history";
        }

        /*
         * 履歴対象の予約だけ削除可能。
         * 現在予約をURL直打ちで非表示にされるのを防ぐ。
         */
        if (!isHistoryReservation(reservation)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "現在有効な予約は履歴から削除できません。"
            );

            return "redirect:/members/club/reservations/history";
        }

        reservationService.hideFromMember(id, memberId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "予約履歴を削除しました。"
        );

        return "redirect:/members/club/reservations/history";
    }

    /*
     * キャンセル申請・変更申請できる予約か判定する。
     */
    private boolean isCancelableReservation(Reservation reservation) {

        if (reservation == null || reservation.getReservationDate() == null) {
            return false;
        }

        return STATUS_RESERVED.equals(reservation.getStatus())
                && !reservation.getReservationDate()
                        .toLocalDate()
                        .isBefore(LocalDate.now());
    }

    /*
     * 予約履歴に表示される対象か判定する。
     */
    private boolean isHistoryReservation(Reservation reservation) {

        if (reservation == null) {
            return false;
        }

        if (STATUS_CANCELED.equals(reservation.getStatus())
                || STATUS_COMPLETED.equals(reservation.getStatus())) {
            return true;
        }

        if (reservation.getReservationDate() == null) {
            return false;
        }

        return reservation.getReservationDate()
                .toLocalDate()
                .isBefore(LocalDate.now());
    }

    private void setChangeRequestFormAttributes(
            Model model,
            Reservation reservation,
            String errorMessage,
            String requestedReservationDate,
            Integer requestedPeopleCount,
            String requestedCourseName,
            String requestedRemarks,
            String requestReason) {

        model.addAttribute("title", "変更申請");
        model.addAttribute("reservation", reservation);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("requestedReservationDate", requestedReservationDate);
        model.addAttribute("requestedPeopleCount", requestedPeopleCount);
        model.addAttribute("requestedCourseName", requestedCourseName);
        model.addAttribute("requestedRemarks", requestedRemarks);
        model.addAttribute("requestReason", requestReason);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}