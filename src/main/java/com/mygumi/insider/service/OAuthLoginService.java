package com.mygumi.insider.service;


import com.mygumi.insider.domain.oauth.*;
import com.mygumi.insider.dto.Member;
import com.mygumi.insider.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;


    public Map<String, Object> isMember(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = memberRepository.findByKakaoId(oAuthInfoResponse.getKakaoId()).map(Member::getId).orElseGet(() -> 0L);

        Map<String,Object> loginInfo = new HashMap<>();
        AuthTokens authTokens = null;
        boolean check = false;

        //만약 DB에 유저 정보가 없다면
        if(memberId == 0L){
            Long newMemberId = newMember(oAuthInfoResponse);
            authTokens = authTokensGenerator.generate(newMemberId);
        }

        //DB에 이미 유저가 있을 때
        else{
            authTokens = authTokensGenerator.generate(memberId);
            check = true;
        }

        loginInfo.put("jwt",authTokens);
        loginInfo.put("isPresent" , check);

        return loginInfo;
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .thumbnail_image_url(oAuthInfoResponse.get_thumbnail_image_url())
                .kakaoId(oAuthInfoResponse.getKakaoId())
                .build();
        return memberRepository.save(member).getId();
    }
}
