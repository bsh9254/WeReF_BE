package com.example.week06_be.service;

import com.example.week06_be.dto.ResponseDto;
import com.example.week06_be.dto.request.CommentRequestDto;
import com.example.week06_be.dto.response.CommentResponseDto;
import com.example.week06_be.dto.response.Commentlist;
import com.example.week06_be.model.Comment;
import com.example.week06_be.model.Member;
import com.example.week06_be.model.Post;
import com.example.week06_be.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberservice;
    private final PostService postService;

    @Transactional
    public ResponseDto<?> getAllCommentByPost(Long postId)
    {
        Post post = postService.isPresentPost(postId);
        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<Commentlist> commentLists = new ArrayList<>();

        for (Comment comment : commentList) {
            commentLists.add(
                    Commentlist.builder()
                            .id(comment.getId())
                            .username(comment.getMember().getUsername())
                            .comment(comment.getComment())
                            .createdAt(comment.getCreatedAt())
                            .build()
            );
        }
        return ResponseDto.success(CommentResponseDto.builder()
                .commentlist(commentLists)
                .build());
    }
    @Transactional
    public ResponseDto<?> creatComment(Long postId,CommentRequestDto commentRequestDto, HttpServletRequest request)
    {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        Member member= memberservice.getMemberfromContext();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Post post= postService.isPresentPost(postId);
        Comment comment=Comment
                .builder()
                .post(post)
                .comment(commentRequestDto.getComment())
                .member(member)
                .editCheck(false)
                .build();
        commentRepository.save(comment);
        return ResponseDto.success(Commentlist
                .builder()
                .id(comment.getId())
                .username(comment.getMember().getUsername())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .editCheck(comment.isEditCheck())
                .build()
        );

    }
    @Transactional
    public ResponseDto<?> updateComment(Long commentId,CommentRequestDto commentRequestDto, HttpServletRequest request)
    {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        Member member= memberservice.getMemberfromContext();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = isPresentComment(commentId);
        comment.update(commentRequestDto);

        return ResponseDto.success(Commentlist
                .builder()
                .id(comment.getId())
                .username(comment.getMember().getUsername())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .editCheck(comment.isEditCheck())
                .build()
        );

    }
    @Transactional
    public ResponseDto deleteComment(Long commentId,HttpServletRequest request)
    {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }
        Member member= memberservice.getMemberfromContext();
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Comment comment=isPresentComment(commentId);
        commentRepository.delete(comment);

        return ResponseDto.success("Success to "+commentId+" delete");
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

}
