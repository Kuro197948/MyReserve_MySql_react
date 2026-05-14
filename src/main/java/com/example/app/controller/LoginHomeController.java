package com.example.app.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.app.dto.AdminLoginForm;

@Controller
public class LoginHomeController {

    @GetMapping("/")
    public String loginHome() {
        return "redirect:/loginhome";
    }

    @GetMapping({"/loginhome", "/loginHome"})
    public String showLoginHome(HttpSession session, Model model) {

        model.addAttribute("admin", new AdminLoginForm());

        if (session.getAttribute("loginId") != null) {
            model.addAttribute("adminLoggedIn", true);
        }

        return "loginhome";
    }
}