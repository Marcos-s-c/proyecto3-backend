package com.sistema.venus.controller;

import com.sistema.venus.domain.Post;
import com.sistema.venus.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/post")
public class PostController {
    @Autowired
    private PostService postService;
    @PostMapping(value = "create")
    public ResponseEntity<Object> create(@RequestPart(name="postId",required = false)String postId, @RequestPart(name = "file", required = false) MultipartFile file,@RequestPart("subject") String subject,@RequestPart("content") String content) throws IOException {
        try{
            postService.savePost(postId, file,subject,content);
            Map<String,Boolean> map = new HashMap<>();
            map.put("Success",true);
            return ResponseEntity.of(Optional.of(map));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("getPost")
    public Post getPostById(@RequestParam Long postId){
        try{
            return postService.getPostById(postId);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
