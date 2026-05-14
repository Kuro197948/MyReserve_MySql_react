package com.example.app.controller.api;

import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Member;
import com.example.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberApiController {

    private final MemberService memberService;

    @GetMapping("/types")
    public ResponseEntity<?> types(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        return ResponseEntity.ok(memberService.getTypeList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        Member member = memberService.getMemberById(id);

        if (member == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "会員情報が見つかりません。"));
        }

        return ResponseEntity.ok(member);
    }
    
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        int numPerPage = 5;

        return ResponseEntity.ok(Map.of(
                "members", memberService.getMemberListByPage(page, numPerPage),
                "page", page,
                "totalPages", memberService.getTotalPages(numPerPage)
        ));
    }
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody Member member,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        memberService.addMember(member);

        return ResponseEntity.ok(Map.of("message", "会員を追加しました。"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @Valid @RequestBody Member member,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        member.setId(id);
        memberService.editMember(member);

        return ResponseEntity.ok(Map.of("message", "会員情報を更新しました。"));
    }
}