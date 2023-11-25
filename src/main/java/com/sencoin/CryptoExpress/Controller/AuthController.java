package com.sencoin.CryptoExpress.Controller;


import com.sencoin.CryptoExpress.Dto.LoginDto;
import com.sencoin.CryptoExpress.Dto.RegistrationDto;
import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.Service.EmailConfirmationService;
import com.sencoin.CryptoExpress.Service.LoginService;
import com.sencoin.CryptoExpress.Service.OtpService;
import com.sencoin.CryptoExpress.Service.RegistrationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailConfirmationService emailConfirmationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDto registrationDto) {
        try {
            User user = new User();
            user.setUsername(registrationDto.getUsername());
            user.setPassword(registrationDto.getPassword());
            user.setConfirmPassword(registrationDto.getConfirmPassword());
            user.setEmail(registrationDto.getEmail());

            registrationService.registerUser(user);

            return ResponseEntity.ok("Inscription réussie. Veuillez vérifier votre e-mail pour l'OTP.");
        } catch (Exception e) {
            // Log l'exception pour un débogage ultérieur
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'inscription.");
        }
    }
    @GetMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        try {

            // Vérifiez si l'e-mail est déjà confirmé
            if (emailConfirmationService.isEmailConfirmed(email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cet e-mail est déjà confirmé.");
            }

            // Envoyez l'OTP
            otpService.sendOtp(email);

            return ResponseEntity.ok("OTP envoyé avec succès. Veuillez vérifier votre e-mail.");
        } catch (Exception e) {
            // Log l'exception pour un débogage ultérieur
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'OTP.");
        }
    }





    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto payload) {
        logger.info("----------",payload.getPassword(),"-----------",payload.getEmail());


        try {
            System.out.println("--------controller--"+payload.getPassword());
            System.out.println("--------controller--"+payload.getEmail());
            // Utilisez loginRequest.getEmail() et loginRequest.getPassword() pour obtenir les informations d'identification
            loginService.loginUser(payload.getEmail(), payload.getPassword());
            return ResponseEntity.ok("Connexion réussie. Veuillez vérifier votre e-mail pour l'OTP.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
