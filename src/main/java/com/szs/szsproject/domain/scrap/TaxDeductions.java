package com.szs.szsproject.domain.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TaxDeductions {
    @JsonProperty("국민연금")
    private List<PensionDeduction> pensionDeductions;

    @JsonProperty("신용카드소득공제")
    private CreditCardDeduction creditCardDeduction;

    @JsonProperty("세액공제")
    private String taxDeduction;
}
