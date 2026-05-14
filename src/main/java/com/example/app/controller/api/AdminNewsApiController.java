package com.example.app.controller.api;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.News;
import com.example.app.domain.NewsForm;
import com.example.app.service.NewsService;

@RestController
@RequestMapping("/api/admin/announcements")
public class AdminNewsApiController {

    private final NewsService newsService;

    public AdminNewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        List<News> newsList = newsService.getNewsList();

        return ResponseEntity.ok(newsList);
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

        News news = newsService.getNewsById(id);

        if (news == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "お知らせが見つかりません。"));
        }

        return ResponseEntity.ok(news);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @ModelAttribute NewsForm newsForm,
            HttpSession session) {

        Object loginId = session.getAttribute("loginId");

        if (loginId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        newsForm.setAuthor(loginId.toString());

        newsService.addNews(newsForm);

        return ResponseEntity.ok(Map.of("message", "新着情報を追加しました。"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @ModelAttribute NewsForm newsForm,
            HttpSession session) {

        Object loginId = session.getAttribute("loginId");

        if (loginId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        News news = newsService.getNewsById(id);

        if (news == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "お知らせが見つかりません。"));
        }

        newsForm.setAuthor(loginId.toString());

        newsService.updateNews(id, newsForm);

        return ResponseEntity.ok(Map.of("message", "お知らせを更新しました。"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("loggedIn", false));
        }

        newsService.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "お知らせを削除しました。"));
    }
}