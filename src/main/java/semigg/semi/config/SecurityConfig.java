package semigg.semi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (POST 요청 허용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register",          // ✅ 정확히 이 경로 포함되어야 함!
                                "/api/users/send-auth-code",
                                "/api/users/email",
                                "/api/users/**"                // 혹시 대비한 추가 허용
                        ).permitAll()
                        .anyRequest().permitAll() //전부허용
                );

        return http.build();
    }
}