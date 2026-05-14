package com.example.app.mapper;

import java.util.List;

import com.example.app.domain.News;

public interface NewsMapper {

    List<News> selectAll();

    List<News> selectLatestWithDetail(Integer limit);

    News selectById(Integer id);

    void insert(News news);

    void update(News news);

    int deleteById(Integer id);
}