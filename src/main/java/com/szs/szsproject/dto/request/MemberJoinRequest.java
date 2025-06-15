package com.szs.szsproject.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberJoinRequest {
    @NotNull(message = "아이디를 넣어주세요")
    private String userId;
    
    @NotNull(message = "패스워드를 넣어주세요")
    private String password;
    
    @NotNull(message = "이름을 넣어주세요")
    private String name;
    
    @NotNull(message = "주민번호를 넣어주세요")
    @Pattern(regexp = "^\\d{6}-\\d{7}$")
    private String reqNo;
} 