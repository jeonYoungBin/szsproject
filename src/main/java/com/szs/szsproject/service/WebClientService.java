package com.szs.szsproject.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.webclient.service.ScrapClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebClientService {
    private final ScrapClient scrapClient;

    public SrpResponse callApi(String name, String regNo) {
        return scrapClient.callApi(name, regNo);
    }

    public Mono<SrpResponse> callApiReactive(String name, String regNo) {
        return scrapClient.callApiReactive(name, regNo);
    }
}
