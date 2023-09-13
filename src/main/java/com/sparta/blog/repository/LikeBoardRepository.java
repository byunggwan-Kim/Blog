package com.sparta.blog.repository;

import com.sparta.blog.entity.LikeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long> {

    Optional<LikeBoard> findByBoardIdAndUserId(Long id, Long id1);
}
