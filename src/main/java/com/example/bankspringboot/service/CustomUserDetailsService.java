package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.User;
import com.example.bankspringboot.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {

  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    user.getRoles()
        .forEach(
            role -> {
              grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
              role.getPermissions()
                  .forEach(
                      permission ->
                          grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName())));
            });
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), grantedAuthorities);
  }
}
