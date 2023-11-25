package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public void loginUser(String email, String password) {

        // Vérifier les informations d'identification de l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé pour cet e-mail."));
        System.out.println(user);
        // Log pour débogage
        logger.info("User Password from DB: {}", user.getPassword());
        logger.info("Provided Password: {}", password);

        if (passwordEncoder.matches(password, user.getPassword())) {
            // Générer et envoyer l'OTP seulement si l'OTP n'est pas déjà activé
            if (!user.isOtpEnabled()) {
                otpService.sendOtp(email);

                // Activer l'OTP pour cet utilisateur
                user.setOtpEnabled(true);
                userRepository.save(user);
            }
        } else {
            throw new RuntimeException("Mot de passe incorrect.");
        }
    }
}


