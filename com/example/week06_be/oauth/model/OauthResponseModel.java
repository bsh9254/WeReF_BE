package com.example.week06_be.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OauthResponseModel {

    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private List<?> data;
}