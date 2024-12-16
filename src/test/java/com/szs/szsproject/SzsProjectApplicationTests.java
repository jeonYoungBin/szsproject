package com.szs.szsproject;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.service.WebClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SzsProjectApplicationTests {
    @Autowired
    WebClientService webClientService;

    @Test
    void contextLoads() {
        SrpResponse aa = webClientService.callApi("동탁", "921108-1582816");
        System.out.println(aa.getData().getTotalIncome());
    }

}
