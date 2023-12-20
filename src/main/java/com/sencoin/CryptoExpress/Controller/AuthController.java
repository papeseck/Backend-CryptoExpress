package com.sencoin.CryptoExpress.Controller;

import com.sencoin.CryptoExpress.Dto.EmailVerificationDto;
import com.sencoin.CryptoExpress.Dto.LoginDto;
import com.sencoin.CryptoExpress.Dto.OtpVerificationDto;
import com.sencoin.CryptoExpress.Dto.RegistrationDto;
import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.Service.EmailConfirmationService;
import com.sencoin.CryptoExpress.Service.RegistrationService;
import com.sencoin.CryptoExpress.Service.OtpService;
import com.sencoin.CryptoExpress.Service.LoginService;
import com.sencoin.CryptoExpress.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("api/auth")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final RegistrationService registrationService;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailConfirmationService emailConfirmationService;
    private final LoginService loginService;

    public AuthController(RegistrationService registrationService, OtpService otpService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailConfirmationService emailConfirmationService, LoginService loginService) {
        this.registrationService = registrationService;
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailConfirmationService = emailConfirmationService;
        this.loginService = loginService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtpForRegistration(@RequestBody EmailVerificationDto emailVerifDto) {
        System.out.println("emailVerifDto = " + emailVerifDto.getEmail());
        try {
            String email = emailVerifDto.getEmail();

            // Générer et envoyer l'OTP
            otpService.sendOtp(email);

            return ResponseEntity.ok("OTP envoyé avec succès. Veuillez vérifier votre e-mail et fournir l'OTP pour continuer l'inscription.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'OTP.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationDto otpVerificationDto) {
        try {
            String otp = otpVerificationDto.getOtp();

            // Ajoutez ce log pour vérifier l'OTP reçu
            log.debug("Vérification de l'OTP : {}", otp);

            // Vérifiez si l'OTP est valide
            if (otpService.isOtpValid(otp)) {
                return ResponseEntity.ok("Vérification OTP réussie. Vous pouvez maintenant continuer l'inscription.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code OTP invalide.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la vérification de l'OTP.");
        }
    }

    @PostMapping("/register-final")
    public ResponseEntity<String> registerFinal(@RequestBody RegistrationDto registrationDto) {
        try {
            log.info("Register Final Called with RegistrationDto: {}", registrationDto);

            // Vérifiez que l'e-mail correspond à celui vérifié précédemment
            String verifiedEmail = otpService.getVerifiedEmail(); // Obtenez l'e-mail vérifié de votre service OTP (si disponible)
            if (verifiedEmail == null || !verifiedEmail.equals(registrationDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'e-mail ne correspond pas à celui vérifié précédemment.");
            }

            // ... (le reste du code)

            User user = new User();
            user.setPassword(registrationDto.getPassword());
            user.setConfirmPassword(registrationDto.getConfirmPassword());
            user.setEmail(registrationDto.getEmail());

            registrationService.registerUser(user);

            log.info("Inscription réussie pour l'utilisateur : {}", user.getEmail());

            return ResponseEntity.ok("Inscription réussie.");
        } catch (Exception e) {
            log.error("Erreur lors de la finalisation de l'inscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la finalisation de l'inscription.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        try {
            String email = loginDto.getEmail();
            String password = loginDto.getPassword();

            // Recherchez l'utilisateur par e-mail dans la base de données
            Optional<User> userOptional = userRepository.findByEmail(email);

            // Vérifiez si l'utilisateur existe
            if (userOptional.isPresent()) {
                // Récupérez le mot de passe stocké dans la base de données
                String storedPassword = userOptional.get().getPassword();
                log.info("Mot de passe dans la base de donnees: {}", storedPassword);
                log.info("Utilisateur trouvé par e-mail: {}", email);
                log.info("Mot de passe fourni (en clair): {}", password);

                // Vérifiez si le mot de passe fourni correspond au mot de passe stocké
                if (passwordEncoder.matches(password, storedPassword)) {
                    // Authentification réussie, générer et envoyer l'OTP et le token JWT
                    String responseMessage = generateAndSendOtp(email);

                    // Retournez le message succès avec le token généré et l'OTP envoyé
                    return ResponseEntity.ok(responseMessage);
                } else {
                    // Ajouter des logs pour enregistrer les informations
                    log.warn("Identifiants invalides. E-mail: {}", email);
                    log.warn("Mot de passe fourni (en clair): {}", password);

                    // Retournez une réponse d'échec d'authentification
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides.");
                }
            } else {
                // Ajouter des logs pour enregistrer les informations
                log.warn("Utilisateur non trouvé par e-mail: {}", email);

                // Retournez une réponse d'échec d'authentification
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants invalides.");
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'authentification.");
        }
    }

    private String generateAndSendOtp(String email) {
        try {
            // Générer et envoyer l'OTP
            otpService.sendOtp(email);

            // Générer le token JWT
            String token = generateJWTToken(email);

            // Vous pouvez également stocker le token dans la base de données ou la session utilisateur si nécessaire

            // Retourner le message succès avec le token généré
            return "Authentification réussie. Un OTP a été envoyé à votre adresse e-mail. Token JWT généré : " + token;
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs d'envoi de l'OTP ou de génération du token
            return "Erreur lors de la génération et de l'envoi de l'OTP ou du token JWT.";
        }
    }

    private String generateJWTToken(String email) {
        // Clé secrète pour signer le token (changez cela dans un environnement de production)
        String secretKey = "votre_cle_secrete";

        // Durée de validité du token (12 heures dans cet exemple)
        long expirationTime = 12 * 60 * 60 * 1000; // 12 heures en millisecondes

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
