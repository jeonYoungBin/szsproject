package com.szs.szsproject.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.entity.Member;
import com.szs.szsproject.exception.CustomException;
import com.szs.szsproject.exception.ServiceExceptionCode;
import com.szs.szsproject.repository.MemberJpaDataRepository;
import com.szs.szsproject.utils.JwtTokenUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class WebClientService {
    @Value("${endpoint.key}")
    private String key;

    private final WebClient webClient;

    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://codetest-v4.3o3.co.kr").build();
    }

    public SrpResponse callApi(String name, String regNo) {
        // 요청 Body 생성
        ApiRequest request = new ApiRequest(name, regNo);
        // API 호출 및 응답 처리
        return webClient.post()
                .uri("/scrap")
                .header("X-API-KEY", key) // API 키 추가
                .contentType(MediaType.APPLICATION_JSON) // JSON 타입 지정
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SrpResponse.class)
                .block();
    }


    @Getter
    @Builder
    static class ApiRequest {
        private String name;
        private String regNo;

        public ApiRequest(String name, String regNo) {
            this.name = name;
            this.regNo = regNo;
        }
    }
}
