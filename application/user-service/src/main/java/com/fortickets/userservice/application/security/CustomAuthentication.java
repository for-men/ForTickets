package com.fortickets.userservice.application.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class CustomAuthentication extends UsernamePasswordAuthenticationToken {
    private final Long userId;
    private final String email;

    public CustomAuthentication(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {
        super(userId, null, authorities);
        this.userId = userId;
        this.email = email;
    }
}
