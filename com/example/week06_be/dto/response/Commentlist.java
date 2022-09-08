package com.example.week06_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Commentlist {
    private Long id;
    private String username;
    private String comment;

    private LocalDateTime createdAt;

    private boolean editCheck;
}
