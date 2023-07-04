package com.mygumi.insider.dto;


import javax.persistence.*;

import com.mygumi.insider.domain.oauth.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;


@Setter
@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    private String usernickname;

    private String thumbnail_image_url;

    private Long kakaoId;

    private String oAuthProvider;

    private int valid;

    @Builder
    public Member(String email, String nickname, OAuthProvider oAuthProvider, String thumbnail_image_url, Long kakaoId) {
        this.email = email;
        this.nickname = nickname;
        this.oAuthProvider = oAuthProvider.toString();
        this.thumbnail_image_url = thumbnail_image_url;
        this.kakaoId = kakaoId;
        this.valid = 1;
    }

    @Override
    public String toString(){
        return this.email+" "+this.nickname+" "+this.usernickname+" "+this.oAuthProvider;
    }
}
