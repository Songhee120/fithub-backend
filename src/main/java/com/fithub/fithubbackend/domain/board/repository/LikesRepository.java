package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Likes;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    void deleteByUserAndPost(User user, Post post);

    Optional<Likes> findByUserAndPost(User user, Post post);

    List<Likes> findByUserOrderByCreatedDateDesc(User user);

    List<Likes> findByPostOrderByCreatedDateAsc(Post post);

    @Query(value = "SELECT EXISTS (" +
            "SELECT *" +
            "FROM likes l " +
            "WHERE l.post_id = :postId AND l.user_id = :userId );", nativeQuery = true)
    Integer existsByUserIdAndPostId(@Param("userId") long userId, @Param("postId") long postId);
}
