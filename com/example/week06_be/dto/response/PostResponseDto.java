package com.example.week06_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String imgUrl;
    private List<Referencelist> referenceList;        // Reference or ReferenceDto
//    private List<Commentlist> commentList;
    private int cntHeart;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
