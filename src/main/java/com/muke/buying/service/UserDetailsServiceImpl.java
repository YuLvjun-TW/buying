package com.muke.buying.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现自定义登录逻辑
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("username not found.");
        }

        String password = passwordEncoder.encode("123");
//        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_user");
        return new User(username, password, authorities);
    }
}
