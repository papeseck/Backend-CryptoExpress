package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Transactional
    public void registerUser(User user) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new RuntimeException("Cet e-mail est déjà utilisé.");
            }

            // Encoder le mot de passe avant de le sauvegarder
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Enregistrer l'utilisateur dans la base de données
            userRepository.save(user);

            // Générer et envoyer l'OTP si les informations d'inscription sont correctes
            otpService.sendOtp(user.getEmail());

            logger.info("Utilisateur enregistré avec succès: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur", e);
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur.");
        }
    }

}
