package com.sooscode.sooscode_api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sooscode.sooscode_api.domain.user.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

}
