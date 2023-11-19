package com.sistema.venus.services;

import com.sistema.venus.domain.Comment;
import com.sistema.venus.repo.CommentRepository;
import com.sistema.venus.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentsRepository.getCommentsByPost(postId);
    }

    public Comment addComment(Comment newComment) {
        newComment.setUser_id( userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        return commentsRepository.save(newComment);
    }
}
