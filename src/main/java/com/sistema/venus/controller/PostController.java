package com.sistema.venus.controller;

import com.sistema.venus.domain.Post;
import com.sistema.venus.services.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/post")
public class PostController {
    @Autowired
    private PostService postService;

    Logger logger = LoggerFactory.getLogger(PostController.class);

    @PostMapping(value = "create")
    public ResponseEntity<Object> create(@RequestPart(name = "postId", required = false) String postId,
            @RequestPart(name = "file", required = false) MultipartFile file, @RequestPart("subject") String subject,
            @RequestPart("content") String content) throws IOException, ValidationException {
        try {
            postService.upsertPost(postId, file, subject, content);
            Map<String, Boolean> map = new HashMap<>();
            map.put("Success", true);
            return ResponseEntity.of(Optional.of(map));
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(Optional.of(e));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Optional.of("Ha ocurrido un error salvando el post"));
        }
    }

    @GetMapping("getPost")
    public Post getPostById(@RequestParam Long postId) {
        try {
            return postService.getPostById(postId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("getAllPosts")
    public List<Post> getAllPosts(@RequestParam(required = false) String searchParam, @RequestParam(required = false) String sortBy) {
        try {
            return postService.getAllPosts(searchParam, sortBy);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("likePost")
    public ResponseEntity<Object> likePost(@RequestParam Long postId) {
        try {
            postService.likePost(postId);
            Map<String, Boolean> map = new HashMap<>();
            map.put("Success", true);
            return ResponseEntity.of(Optional.of(map));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("borrar/{postId}")
    public ResponseEntity<Object> borrarPost(@PathVariable(value = "postId") Long postId){
        try{
            postService.borrarPost(postId);
            Map<String, String> map = new HashMap<String, String>();
            map.put("Borrado", "Success");
            return ResponseEntity.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
