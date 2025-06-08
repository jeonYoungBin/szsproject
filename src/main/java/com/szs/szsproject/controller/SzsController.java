package com.szs.szsproject.controller;

import com.szs.szsproject.exception.response.ResponseDto;
import com.szs.szsproject.entity.Member;
import com.szs.szsproject.service.SzsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/szs")
@Slf4j
@AllArgsConstructor
public class SzsController {

    private final SzsService szsService;

    /**
     * 회원 가입
     */
    @Tag(name = "인증")
    @Operation(summary = "회원 가입", description = "회원 가입 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberJoinResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberJoinResponse> signup(@RequestBody @Valid MemberJoinRequest request) throws Exception {
        Member signup = szsService.signup(request.getUserId(), request.getPassword(), request.getName(), request.getReqNo());
        return ResponseEntity.ok(MemberJoinResponse.builder().userId(signup.getUserId()).name(signup.getName()).build());
    }

    @Tag(name = "인증")
    @Operation(summary = "로그인", description = "로그인 api 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = MemberLoginResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberLoginResponse> login(@RequestBody @Valid MemberLoginRequest request) throws Exception {
        return ResponseEntity.ok(MemberLoginResponse.builder().accessToken(szsService.login(request.getUserId(), request.getPassword())).build());
    }

    @Tag(name = "소득정보")
    @Operation(summary = "소득정보", description = "소득정보 api 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호출 성공", content = @Content(schema = @Schema(implementation = IncomeResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/scrap", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<IncomeResponse>> income(Authentication authentication) throws Exception {
        return szsService.calculateDeductionAsync(authentication.getName())
                .map(memberId -> ResponseEntity.ok(
                        IncomeResponse.builder()
                                .memberId(memberId)
                                .build()
                ));
    }

    @Tag(name = "결정세액")
    @Operation(summary = "소득정보", description = "소득정보 api 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호출 성공", content = @Content(schema = @Schema(implementation = RefundResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefundResponse> refund(Authentication authentication) throws Exception {
        return ResponseEntity.ok(RefundResponse.builder().refund(szsService.calculateRefund(authentication.getName())).build());
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class RefundResponse {
        private String refund;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class IncomeResponse {
        private Long memberId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class MemberLoginResponse {
        private String accessToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class MemberJoinResponse {
        private String name;
        private String userId;
    }

    @Getter
    @Builder
    static class MemberJoinRequest {
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

    @Getter
    @Builder
    static class MemberLoginRequest {
        @NotNull(message = "아이디를 넣어주세요")
        private String userId;
        @NotNull(message = "패스워드를 넣어주세요")
        private String password;
    }
}
