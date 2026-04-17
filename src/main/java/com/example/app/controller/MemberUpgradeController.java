package com.example.app.controller;

import java.time.YearMonth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.UpgradePaymentForm;
import com.example.app.service.MemberUpgradeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberUpgradeController {

    private final MemberUpgradeService memberUpgradeService;
    private final HttpSession session;

    @GetMapping("/members/club/upgrade")
    public String showUpgradePage(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        if (memberTypeId != null && memberTypeId == 2) {
            return "redirect:/members/club/home";
        }

        model.addAttribute("title", "プレミアム会員アップグレード");
        model.addAttribute("upgradePaymentForm", new UpgradePaymentForm());
        return "members/club/upgrade";
    }

    @PostMapping("/members/club/upgrade/complete")
    public String completeUpgrade(
            @ModelAttribute("upgradePaymentForm") @Valid UpgradePaymentForm form,
            Errors errors,
            Model model) {

        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        if (memberTypeId != null && memberTypeId == 2) {
            return "redirect:/members/club/home";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "プレミアム会員アップグレード");
            return "members/club/upgrade";
        }

        // 基本チェック
        if (!form.getCardNumber().matches("\\d{16}")) {
            errors.rejectValue("cardNumber", "error.cardNumber", "カード番号は16桁の数字で入力してください");
        }

        if (!form.getExpiry().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            errors.rejectValue("expiry", "error.expiry", "有効期限は MM/YY 形式で入力してください");
        } else {
            String[] parts = form.getExpiry().split("/");
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]);

            YearMonth inputExpiry = YearMonth.of(year, month);
            YearMonth now = YearMonth.now();

            if (inputExpiry.isBefore(now)) {
                errors.rejectValue("expiry", "error.expiry", "有効期限切れのカードは使用できません");
            }
        }

        if (!form.getCvc().matches("\\d{3,4}")) {
            errors.rejectValue("cvc", "error.cvc", "セキュリティコードは3〜4桁の数字で入力してください");
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "プレミアム会員アップグレード");
            return "members/club/upgrade";
        }

        // ダミー決済の成功 / 失敗分岐
        String cardNumber = form.getCardNumber();

        if ("4000000000000002".equals(cardNumber)) {
            errors.rejectValue("cardNumber", "error.cardNumber", "カードが拒否されました。別のカードをお試しください");
            model.addAttribute("title", "プレミアム会員アップグレード");
            return "members/club/upgrade";
        }

        if ("4000000000000069".equals(cardNumber)) {
            errors.rejectValue("expiry", "error.expiry", "このカードは有効期限切れとして扱われます");
            model.addAttribute("title", "プレミアム会員アップグレード");
            return "members/club/upgrade";
        }

        if (!"4242424242424242".equals(cardNumber)) {
            errors.rejectValue("cardNumber", "error.cardNumber", "利用できないダミーカード番号です");
            model.addAttribute("title", "プレミアム会員アップグレード");
            return "members/club/upgrade";
        }

        // 成功時のみプレミアム化
        memberUpgradeService.upgradeToPremium(memberId);
        session.setAttribute("memberTypeId", 2);

        return "redirect:/members/club/upgrade/result";
    }

    @GetMapping("/members/club/upgrade/result")
    public String showUpgradeResult(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        model.addAttribute("title", "アップグレード完了");
        return "members/club/upgradeResult";
    }

    @GetMapping("/members/club/downgrade")
    public String showDowngradePage(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        if (memberTypeId == null || memberTypeId != 2) {
            return "redirect:/members/club/home";
        }

        model.addAttribute("title", "プレミアム会員解約");
        return "members/club/downgrade";
    }

    @PostMapping("/members/club/downgrade/complete")
    public String completeDowngrade() {
        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        if (memberTypeId == null || memberTypeId != 2) {
            return "redirect:/members/club/home";
        }

        memberUpgradeService.downgradeToRegular(memberId);
        session.setAttribute("memberTypeId", 1);

        return "redirect:/members/club/downgrade/result";
    }

    @GetMapping("/members/club/downgrade/result")
    public String showDowngradeResult(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        model.addAttribute("title", "解約完了");
        return "members/club/downgradeResult";
    }
}