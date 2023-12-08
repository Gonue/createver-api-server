package com.createver.server.global.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomMember> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomMember customMember) {
        String email = customMember.email();
        String role = customMember.role();


        final UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(email, "password",
                List.of(new SimpleGrantedAuthority(role)));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }
}