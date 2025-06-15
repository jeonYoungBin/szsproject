package com.szs.szsproject.webclient.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.webclient.config.WebClientProperties;
import com.szs.szsproject.webclient.dto.ScrapRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ScrapClientImpl implements ScrapClient {
    private final WebClient webClient;
    private final WebClientProperties properties;

    @Override
    public SrpResponse callApi(String name, String regNo) {
        ScrapRequest request = new ScrapRequest(name, regNo);
        return webClient.post()
                .uri("/scrap")
                .header("X-API-KEY", properties.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SrpResponse.class)
                .block();
    }

    @Override
    public Mono<SrpResponse> callApiReactive(String name, String regNo) {
        ScrapRequest request = new ScrapRequest(name, regNo);
        return webClient.post()
                .uri("/scrap")
                .header("X-API-KEY", properties.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SrpResponse.class);
    }
} 