package com.mygumi.insider.controller;


import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import com.mygumi.insider.dto.Member;
import com.mygumi.insider.repository.MemberRepository;
import com.mygumi.insider.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Api(value = "일반유저가 할 수 있는 역할에 대한 Controller")
public class MemberController {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final MemberService memberService;

    @ApiOperation(value = "해당 유저의 닉네임을 설정하는 API",
            notes = "Authorization: Bearer $[JWT Token], {nickName: $[바꾸고 싶은 닉네임]} 형식으로 보내준다 파라미터는 1개")
    @PutMapping("/makenickName")
    public ResponseEntity<Member> makeUserMadeNickName(@ApiParam(value = "유저 jwt 토큰") @RequestHeader("Authorization") String jwt,
                                                       @ApiParam(value = "바꾸고 싶은 nickName (map 형식)")@RequestBody Map<String,String> param){

        String accessToken = jwt.replaceAll("Bearer ", "");
        //System.out.println(accessToken+" "+param.get("nickName"));
        Long memberId = memberService.setUserNickName(param.get("nickName"), accessToken);
        return ResponseEntity.ok(memberRepository.findById(memberId).orElse(null));
    }

    @ApiOperation(value = "회원탈퇴라는 개념없이 일단은 카카오톡 어플에서 계정 연결 해제를 하는 방식으로 하기로 함",
                  notes = "논리적 탈퇴 valid = 1 -> 0으로 변경")
    @DeleteMapping("/quitService")
    public ResponseEntity<?> quitService(@RequestHeader("Authorization") String jwt){
        Long id = authTokensGenerator.extractMemberId(jwt);
        memberService.quitService(id);
        return ResponseEntity.ok(null);
    }
}
