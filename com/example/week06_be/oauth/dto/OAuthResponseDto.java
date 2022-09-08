package com.example.week06_be.oauth.dto;

import com.example.week06_be.model.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthResponseDto {
    private String email;
    private String username;
    private String access_Token;
    private String refresh_Token;

    public OAuthResponseDto(Member member, String access_Token, String refresh_Token){
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.access_Token = access_Token;
        this.refresh_Token = refresh_Token;
    }
}