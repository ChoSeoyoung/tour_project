package com.example.tourservice.web.dto;

import com.example.tourservice.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private int cost;
    private String content;

    @Builder
    public PostsSaveRequestDto(String title, Integer cost, String content) {
        this.title=title;
        this.cost=cost;
        this.content=content;
    }

    public Posts toEntity(){
        return Posts.builder()
                .title(title)
                .cost(cost)
                .content(content)
                .build();
    }
}
