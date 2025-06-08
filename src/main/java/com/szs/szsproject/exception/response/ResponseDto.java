package com.szs.szsproject.exception.response;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ResponseDto {
    int code;
    String message;
    Object result;

    public ResponseDto(int code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
}
