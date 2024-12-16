package com.szs.szsproject.domain.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SrpResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("errors")
    private Errors errors;
}
