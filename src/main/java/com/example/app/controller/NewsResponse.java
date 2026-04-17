package com.example.app.controller;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsResponse {
    private Integer id;
    private String title;
    private String author;
    private LocalDate postDate;
    private String article;
}