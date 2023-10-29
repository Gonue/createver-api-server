package com.template.server.global.config;

import com.template.server.domain.member.service.MemberService;
import com.template.server.global.auth.filter.JwtAuthenticationFilter;
import com.template.server.global.auth.filter.JwtVerificationFilter;
import com.template.server.global.auth.handler.*;
import com.template.server.global.auth.jwt.JwtTokenizer;
import com.template.server.global.auth.utils.CustomAuthorityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .cors().configurationSource(corsConfigurationSource)
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "/index.html", "/static/**", "/image/**").permitAll()
                        .antMatchers("/oauth2/authorization/**").permitAll()
                        .antMatchers("/login/oauth2/code/*").permitAll()
                        .antMatchers("/api/v1/member/join", "/api/v1/member/login").permitAll()
                        .antMatchers("/api/v1/member/**").hasAnyRole("USER", "ADMIN")

                        .antMatchers(HttpMethod.POST, "/api/v1/image/upload").hasAnyRole("USER", "ADMIN")

                        .antMatchers(HttpMethod.POST, "/api/v1/article").hasRole("ADMIN")  // 아티클 생성
                        .antMatchers(HttpMethod.PATCH, "/api/v1/article/*").hasRole("ADMIN")  // 아티클 수정
                        .antMatchers(HttpMethod.DELETE, "/api/v1/article/*").hasRole("ADMIN")  // 아티클 삭제

                        .antMatchers(HttpMethod.POST, "/api/v1/image/create/stable").hasAnyRole("USER", "ADMIN")

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

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);

            jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/member/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, OAuth2LoginAuthenticationFilter.class);
        }
    }
}
