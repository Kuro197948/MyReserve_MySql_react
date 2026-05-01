package com.example.app.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.MemberRegisterForm;
import com.example.app.domain.MemberRegisterResult;
import com.example.app.service.MemberRegisterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberRegisterController {

    private final MemberRegisterService memberRegisterService;

    @GetMapping("/members/register")
    public String showRegister(Model model) {
        model.addAttribute("form", new MemberRegisterForm());
        return "members/register";
    }

    @PostMapping("/members/register")
    public String register(
            @ModelAttribute("form") @Valid MemberRegisterForm form,
            BindingResult errors,
            Model model) {

        if (errors.hasErrors()) {
            return "members/register";
        }

        try {
            MemberRegisterResult result = memberRegisterService.register(form);

            model.addAttribute("member", result.getMember());
            model.addAttribute("token", result.getPasswordResetToken().getToken());

            return "members/registerComplete";

        } catch (IllegalArgumentException e) {
            errors.rejectValue("email", "error.duplicate.email", e.getMessage());
            return "members/register";
        }
    }
}