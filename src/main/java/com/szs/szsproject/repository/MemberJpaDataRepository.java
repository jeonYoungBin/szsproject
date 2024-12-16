package com.szs.szsproject.repository;


import com.szs.szsproject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaDataRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByNameAndRegNo(String username, String regNo);
}
