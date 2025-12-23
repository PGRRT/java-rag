package com.example.user.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public record UserPrincipal(UUID id,
                            String email,
                            Collection<? extends GrantedAuthority> authorities) implements UserDetails {
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return null; } // Not needed for JWT authentication
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    // Not needed for JWT authentication, so we return true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
