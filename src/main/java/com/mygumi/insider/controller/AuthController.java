package com.mygumi.insider.controller;


import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import com.mygumi.insider.infra.kakao.KakaoLoginParams;
import com.mygumi.insider.service.OAuthLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Api(value = "카카오 로그인과 관련된 Controller")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;
    private final AuthTokensGenerator authTokensGenerator;


//    @PostMapping("/kakao")
//    public ResponseEntity<AuthTokens> loginKakao(@ApiParam(value = "카카오 로그인을 한 후 [redirect URL]?code=" +
//            "뒤에 생기는 Kakao에서 보내주는 Token") @RequestBody KakaoLoginParams params) {
//        return ResponseEntity.ok(oAuthLoginService.login(params));
//    }

    @ApiOperation(value = "유저가 동의를 하고 로그인을 하면 이 API에서 유저 정보를 등록한다",
        notes = "해당 토큰에는 카카오 유저정보가 들어있고 이 API를 통해서 유저가" +
                "이미 가입되어있는지, 없다면 DB에 저장하고 해당 유저에 대한 JWT Token들을 생성하여 " +
                "Response Body로 전송")
    @PostMapping("/kakao")
    public ResponseEntity<?>loginKakao(@ApiParam(value = "카카오 로그인을 한 후 [redirect URL]?code=" +
            "뒤에 생기는 Kakao에서 보내주는 Token") @RequestBody KakaoLoginParams params) {
        return ResponseEntity.ok(oAuthLoginService.isMember(params));
    }


    @ResponseBody
    @PostMapping("/getaccesstoken")
    public void kakaoaccess(@RequestBody String Token)
    {
        System.out.println(oAuthLoginService.getKakaoAccessToken(Token));
    }


    @ApiOperation(value = "모바일버전 access token 넣으면 저장후 jwt 반환")
    @ResponseBody
    @PostMapping("/kakaomobile")
    public ResponseEntity<?> kakaouser(@RequestBody String accessToken) {

        Map<String,Object> resultMap = new HashMap<>();

        try {
            // 액세스 토큰을 이용하여 카카오 서버에서 유저 정보(닉네임, 이메일) 받아오기
            Map<String, Object> userInfo = oAuthLoginService.getUserInfo(accessToken);
            resultMap.put("message","success");
            resultMap.put("user",userInfo);

        } catch (Exception e) {
            resultMap.put("message","fail");
            e.printStackTrace();
        }

        return ResponseEntity.ok(resultMap);
    }


    @ApiOperation(value = "접속 유저의 JWT 토큰을 통해서 해당 유저의 ID를 리턴하는 API",
                  notes = "전송 형식: Authorization : Bearer $[JWT Token]")
    @PostMapping("/findid")
    public ResponseEntity<Long> extractIdByAccessToken(@ApiParam(value = "해당 유저의 JWT 토큰") @RequestHeader("Authorization") String jwt){
        String accessToken = jwt.replace("Bearer ", "");
        return ResponseEntity.ok(authTokensGenerator.extractMemberId(accessToken));
    }
}
