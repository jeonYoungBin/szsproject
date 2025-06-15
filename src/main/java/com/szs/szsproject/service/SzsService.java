package com.szs.szsproject.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.entity.Member;
import com.szs.szsproject.exception.CustomException;
import com.szs.szsproject.exception.ServiceExceptionCode;
import com.szs.szsproject.repository.MemberJpaDataRepository;
import com.szs.szsproject.utils.AesUtil;
import com.szs.szsproject.utils.JwtTokenUtil;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.swing.text.MutableAttributeSet;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SzsService {
    private final MemberJpaDataRepository memberJpaDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenProvider;
    private final AesUtil aesUtil;
    private final WebClientService webClientService;

    /**
     * 회원 가입
     */
    @Transactional
    public Member signup(String userId, String password, String name, String reqNo) throws Exception {
        if(memberJpaDataRepository.findByUserId(userId).isPresent())
            throw new CustomException(ServiceExceptionCode.ALREADY_JOIN);

        if(!possibleJoinMember(name, reqNo))
            throw new CustomException(ServiceExceptionCode.UNABLE_TO_JOIN);

        return memberJpaDataRepository.save(Member.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .userId(userId)
                .regNo(aesUtil.encrypt(reqNo)).build());
    }

    /**
     * 로그인
     */
    public String login(String userId, String password) throws Exception {
        Member member = memberJpaDataRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND_USER));
        if(!passwordEncoder.matches(password,member.getPassword())) {
            throw new CustomException(ServiceExceptionCode.NOT_PASSWORD_MATCH);
        }
        return jwtTokenProvider.createToken(member.getUserId(), aesUtil.decrypt(member.getRegNo()));
    }

    public Mono<Long> calculateDeductionAsync(String userId) {
        return Mono.fromCallable(() -> {
                    Member member = memberJpaDataRepository.findByUserId(userId)
                            .orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND_USER));
                    String name = member.getName();
                    String regNo = aesUtil.decrypt(member.getRegNo());
                    return Tuples.of(member, name, regNo);
                })
                .flatMap(tuple -> webClientService.callApiReactive(tuple.getT2(), tuple.getT3())
                        .flatMap(srpResponse -> saveDeductions(tuple.getT1(), srpResponse))
                );
    }

    @Transactional
    public Mono<Long> saveDeductions(Member member, SrpResponse srpResponse) {
        // totalIncome
        member.updateTotalIncome(String.valueOf(srpResponse.getData().getTotalIncome()));

        // pension
        double pensionSum = srpResponse.getData().getTaxDeductions().getPensionDeductions().stream()
                .mapToDouble(d -> Double.parseDouble(d.getDeductionAmount().replace(",", "")))
                .sum();
        member.updateTotalPensionDeductions(String.valueOf(pensionSum));

        // credit card
        double creditSum = srpResponse.getData().getTaxDeductions().getCreditCardDeduction().getMonthlyDeductions().stream()
                .flatMap(map -> map.values().stream())
                .mapToDouble(val -> Double.parseDouble(val.replace(",", "")))
                .sum();
        member.updateTotalCreditCardDeduction(String.valueOf(creditSum));

        // tax deduction
        member.updateTotalTaxDeduction(srpResponse.getData().getTaxDeductions().getTaxDeduction().replace(",", ""));

        return Mono.just(memberJpaDataRepository.save(member).getId());
    }

    public String calculateRefund(String userId) throws CustomException {
        Member member = memberJpaDataRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND_USER));

        // 과세 표준 = 종합소득금액 - 소득공제
        long taxBase = Math.round(Double.valueOf(member.getTotalIncome()) -
                (Double.valueOf(member.getTotalCreditCardDeduction()) + Double.valueOf(member.getTotalPensionDeductions())));

        // 산출세액
        double taxAmount = 0.0;
        if (taxBase <= 14_000_000L) {
            taxAmount = taxBase * 0.06;
        } else if (taxBase > 14_000_000L && taxBase <= 50_000_000L) {
            taxAmount = 840_000L + (taxBase - 14_000_000L) * 0.15;
        } else if (taxBase > 50_000_000L && taxBase <= 88_000_000L) {
            taxAmount = 6_240_000 + (taxBase - 50_000_000L) * 0.24;
        } else if (taxBase > 88_000_000L && taxBase <= 150_000_000L) {
            taxAmount = 15_360_000 + (taxBase - 88_000_000L) * 0.35;
        } else if (taxBase > 150_000_000L && taxBase <= 300_000_000L) {
            taxAmount = 37_060_000 + (taxBase - 150_000_000L) * 0.38;
        } else if (taxBase > 300_000_000L && taxBase <= 500_000_000L) {
            taxAmount = 113_060_000 + (taxBase - 300_000_000L) * 0.4;
        } else if (taxBase > 500_000_000L && taxBase <= 1_000_000_000L) {
            taxAmount = 193_060_000 + (taxBase - 500_000_000L) * 0.42;
        } else {
            taxAmount = 403_060_000 + (taxBase - 1_000_000_000L) * 0.45;
        }

        NumberFormat format = NumberFormat.getInstance();
        return format.format(Math.round(taxAmount - Double.valueOf(member.getTotalTaxDeduction())));
    }


    /**
     * 가입 가능한 이름&주민번호 체크
     */
    private Boolean possibleJoinMember(String name, String regNo) {
        List<Pair<String, String>> pairList = Arrays.asList(
                Pair.of("동탁", "921108-1582816"),
                Pair.of("관우", "681108-1582816"),
                Pair.of("손권", "890601-2455116"),
                Pair.of("유비", "790411-1656116"),
                Pair.of("조조", "820326-2715702")
        );

        return pairList.stream()
                .anyMatch(pair -> pair.getFirst().equals(name) && pair.getSecond().equals(regNo));
    }
}
