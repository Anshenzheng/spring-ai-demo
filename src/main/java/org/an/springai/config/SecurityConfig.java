package org.an.springai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 注入全局CORS配置（如果有）
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启CORS（与全局CORS配置配合）
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // 2. 关闭CSRF（适用于非浏览器客户端的API）
                .csrf(csrf -> csrf.disable())
                // 3. 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 放行所有接口（根据需求调整，如只放行/api/**）
                        .requestMatchers("/**").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 4. 禁用默认的Basic认证（可选，根据需求保留）
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}