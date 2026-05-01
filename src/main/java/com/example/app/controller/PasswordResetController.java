package com.example.app.controller;

import jakarta.validation.Valid;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.Member;
import com.example.app.domain.PasswordResetToken;
import com.example.app.dto.PasswordResetForm;
import com.example.app.service.MemberService;
import com.example.app.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final MemberService memberService;

    @GetMapping("/reset")
    public String showForm(
            @RequestParam(required = false) String token,
            Model model) {

        if (token == null || token.isBlank()) {
            return "error/invalid-token";
        }

        PasswordResetToken resetToken = passwordResetService.findValidToken(token);

        if (resetToken == null) {
            return "error/invalid-token";
        }

        PasswordResetForm form = new PasswordResetForm();
        form.setToken(token);

        model.addAttribute("form", form);
        return "members/reset-password";
    }

    @PostMapping("/reset")
    public String submit(
            @ModelAttribute("form") @Valid PasswordResetForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            return "members/reset-password";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "確認用パスワードが一致しません"
            );
            return "members/reset-password";
        }

        PasswordResetToken resetToken = passwordResetService.findValidToken(form.getToken());

        if (resetToken == null) {
            return "error/invalid-token";
        }

        Member member = memberService.getMemberById(resetToken.getMemberId());

        if (member == null) {
            return "error/invalid-token";
        }

        member.setLoginPass(BCrypt.hashpw(form.getPassword(), BCrypt.gensalt()));
        memberService.updateCredentials(member);

        passwordResetService.deleteToken(form.getToken());

        return "redirect:/members/memberslogin";
    }
    @GetMapping("/forgot")
    public String showForgotForm() {
        return "members/forgot-password";
    }
    
    @PostMapping("/forgot")
    public String processForgot(
            @RequestParam String email,
            Model model) {

        Member member = memberService.getMemberByEmail(email);

        if (member == null) {
            model.addAttribute("error", "このメールアドレスは登録されていません。");
            return "members/forgot-password";
        }

        PasswordResetToken resetToken =
                passwordResetService.createToken(member.getId());

        model.addAttribute("token", resetToken.getToken());

        return "members/forgot-password-complete";
    }
}