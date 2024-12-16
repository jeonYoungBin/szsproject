package com.szs.szsproject.domain.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 국민연금
 */
@Getter
public class PensionDeduction {
    @JsonProperty("월")
    private String month;

    @JsonProperty("공제액")
    private String deductionAmount;
}
