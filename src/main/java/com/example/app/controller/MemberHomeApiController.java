package com.example.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.News;
import com.example.app.service.NewsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member/home")
@RequiredArgsConstructor
public class MemberHomeApiController {

    private final NewsService newsService;

    @GetMapping("/news")
    public List<NewsResponse> getLatestNews(
            @RequestParam(defaultValue = "3") int limit) {

        List<News> newsList = newsService.getLatestWithDetail(limit);

        return newsList.stream()
                .map(news -> new NewsResponse(
                        news.getId(),
                        news.getTitle(),
                        news.getAuthor(),
                        news.getPostDate(),
                        news.getDetail() != null ? news.getDetail().getArticle() : null
                ))
                .collect(Collectors.toList());
    }
}