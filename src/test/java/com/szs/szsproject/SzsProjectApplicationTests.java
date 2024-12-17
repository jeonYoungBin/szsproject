package com.szs.szsproject;

import com.szs.szsproject.domain.scrap.PensionDeduction;
import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.service.WebClientService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class SzsProjectApplicationTests {
    @Autowired
    WebClientService webClientService;

    @Test
    void contextLoads() {
        SrpResponse srpResponse = webClientService.callApi("관우", "681108-1582816");
        Assertions.assertThat(srpResponse.getData().getTotalIncome()).isEqualTo("10000000");
    }

}
