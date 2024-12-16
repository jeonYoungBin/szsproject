package com.szs.szsproject.service;

import com.szs.szsproject.entity.Member;
import com.szs.szsproject.exception.CustomException;
import com.szs.szsproject.exception.ServiceExceptionCode;
import com.szs.szsproject.repository.MemberJpaDataRepository;
import com.szs.szsproject.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SzsService {
    private final MemberJpaDataRepository memberJpaDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenProvider;

    /**
     * 회원 가입
     */
    @Transactional
    public Member signup(String userId, String password, String name, String reqNo) throws Exception {
        if(memberJpaDataRepository.findByUserId(userId).isPresent())
            throw new CustomException(ServiceExceptionCode.ALREADY_JOIN);

        if(!possibleJoinMember(name, reqNo))
            throw new CustomException(ServiceExceptionCode.UNABLE_TO_JOIN);

        return memberJpaDataRepository.save(Member.builder().name(name).password(passwordEncoder.encode(password)).userId(userId).regNo(reqNo).build());
    }

    /**
     * 로그인
     */
    public String login(String userId, String password) throws Exception {
        Member member = memberJpaDataRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ServiceExceptionCode.DATA_NOT_FOUND_USER));
        if(!passwordEncoder.matches(password,member.getPassword())) {
            throw new CustomException(ServiceExceptionCode.NOT_PASSWORD_MATCH);
        }
        return jwtTokenProvider.createToken(member.getUserId(), member.getRegNo());
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
