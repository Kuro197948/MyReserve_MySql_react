package com.example.app.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminAuthApiController {

    private final HttpSession session;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {

        Map<String, Object> response = new HashMap<>();

        session.invalidate();

        response.put("success", true);
        response.put("message", "ログアウトしました");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {

        Map<String, Object> response = new HashMap<>();

        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            response.put("loggedIn", false);
            return ResponseEntity.ok(response);
        }

        response.put("loggedIn", true);
        response.put("loginId", loginId);

        return ResponseEntity.ok(response);
    }
}