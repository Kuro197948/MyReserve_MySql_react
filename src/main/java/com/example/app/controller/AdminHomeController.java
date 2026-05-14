package com.example.app.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    @GetMapping("/admins/club/home")
    public String home(HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/admins/adminslogin";
        }

        return "redirect:http://localhost:5175/admin/home";
    }
}