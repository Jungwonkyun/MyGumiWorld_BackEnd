package com.mygumi.insider.service;


import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import com.mygumi.insider.dto.Member;
import com.mygumi.insider.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;

    public MemberService(MemberRepository memberRepository, AuthTokensGenerator authTokensGenerator) {
        this.memberRepository = memberRepository;
        this.authTokensGenerator = authTokensGenerator;
    }

    //유저가 원하는 닉네임 설정
    @Transactional
    public Long setUserNickName(String UsernickName, String accessToken) {
        //System.out.println("accessToken: "+accessToken);
        Long id = authTokensGenerator.extractMemberId(accessToken);
        Optional<Member> oMember = memberRepository.findById(id);

        if(oMember.isEmpty()) return 0L;

        Member member = oMember.get();
        member.setUsernickname(UsernickName);

        //System.out.println(member.toString());
        return  memberRepository.save(member).getId();
    }

    @Transactional
    public void quitService(Long id){
        Optional<Member> oMember = memberRepository.findById(id);

        if(oMember.isEmpty()) return;

        Member member = oMember.get();
        member.setValid(0);
    }

}
