package com.createver.server.global.config;

import com.createver.server.domain.member.service.MemberService;
import com.createver.server.global.auth.filter.JwtAuthenticationFilter;
import com.createver.server.global.auth.filter.JwtVerificationFilter;
import com.createver.server.global.auth.handler.*;
import com.createver.server.global.auth.jwt.JwtTokenizer;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;
    private final CorsConfigurationSource corsConfigurationSource;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                        .accessDeniedHandler(new MemberAccessDeniedHandler())
                );
        http.apply(new CustomFilterConfigurer());

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/*").permitAll()
                        .requestMatchers("/api/v1/member/join").permitAll()
                        .requestMatchers("api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/member/**").hasAnyRole("USER", "ADMIN")

//                        .requestMatchers(HttpMethod.POST, "/api/v1/image/upload/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/music", "/api/v1/music/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/music/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/music/*").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/api/v1/article/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/image/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/review/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/image/create/stable").hasAnyRole("USER", "ADMIN")

                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new OAuth2MemberSuccessHandler(jwtTokenizer, authorityUtils, memberService)));


        return http.build();
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer, redisTemplate);

            jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, OAuth2LoginAuthenticationFilter.class);
        }
    }
}
