package com.fortickets.userservice.application.security;

import com.fortickets.userservice.domain.entity.User;
import com.fortickets.userservice.domain.entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // UserDetails 인터페이스의 필수 메서드인 getUsername() 을 기준으로 사용자를 찾기 때문에 메서드명은 변경 불가.
    // 따라서 메서즈명은 getUsername() 이지만 retyrn 값은 user.getEmail() 로 사용.
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
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
        return true;
    }

    // User 객체에서 user_id를 반환하는 메서드
    public Long getUserId() {
        return user.getUserId();
    }
}
