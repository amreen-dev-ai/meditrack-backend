package com.meditrack.security;

import com.meditrack.entity.User;
import com.meditrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

// Spring Security doesn't know about YOUR "User" entity out of the box.
// This class is the adapter: it implements Spring's UserDetailsService interface,
// telling Spring Security "here's how to load a user by username, from OUR database."
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Spring Security expects a UserDetails object, not our own User entity directly --
        // org.springframework.security.core.userdetails.User is Spring's built-in implementation.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),   // already BCrypt-hashed, stored that way
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
