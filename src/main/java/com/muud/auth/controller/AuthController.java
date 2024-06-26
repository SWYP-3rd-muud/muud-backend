package com.muud.auth.controller;

import com.muud.auth.domain.dto.*;
import com.muud.auth.service.AuthService;
import com.muud.auth.service.KakaoService;
import com.muud.global.error.ApiException;
import com.muud.global.error.ExceptionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoService kakaoService;
    @Value("${admin-code}")
    private String ADMIN_CODE;

    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signupWithEmail(@Valid @RequestBody SignupRequest request){
        authService.signupWithEmail(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignupResponse.of());
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SigninResponse> signinWithEmail(@Valid @RequestBody SigninRequest signinRequest) {
        SigninResponse signinResponse = authService.signinWithEmail(signinRequest);
        return ResponseEntity.ok()
                .body(signinResponse);
    }

    @PostMapping("/auth/kakao/signin")
    public ResponseEntity<SigninResponse> signinWithKakao(@RequestBody Map<String, String> mapCode){
        KakaoInfoResponse kakaoInfoResponse = kakaoService.getKakaoInfo(mapCode.get("code"));
        SigninResponse signinResponse = authService.signinWithKakao(kakaoInfoResponse);
        if(signinResponse.isNewUser()){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(signinResponse);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(signinResponse);
    }

    @PostMapping("/auth/signup/admin")
    public ResponseEntity<SignupResponse> signupAdmin(@RequestBody SignupRequest signupRequest, @RequestHeader(name = "Auth_Code") String authCode){
        if(!authCode.equals(ADMIN_CODE))
            throw new ApiException(ExceptionType.FORBIDDEN_USER);
        Long userId = authService.signupAdmin(signupRequest);
        return ResponseEntity.created(URI.create("/users/"+userId))
                .body(SignupResponse.of());
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> reIssueToken(@RequestBody Map<String, String> mapToken){
        String refreshToken = mapToken.get("refreshToken");
        if(refreshToken==null)
            throw new ApiException(ExceptionType.INVALID_AUTHENTICATE);
        TokenResponse tokenResponse = authService.reIssueToken(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tokenResponse);
    }

}