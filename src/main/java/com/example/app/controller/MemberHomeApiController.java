package com.example.app.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.app.domain.News;
import com.example.app.domain.Reservation;
import com.example.app.service.NewsService;
import com.example.app.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member/home")
@RequiredArgsConstructor
public class MemberHomeApiController {

    private final NewsService newsService;
    private final ReservationService reservationService;

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

    @GetMapping("/next-reservation")
    public NextReservationResponse getNextReservation(HttpSession session) {

        Integer memberId = (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Reservation nextReservation =
                reservationService.findNextReservationByMemberId(memberId);

        if (nextReservation == null) {
            return null;
        }

        return new NextReservationResponse(
                nextReservation.getId(),
                nextReservation.getCourseName(),
                nextReservation.getReservationDate(),
                nextReservation.getDaysUntilReservationLabel(),
                nextReservation.getDaysUntilReservationClass()
        );
    }

    public record NextReservationResponse(
            Integer id,
            String courseName,
            LocalDateTime reservationDate,
            String daysUntilReservationLabel,
            String daysUntilReservationClass
    ) {
    }
}