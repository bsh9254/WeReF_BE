package com.example.week06_be.controller;

import com.example.week06_be.dto.ResponseDto;
import com.example.week06_be.dto.request.PostRequestDto;
import com.example.week06_be.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    //@Secured("ROLE_USER")
    @RequestMapping(value = "/api/auth/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestPart MultipartFile multipartFile, @RequestPart PostRequestDto requestDto,
                                     HttpServletRequest request) {
        return postService.createPost(multipartFile, requestDto, request);
    }
    @GetMapping(value="/api/post")
    public ResponseDto<?> getAllPost() {
        return postService.getAllPost();
    }

    //@Secured("ROLE_USER")
    @GetMapping(value="/api/post/{postId}")
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    //@Secured("ROLE_USER")
    @PutMapping(value="/api/auth/post/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestPart MultipartFile multipartFile, @RequestPart PostRequestDto requestDto, HttpServletRequest request) {
        return postService.updatePost(postId, multipartFile, requestDto, request);
    }

    //@Secured("ROLE_USER")
    @DeleteMapping(value="/api/auth/post/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return postService.deletePost(postId, request);
    }

    //@Secured("ROLE_USER")
    @PostMapping(value="/api/auth/heart/{postId}")
    public ResponseDto<?> clickHeart(@PathVariable Long postId, HttpServletRequest request) {
        return postService.clickHeart(postId, request);
    }
    
    @GetMapping(value="/api/auth/heart/{postId}")
    public ResponseDto<?> checkHeart(@PathVariable Long postId, HttpServletRequest request) {
        return postService.checkHeart(postId, request);
    }

}
