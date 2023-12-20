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
                logger.error("Cet e-mail est déjà utilisé: {}", user.getEmail());
                throw new RuntimeException("Cet e-mail est déjà utilisé.");
            }

            // Encoder le mot de passe avant de le sauvegarder
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Ajouter un log pour imprimer l'e-mail avant l'enregistrement
            logger.info("Enregistrement de l'utilisateur avec l'e-mail: {}", user.getEmail());

            // Enregistrer l'utilisateur dans la base de données
            userRepository.save(user);
            logger.info("Utilisateur enregistré avec succès: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur", e);

            // Ajouter ces logs pour imprimer la stack trace complète de l'exception
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error(element.toString());
            }

            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur.");
        }
    }

}

