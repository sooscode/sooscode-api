package com.sooscode.sooscode_api.infra.security;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

/**
 * 스프링 시큐리티가 사용하는 사용자 정보 포맷
 * 시큐리티는 USer entity 그대로 사용 X -> UserDetails로 감싸야 함
 * USer entity를 기반으로 인증 가능한 정보 제공
 * 컨트롤러에서 로그인 정보 확인할 때 @AuthenticationPrincipal CustomUserDetails userDetails 로 사용
 */
public class CustomUserDetails implements UserDetails {

    private final User user;


    /**
     * 일반 로그인/OAuth 공통 생성자
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        /**
         * OAuth 로그인 사용자 (password = null) 대비
         */
        return user.getPassword() != null ? user.getPassword() : "";
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    /**
     * User entity 접근용
     */
    public User getUser() {
        return user;
    }
}
