package com.szs.szsproject;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.service.WebClientService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
class SzsProjectApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(SzsProjectApplicationTests.class);
    @Autowired
    WebClientService webClientService;

    @Autowired
    CallService callService;

    @Test
    void test() {
        callService.external();
    }

    @TestConfiguration
    static class testConfiguration {
        @Bean
        CallService externalService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    static class CallService {
        private final InternalService internalService;

        public CallService(InternalService internalService) {
            this.internalService = internalService;
        }

        public void external() {
            log.info("external call");
            printTxInfo();
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", txActive);
        }
    }

    static class InternalService {
        @Transactional
        public void internal() {
            log.info("internal call");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("actual transaction active: {}", txActive);
        }
    }


    @Test
    void contextLoads() {
        SrpResponse srpResponse = webClientService.callApi("관우", "681108-1582816");
        Assertions.assertThat(srpResponse.getData().getTotalIncome()).isEqualTo("10000000");
    }



}
