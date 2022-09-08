package com.example.week06_be.service;

import com.example.week06_be.dto.ResponseDto;
import com.example.week06_be.dto.request.PostRequestDto;
import com.example.week06_be.dto.response.Commentlist;
import com.example.week06_be.dto.response.PostResponseDto;
import com.example.week06_be.dto.response.Referencelist;
import com.example.week06_be.jwt.TokenProvider;
import com.example.week06_be.model.*;
import com.example.week06_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ReferenceRepository referenceRepository;
    private final MemberService memberService;
    private final HeartRepository heartRepository;
    private final FileUploadService fileUploadService;
    private final TokenProvider tokenProvider;

    
    @Transactional
    public ResponseDto<?> createPost(MultipartFile multipartFile, PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        Member member= memberService.getMemberfromContext();


        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .imgUrl(fileUploadService.uploadImage(multipartFile))
                .member(member)
                .build();
        postRepository.save(post);

        List<String> refUrls = requestDto.getRefUrl();
        String refUrl;

        for(int i = 0; i <refUrls.toArray().length; i++) {
            refUrl = refUrls.get(i);
            Reference reference = Reference.builder()
                    .refUrl(refUrl)
                    .member(member)     // 마이페이지에서 본인이 올린 reference 바로? 글 통해서 걸러서?
                    .post(post)
                    .build();
            referenceRepository.save(reference);
        }

        return ResponseDto.success("성공적으로 등록되었습니다. 글번호: "+ post.getId());
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            int cntHeart = heartRepository.countHeartByPostId(post.getId());
            postResponseDtoList.add(PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .author(post.getMember().getUsername())
                    .imgUrl(post.getImgUrl())
                    .cntHeart(cntHeart)
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
            );
        }

        return ResponseDto.success(postResponseDtoList);

    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 Id 입니다.");
        }
        List<Reference> referenceList = referenceRepository.findAllByPost(post);        //id로 찾는게 효율적?
        List<Referencelist> referencelistList = new ArrayList<>();

        for (Reference reference : referenceList) {
            referencelistList.add(
                    Referencelist.builder()
                            .id(reference.getId())
                            .refUrl(reference.getRefUrl())
                            .build()
            );
        }
        
        int cntHeart = heartRepository.countHeartByPostId(post.getId());

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .description(post.getDescription())
                        .author(post.getMember().getUsername())
                        .imgUrl(post.getImgUrl())
                        .referenceList(referencelistList)
//                        .commentList(commentLists)
                        .cntHeart(cntHeart)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
                );

    }

    @Transactional
    public ResponseDto<?> updatePost(Long postId, MultipartFile multipartFile, PostRequestDto requestDto, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }


        Member member= memberService.getMemberfromContext();

        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }
        
        

        //새로 추가된 url만 받아오기
        List<String> refUrls = requestDto.getRefUrl();
        String refUrl;

        for(int i = 0; i <refUrls.toArray().length; i++) {
            refUrl = refUrls.get(i);
            Reference reference = Reference.builder()
                    .refUrl(refUrl)
                    .member(member)
                    .post(post)
                    .build();
            referenceRepository.save(reference);
        }

        String imgUrl = fileUploadService.uploadImage(multipartFile);
        post.update(requestDto, imgUrl);
        
        postRepository.save(post);

        // 레퍼런스 삭제는 마지막으로 하기 (프론트에서 "-" 누르다가 글수정을 취소할 경우 고려)

        return ResponseDto.success("성공적으로 수정되었습니다. 글번호: "+ post.getId());    // 글 전체 데이터 받아오기?
    }

    @Transactional
    public ResponseDto<?> deletePost(Long postId, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {

            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        Member member= memberService.getMemberfromContext();


        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
        return ResponseDto.success("성공적으로 삭제되었습니다. 글번호: "+ post.getId());
    }

    @Transactional
    public ResponseDto<?> clickHeart(Long postId, HttpServletRequest request) {

        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
        }

        Member member= memberService.getMemberfromContext();


        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Heart isDoneHeart = heartRepository.findByPostIdAndMemberId(postId, member.getId());

        if (null != isDoneHeart) {
            heartRepository.delete(isDoneHeart);
            return ResponseDto.success(heartRepository.countHeartByPostId(postId));
        }

        Heart heart = Heart.builder()
                .member(member)
                .post(isPresentPost(postId))
                .build();

        heartRepository.save(heart);

        return ResponseDto.success(heartRepository.countHeartByPostId(postId)); 
    }
    
    @Transactional
    public ResponseDto<?> checkHeart(Long postId, HttpServletRequest request) {
        Member member= memberService.getMemberfromContext();      

        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Heart isDoneHeart = heartRepository.findByPostIdAndMemberId(postId, member.getId());

        if (null != isDoneHeart) {
            return ResponseDto.success(member.getUsername());
        } else {
            return ResponseDto.success(false);
        }
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
}
