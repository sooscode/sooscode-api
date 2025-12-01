package com.sooscode.sooscode_api.infra.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        //System.out.println("loadUserByUsername 호출: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    //System.out.println("DB에서 유저 못 찾음: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        // 구글 로그인 사용자라면 패스워드는 더미 문자열로 설정
        String password = user.getPassword();
        if (password == null || password.equals("GOOGLE_USER")) {
            password = "";
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(password)
                .roles(user.getRole().name())
                .build();
    }
}
