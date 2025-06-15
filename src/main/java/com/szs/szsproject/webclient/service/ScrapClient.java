package com.szs.szsproject.webclient.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import reactor.core.publisher.Mono;

public interface ScrapClient {
    SrpResponse callApi(String name, String regNo);
    Mono<SrpResponse> callApiReactive(String name, String regNo);
} 