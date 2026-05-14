package com.example.app.mapper;

import com.example.app.domain.NewsDetail;

public interface NewsDetailMapper {

    void insert(NewsDetail detail);

    void update(NewsDetail detail);

    int deleteByNewsId(Integer newsId);

    NewsDetail selectByNewsId(Integer newsId);
}