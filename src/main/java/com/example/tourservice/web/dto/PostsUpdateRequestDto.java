package com.example.tourservice.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
    private String title;
    private int cost;
    private String content;

    @Builder
    public PostsUpdateRequestDto(String title, Integer cost, String content) {
        this.title = title;
        this.cost = cost;
        this.content = content;
    }
}
