package com.mygumi.insider.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
@Api(value = "카카오 로그인 & 로그아웃으로 리다이렉트만 관여하는 Controller")
public class MainController {

    @ApiOperation(value = "main 에러 나게")
    @GetMapping("/main")
    public ResponseEntity<?> oauthLogin() throws IOException {
        return new ResponseEntity<?>(HttpStatus.OK);
    }
    
    // //카카오 로그인 api
    // @ApiOperation(value = "해당 URL로 들어오면 카카오 로그인 API가 제공하는 내 애플리케이션의 로그인 화면으로 redirect" ,
    //               notes = "redirect될 URL은 고정 값")
    // @GetMapping("/login")
    // public ResponseEntity<?> oauthLogin() throws IOException {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setLocation(URI.create("https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=d17798d61b78b91a8abfca3e6f914fe5&redirect_uri=https://mygumiworld-backend-flkcc.run.goorm.io/api/auth/kakao&prompt=login"));
    //     return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    // }

    // //카카오 계정 로그아웃 -> 로그인 되었을 때만 로그아웃 할 수 있도록 구현
    // @ApiOperation(value = "해당 URL로 들어오면 카카오 로그인 API가 제공하는 내 애플리케이션의 로그아웃 화면으로 redirect" ,
    //         notes = "Authorization: Bearer $[JWT Token] 형식으로 Header에 달아준다")
    // @GetMapping("/logout")
    // public ResponseEntity<?> oauthLogout(@ApiParam("value = 로그인 된 유저의 JWT 토큰 값")  @RequestHeader("Authorization") String jwt) throws IOException {

    //     String accessToken = jwt.replaceAll("Bearer ", "");

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setLocation(URI.create("https://kauth.kakao.com/oauth/logout?client_id=d17798d61b78b91a8abfca3e6f914fe5&logout_redirect_uri=https://mygumiworld-backend-flkcc.run.goorm.io/api/auth/kakao"));
    //     return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    // }

}
