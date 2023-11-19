package com.sistema.venus.controller;

import com.sistema.venus.domain.Comment;
import com.sistema.venus.services.CommentService;
import com.sistema.venus.services.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/comments")
@CrossOrigin("*")
public class CommentsController {
    @Autowired
    CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody Comment commentToAdd) {
        System.out.println(commentToAdd);
        Comment newComment = commentService.addComment(commentToAdd);
        System.out.println(newComment);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    @GetMapping("/getByPostId/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
