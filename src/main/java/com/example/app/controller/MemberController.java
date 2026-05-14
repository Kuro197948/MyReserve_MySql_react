package com.example.app.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Member;
import com.example.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/admins/club")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private static final String REACT_ADMIN_BASE_URL = "http://localhost:5175/admin";

    // 1ページあたりの表示人数
    private final int NUM_PER_PAGE = 5;

    private final MemberService service;

    @GetMapping("/memberslist")
    public String list(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members";
    }

    @GetMapping("/members/add")
    public String addGet(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members/new";
    }

    @PostMapping("/members/add")
    public String addPost(
            HttpSession session,
            @Valid Member member,
            Errors errors,
            RedirectAttributes rd,
            Model model) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "会員の追加");
            model.addAttribute("types", service.getTypeList());
            return "admins/club/membersave";
        }

        service.addMember(member);
        rd.addFlashAttribute("statusMessage", "会員を追加しました。");

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members";
    }

    @GetMapping("/edit/{id}")
    public String editGet(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members/" + id + "/edit";
    }

    @PostMapping("/edit/{id}")
    public String editPost(
            @PathVariable Integer id,
            HttpSession session,
            @Valid Member member,
            Errors errors,
            RedirectAttributes rd,
            Model model) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "会員情報の変更");
            model.addAttribute("types", service.getTypeList());
            return "admins/club/membersave";
        }

        member.setId(id);
        service.editMember(member);
        rd.addFlashAttribute("statusMessage", "会員情報を更新しました。");

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members";
    }

    @GetMapping("/delete/{id}")
    public String delete(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes rd) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        service.deleteMember(id);
        rd.addFlashAttribute("statusMessage", "会員情報を削除しました。");

        return "redirect:" + REACT_ADMIN_BASE_URL + "/members";
    }
}