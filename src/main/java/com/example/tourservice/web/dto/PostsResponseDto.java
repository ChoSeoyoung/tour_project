package com.example.tourservice.web.dto;

import com.example.tourservice.domain.posts.Posts;
import lombok.Getter;

@Getter
public class PostsResponseDto {
    private Long id;
    private String title;
    private int cost;
    private String content;

    public PostsResponseDto(Posts entity){
        this.id=entity.getPostId();
        this.title=entity.getTitle();
        this.cost=entity.getCost();
        this.content=entity.getContent();
    }
}
