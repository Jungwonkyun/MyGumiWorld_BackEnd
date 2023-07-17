package com.mygumi.insider.controller;



import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import com.mygumi.insider.dto.Member;
import com.mygumi.insider.repository.MemberRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Api(value = "관리자 Role에 해당하는 Controller")
public class AdminController {

    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;

    //모든 맴버 정보 리턴
    @ApiOperation(value = "현재 서비스에 가입한 모든 유저의 정보를 보여준다")
    @GetMapping("/loadAllUsers")
    public ResponseEntity<List<Member>> findAll() {
        return ResponseEntity.ok(memberRepository.findAll());
    }

    //id를 통해서 맴버 삭제
    @ApiOperation(value = "관리자가 직접 유저를 삭제" , notes = "아직 사용하지 않는 API")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Integer> deleteUser (@ApiParam(value = "해당 유저 ID") @PathVariable("id") long id) throws Exception{
        try {
            memberRepository.deleteById(id);
            return ResponseEntity.ok(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok(0);
    }

    //accessToken으로 유저 정보 찾기
    @ApiOperation(value = "유저의 jwt 토큰을 파라미터로 받아서 member에 대한 객체 정보를 반환",
            notes = "DB를 직접 보지 않고 JWT 토큰만 있으면 객체에 대한 값들을 알 수 있음")
    @GetMapping("/{accessToken}")
    public ResponseEntity<Member> findByAccessToken(@ApiParam(value = "해당 유저의 JWT 토큰") @RequestHeader("Authorization") String jwt) {
        Long memberId = authTokensGenerator.extractMemberId(jwt);
        return ResponseEntity.ok(memberRepository.findById(memberId).orElse(null));
    }

    //id를 가지고 유저 정보 찾기
    @ApiOperation(value = "유저의 id값을 header로 보내서 member에 대한 객체 정보를 반환",
            notes = "앞의 API는 JWT Token으로 이번 API는 유저 ID로만 객체 정보 반환")
    @GetMapping("/personId/{id}")
    public ResponseEntity<Member> findById(@ApiParam(value = "해당 유저의 DB 저장 ID") @PathVariable Long id) {
        return ResponseEntity.ok(memberRepository.findById(id).orElse(null));
    }
}
