package com.example.app.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Member;
import com.example.app.domain.MemberLoginForm;
import com.example.app.service.MemberAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberAuthApiController {

    private final MemberAuthService service;
    private final HttpSession session;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberLoginForm form) {

        Map<String, Object> response = new HashMap<>();

        if (form == null
                || !StringUtils.hasText(form.getEmail())
                || !StringUtils.hasText(form.getLoginPass())) {

            response.put("success", false);
            response.put("message", "メールアドレスとパスワードを入力してください");
            return ResponseEntity.badRequest().body(response);
        }

        Member member = service.login(form.getEmail(), form.getLoginPass());

        if (member == null) {
            response.put("success", false);
            response.put("message", "メールアドレスまたはパスワードが正しくありません");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        session.setAttribute("memberId", member.getId());
        session.setAttribute("memberTypeId", member.getType().getId());
        session.setAttribute("memberName", member.getName());

        response.put("success", true);
        response.put("memberId", member.getId());
        response.put("memberName", member.getName());

        return ResponseEntity.ok(response);
    }

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

        Integer memberId = (Integer) session.getAttribute("memberId");
        Integer memberTypeId = (Integer) session.getAttribute("memberTypeId");
        String memberName = (String) session.getAttribute("memberName");

        if (memberId == null) {
            response.put("loggedIn", false);
            return ResponseEntity.ok(response);
        }

        response.put("loggedIn", true);
        response.put("memberId", memberId);
        response.put("memberTypeId", memberTypeId);
        response.put("memberName", memberName);

        return ResponseEntity.ok(response);
    }
}