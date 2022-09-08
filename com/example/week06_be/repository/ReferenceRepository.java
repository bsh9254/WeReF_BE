package com.example.week06_be.repository;

import com.example.week06_be.model.Comment;
import com.example.week06_be.model.Post;
import com.example.week06_be.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    List<Reference> findAllByPost(Post post);
}
