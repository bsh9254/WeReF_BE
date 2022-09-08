package com.example.week06_be.repository;

import com.example.week06_be.model.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    int countHeartByPostId(Long postId);
    Heart findByPostIdAndMemberId(Long postId, Long memberId);
}
