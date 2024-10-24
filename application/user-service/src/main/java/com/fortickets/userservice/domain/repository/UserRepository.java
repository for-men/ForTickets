package com.fortickets.userservice.domain.repository;

import com.fortickets.userservice.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByPhone(String phone);

    // 닉네임 중복 확인 (현재 사용자 제외)
    boolean existsByNicknameAndUserIdNot(String nickname, Long userId);

    // 전화번호 중복 확인 (현재 사용자 제외)
    boolean existsByPhoneAndUserIdNot(String phone, Long userId);

    List<User> findByNicknameContaining(String nickname);
}
