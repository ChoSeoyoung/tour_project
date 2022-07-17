package com.example.tourservice.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(length=100, nullable=false)
    private String title;

    private int cost;

    @Column(length=100, nullable=false)
    private String content;

    @Builder
    public Posts(String title, Integer cost, String content) {
        this.title = title;
        this.cost = cost;
        this.content = content;
    }

    public void update(String title, Integer cost, String content){
        this.title=title;
        this.cost=cost;
        this.content=content;
    }
}