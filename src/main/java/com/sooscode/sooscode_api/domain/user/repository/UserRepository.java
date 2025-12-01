package com.sooscode.sooscode_api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sooscode.sooscode_api.application.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
