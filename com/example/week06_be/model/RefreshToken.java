package com.example.week06_be.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private String kkey;   //데이터베이스 예약어와 겹치는 문제때문에 변수명을 안겹치게 설정하였다.
    private String vvalue;

    public RefreshToken updateValue(String token) {
        this.vvalue = token;
        return this;
    }

    @Builder
    public RefreshToken(String key, String value) {
        this.kkey = key;
        this.vvalue = value;
    }
}