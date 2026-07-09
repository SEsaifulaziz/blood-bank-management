package com.bloodbank.security;

import com.bloodbank.entity.User;
import com.bloodbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Spring Security lifecycle: Loading credentials for identity '{}'", email);


        User user = userRepository.findByEmail(email)
                .orElseThrow(()  ->{
                    log.warn("Spring Security lifecycle failure: Account associated with '{}' does not exist", email);
                    return new UsernameNotFoundException("User not found with email" + email);
                });
        return UserDetailsImpl.build(user);

    }
}
