package com.example.tourservice.web.dto.Posts;

import com.example.tourservice.domain.posts.Posts;
import lombok.Getter;

@Getter
public class PostsListResponseDto {
    private Long id;
    private String title;
    private int cost;
    private String content;

    public PostsListResponseDto(Posts entity){
        this.id=entity.getPostId();
        this.title=entity.getTitle();
        this.cost=entity.getCost();
        this.content=entity.getContent();
    }
}
