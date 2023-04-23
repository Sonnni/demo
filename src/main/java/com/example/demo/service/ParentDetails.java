package com.example.demo.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface ParentDetails {
    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    String getEmail();

    Long getId();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
