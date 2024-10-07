package com.main.Jora.services;

import com.main.Jora.models.User;
import com.main.Jora.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    //Поиск пользователя должен происходить по email, а не username
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        System.out.println("Net usera");
        throw new UsernameNotFoundException(
                "User '" + email + "' not found");
    }
}
