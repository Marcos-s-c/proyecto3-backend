package com.sistema.venus.repo;

import com.sistema.venus.domain.Comment;
import com.sistema.venus.domain.Medication;
import com.sistema.venus.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT p FROM Comment p JOIN p.user_id u WHERE p.post_id = :post_id" )
    List<Comment> getCommentsByPost(Long post_id);
}
