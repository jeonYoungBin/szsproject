package com.szs.szsproject.domain.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CreditCardDeduction {
    @JsonProperty("month")
    private List<Map<String, String>> monthlyDeductions;

    @JsonProperty("year")
    private int year;

}
