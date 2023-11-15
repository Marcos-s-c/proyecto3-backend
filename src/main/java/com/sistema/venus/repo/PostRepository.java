package com.sistema.venus.repo;

import com.sistema.venus.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Post getPostByPostId(Long postId);
    @Query("SELECT p.postId FROM Post p LEFT JOIN p.likes u WHERE u.emailId = :email")
    List<Long> getLikedPostsByUser(String email);
}
