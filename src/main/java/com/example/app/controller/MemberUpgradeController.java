package com.example.app.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.service.MemberUpgradeService;
import com.example.app.service.StripeCheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberUpgradeController {

    private final MemberUpgradeService memberUpgradeService;
    private final StripeCheckoutService stripeCheckoutService;
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
        return "members/club/upgrade";
    }

    @PostMapping("/members/club/upgrade/checkout")
    public String startStripeCheckout(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        if (memberTypeId != null && memberTypeId == 2) {
            return "redirect:/members/club/home";
        }

        try {
            Session checkoutSession = stripeCheckoutService.createPremiumCheckoutSession(memberId);
            return "redirect:" + checkoutSession.getUrl();

        } catch (StripeException e) {
            e.printStackTrace();

            model.addAttribute("title", "プレミアム会員アップグレード");
            model.addAttribute("errorMessage", "決済画面の作成に失敗しました。時間をおいて再度お試しください。");

            return "members/club/upgrade";
        }
    }

    @GetMapping("/members/club/upgrade/success")
    public String completeStripeUpgrade(
            @RequestParam("session_id") String sessionId,
            Model model) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        try {
            Session checkoutSession = stripeCheckoutService.retrieveSession(sessionId);

            String paymentStatus = checkoutSession.getPaymentStatus();

            if (!"paid".equals(paymentStatus)) {
                model.addAttribute("title", "プレミアム会員アップグレード");
                model.addAttribute("errorMessage", "決済が完了していません。もう一度お試しください。");
                return "members/club/upgrade";
            }

            memberUpgradeService.upgradeToPremium(memberId);
            session.setAttribute("memberTypeId", 2);

            return "redirect:/members/club/upgrade/result";

        } catch (StripeException e) {
            e.printStackTrace();

            model.addAttribute("title", "プレミアム会員アップグレード");
            model.addAttribute("errorMessage", "決済結果の確認に失敗しました。時間をおいて再度お試しください。");

            return "members/club/upgrade";
        }
    }

    @GetMapping("/members/club/upgrade/cancel")
    public String cancelStripeUpgrade(Model model) {
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            return "redirect:/members/memberslogin";
        }

        model.addAttribute("title", "プレミアム会員アップグレード");
        model.addAttribute("errorMessage", "決済がキャンセルされました。必要に応じて再度お試しください。");

        return "members/club/upgrade";
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