package com.sing4u.kr.application.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.Serializable;
import java.util.Collection;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class DefaultUserDetail implements UserDetails, Serializable {
    private Long id;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;
    private String name;
    private String companyName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static DefaultUserDetail of(Long id, Collection<GrantedAuthority> authorities) {
        return DefaultUserDetail.builder()
                .id(id)
                .authorities(authorities)
                .build();
    }
}
