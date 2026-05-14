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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.NewsForm;
import com.example.app.service.MemberService;
import com.example.app.service.NewsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admins/club")
@RequiredArgsConstructor
public class NewsController {

    private static final String REACT_ADMIN_BASE_URL = "http://localhost:5175/admin";

    private final NewsService newsService;
    private final MemberService memberService;

    @GetMapping("/announcements")
    public String list(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/announcements";
    }

    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/announcements/" + id;
    }

    @GetMapping("/save")
    public String addGet(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:" + REACT_ADMIN_BASE_URL + "/announcements/new";
    }

    @PostMapping("/save")
    public String addPost(
            HttpSession session,
            @Valid NewsForm newsForm,
            Errors errors,
            RedirectAttributes ra,
            Model model) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        MultipartFile upfile = newsForm.getUpfile();

        if (upfile != null && !upfile.isEmpty()) {
            String type = upfile.getContentType();

            if (type == null || !type.startsWith("image/")) {
                errors.rejectValue("upfile", "error.not_image_file");
            }
        }

        if (errors.hasErrors()) {
            model.addAttribute("memberTypeList", memberService.getTypeList());
            return "admins/club/save";
        }

        newsForm.setAuthor((String) session.getAttribute("loginId"));

        newsService.addNews(newsForm);

        ra.addFlashAttribute("statusMessage", "お知らせを追加しました。");

        return "redirect:" + REACT_ADMIN_BASE_URL + "/announcements";
    }

    @PostMapping("/delete/{id}")
    public String deleteNews(
            @PathVariable Integer id,
            HttpSession session,
            RedirectAttributes ra) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        newsService.deleteById(id);

        ra.addFlashAttribute("statusMessage", "削除しました");

        return "redirect:" + REACT_ADMIN_BASE_URL + "/announcements";
    }
}