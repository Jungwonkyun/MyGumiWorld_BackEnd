package com.mygumi.insider.repository;

import com.mygumi.insider.dto.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(int id);
    Optional<Member> findByKakaoId(Long kakaoId);

}
