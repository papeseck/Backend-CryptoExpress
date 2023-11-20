package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.model.CustomUserDetails;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'e-mail : " + username);
        }
        return new CustomUserDetails(user); // CustomUserDetails est une classe qui implémente UserDetails
    }
}
