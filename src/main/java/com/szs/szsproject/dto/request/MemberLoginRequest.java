package com.szs.szsproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLoginRequest {
    @NotNull(message = "아이디를 넣어주세요")
    private String userId;
    
    @NotNull(message = "패스워드를 넣어주세요")
    private String password;
} 