package com.szs.szsproject.service;

import com.szs.szsproject.domain.scrap.SrpResponse;
import com.szs.szsproject.entity.Member;
import com.szs.szsproject.exception.CustomException;
import com.szs.szsproject.exception.ServiceExceptionCode;
import com.szs.szsproject.repository.MemberJpaDataRepository;
import com.szs.szsproject.utils.AesUtil;
import com.szs.szsproject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /*
    merge Test
     */
    /*
    merge Test 222
     */
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

    @Transactional
    public Long calculateDeduction(String userId) throws Exception {
        Member member = memberJpaDataRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND_USER));
        SrpResponse srpResponse = webClientService.callApi(member.getName(), aesUtil.decrypt(member.getRegNo()));

        //종합소득금액
        member.updateTotalIncome(String.valueOf(srpResponse.getData().getTotalIncome()));

        //국민연금 총 합
        double pensionDeductionsSum = srpResponse.getData().getTaxDeductions().getPensionDeductions().stream()
                .mapToDouble(deduction -> Double.parseDouble(deduction.getDeductionAmount().replace(",", "")))
                .sum();
        member.updateTotalPensionDeductions(String.valueOf(pensionDeductionsSum));

        //신용카드소득공제 총 합
        List<Map<String, String>> monthlyDeductions = srpResponse.getData().getTaxDeductions().getCreditCardDeduction().getMonthlyDeductions();
        double creditCardDeductionSum = monthlyDeductions.stream()
                .flatMap(map -> map.values().stream())
                .mapToDouble(value -> Double.parseDouble(value.replace(",", ""))) // 쉼표 제거 후 double로 변환
                .sum();
        member.updateTotalCreditCardDeduction(String.valueOf(creditCardDeductionSum));

        //세액공제
        member.updateTotalTaxDeduction(srpResponse.getData().getTaxDeductions().getTaxDeduction().replace(",",""));

        return member.getId();
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
