package com.szs.szsproject.domain.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Data {
    @JsonProperty("종합소득금액")
    private Long totalIncome;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("소득공제")
    private TaxDeductions taxDeductions;
}
