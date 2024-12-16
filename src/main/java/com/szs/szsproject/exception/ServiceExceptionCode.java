package com.szs.szsproject.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceExceptionCode implements ErrorCode {

    DATA_NOT_FOUND_USER(HttpStatus.NO_CONTENT, "not found member"),
    ALREADY_JOIN(HttpStatus.CONFLICT, "already join userId"),
    NOT_PASSWORD_MATCH(HttpStatus.NOT_FOUND, "not match password"),
    UNABLE_TO_JOIN(HttpStatus.NOT_FOUND, "unalbe to join user"),
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
