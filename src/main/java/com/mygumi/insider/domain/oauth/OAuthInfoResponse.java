package com.mygumi.insider.domain.oauth;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    Long getKakaoId();
    String get_thumbnail_image_url();
    OAuthProvider getOAuthProvider();
}
