package com.szs.szsproject.controller;

import com.szs.szsproject.dto.request.MemberJoinRequest;
import com.szs.szsproject.dto.request.MemberLoginRequest;
import com.szs.szsproject.dto.response.*;
import com.szs.szsproject.entity.Member;
import com.szs.szsproject.exception.response.ResponseDto;
import com.szs.szsproject.exception.CustomException;
import com.szs.szsproject.service.SzsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/szs")
@Slf4j
@RequiredArgsConstructor
public class SzsController {

    private final SzsService szsService;

    @Tag(name = "인증")
    @Operation(summary = "회원 가입", description = "회원 가입 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberJoinResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberJoinResponse> signup(@RequestBody @Valid MemberJoinRequest request) throws CustomException, Exception {
        Member signup = szsService.signup(request.getUserId(), request.getPassword(), request.getName(), request.getReqNo());
        return ResponseEntity.ok(MemberJoinResponse.builder()
                .userId(signup.getUserId())
                .name(signup.getName())
                .build());
    }

    @Tag(name = "인증")
    @Operation(summary = "로그인", description = "로그인 api 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = MemberLoginResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberLoginResponse> login(@RequestBody @Valid MemberLoginRequest request) throws CustomException, Exception {
        String accessToken = szsService.login(request.getUserId(), request.getPassword());
        return ResponseEntity.ok(MemberLoginResponse.builder()
                .accessToken(accessToken)
                .build());
    }

    @Tag(name = "소득정보")
    @Operation(summary = "소득정보", description = "소득정보 api 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "호출 성공", content = @Content(schema = @Schema(implementation = IncomeResponse.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/scrap", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<IncomeResponse>> income(Authentication authentication) {
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
    public ResponseEntity<RefundResponse> refund(Authentication authentication) throws CustomException {
        String refund = szsService.calculateRefund(authentication.getName());
        return ResponseEntity.ok(RefundResponse.builder()
                .refund(refund)
                .build());
    }
}
