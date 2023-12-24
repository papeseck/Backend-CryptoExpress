package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public String loginUser(String email, String password) {
        // Vérifier les informations d'identification de l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé pour cet e-mail."));

        // Trim du mot de passe fourni
        password = password.trim();

        // Log pour débogage
        logger.info("User Password from DB: {}", user.getPassword());
        logger.info("Provided Password: {}", password);

        // Utiliser BCryptPasswordEncoder pour comparer les mots de passe
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Générer le token JWT
            String jwtToken = generateAndSendOtp(email);

            // Envoyer l'OTP
            otpService.sendOtp(email);

            // Activer l'OTP pour cet utilisateur
            user.setOtpEnabled(true);
            userRepository.save(user);

            return jwtToken;
        } else {
            throw new RuntimeException("Mot de passe incorrect.");
        }
    }

    private String generateSecretKey() {
        // Définissez la longueur de la clé secrète (vous pouvez ajuster cela selon vos besoins)
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];

        // Utilisez un générateur de nombres aléatoires sécurisé pour remplir le tableau de bytes
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);

        // Encodez la clé secrète en base64 pour obtenir une représentation sous forme de chaîne
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public String generateAndSendOtp(String email) {
        String secretKey = generateSecretKey();

        // Durée de validité du token (1 heure dans cet exemple)
        long expirationTime = 1 * 60 * 60 * 1000; // 1 heure en millisecondes

        // Date d'expiration du token
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        // Génération du token JWT
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
