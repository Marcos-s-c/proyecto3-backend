package com.sistema.venus.repo;

import com.sistema.venus.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Post getPostByPostId(Long postId);
}
