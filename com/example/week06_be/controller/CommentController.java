package com.example.week06_be.controller;



import com.example.week06_be.dto.request.CommentRequestDto;
import com.example.week06_be.dto.ResponseDto;
import com.example.week06_be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController

public class CommentController {

    private final CommentService commentService;


    @GetMapping(value="/api/comment/{postId}")
    public ResponseDto<?> getAllCommentByPost(@PathVariable Long postId) {
        return commentService.getAllCommentByPost(postId);
    }

    //@Secured("ROLE_USER")
    @PostMapping("/api/auth/comment/{postId}")
    public ResponseDto<?> creatComment(@PathVariable Long postId,@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {

        return commentService.creatComment(postId,commentRequestDto,request);
    }

    @PutMapping("/api/auth/comment/edit/{commentId}")
    public ResponseDto<?> updateComment(@PathVariable Long commentId,@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request)
    {

        return commentService.updateComment(commentId,commentRequestDto,request);
    }
    @DeleteMapping("/api/auth/comment/delete/{commentId}")
    public ResponseDto<?> deleteComment(@PathVariable Long commentId,HttpServletRequest request){

        return commentService.deleteComment(commentId,request);
    }
}
