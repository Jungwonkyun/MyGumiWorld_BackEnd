package com.mygumi.insider.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygumi.insider.domain.oauth.*;
import com.mygumi.insider.dto.Member;
import com.mygumi.insider.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            
            Optional<Member> oMember = memberRepository.findById(newMemberId);
            Member member = oMember.get();
            member.setUsernickname(member.getNickname());
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



    public Map<String, Object> getUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String postURL = "https://kapi.kakao.com/v2/user/me";
        Map<String,Object> loginInfo = new HashMap<>();
        AuthTokens authTokens = null;
        boolean check = false;

        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            //System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);


            JsonElement element = JsonParser.parseString(result.toString());


            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            Long id = element.getAsJsonObject().get("id").getAsLong();
            //JsonObject id = element.getAsJsonObject().get("id").getAsJsonObject();


            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = "";
            String profile = "";

            if(kakaoAccount.has("email")) {
                email = kakaoAccount.getAsJsonObject().get("email").getAsString();
            }

            if(properties.has("thumbnail_image")) {
                profile = properties.getAsJsonObject().get("thumbnail_image").getAsString();
            }

            Long memberId = memberRepository.findByKakaoId(id).map(Member::getId).orElseGet(() -> 0L);

            //만약 DB에 유저 정보가 없다면
            if(memberId == 0L){
                Member member = Member.builder()
                        .email(email)
                        .nickname(nickname)
                        .oAuthProvider(OAuthProvider.KAKAO)
                        .thumbnail_image_url(profile)
                        .kakaoId(id)
                        .build();
                
                member.setUsernickname(nickname);
                Long newMemberId = memberRepository.save(member).getId();
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

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        loginInfo.put("jwt",authTokens);
        loginInfo.put("isPresent" , check);

        return loginInfo;
    }


    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=d17798d61b78b91a8abfca3e6f914fe5"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=https://mygumiworld-backend-sxxzy.run.goorm.io/api/auth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
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


    private Long newMember2(OAuthInfoResponse oAuthInfoResponse) {
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
