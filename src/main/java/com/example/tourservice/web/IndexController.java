package com.example.tourservice.web;

import com.example.tourservice.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {
    private final PostsService postsService;
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/posts")
    public String index(Model model){
        model.addAttribute("posts",postsService.findAllDesc());
        return "posts/posts";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts/savePost";
    }
}
