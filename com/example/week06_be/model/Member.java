package com.example.week06_be.model;


import com.example.week06_be.Timestamped;
import com.example.week06_be.oauth.dto.GoogleUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor

@Entity
public class Member extends Timestamped {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO) //GenerationType.IDENTITY : ID값이 서로 영향없이 자기만의 테이블 기준으로 올라간다.
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;


    private String email;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String provider;// oauth2를 이용할 경우 어떤 플랫폼을 이용하는지
    private String providerId;// oauth2를 이용할 경우 아이디값

    @Builder
    public Member(String username, String password, Authority authority) {
        this.username = username;
        this.password = password;
        this.authority = authority;

    }
    public Member(GoogleUser googleUser)
    {
        this.username=googleUser.getName();
        this.email=googleUser.getEmail();
        this.password="googlelogin";
        this.provider="Google";
    }

    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public Member(String username, String password, String email, Authority authority, String provider, String providerId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authority = authority;
        this.provider = provider;
        this.providerId = providerId;
    }
}