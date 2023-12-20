package com.sencoin.CryptoExpress.Controller;

import com.sencoin.CryptoExpress.Dto.OtpVerificationDto;
import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.Service.OtpService;
import com.sencoin.CryptoExpress.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class OtpController {
    @Autowired
    private OtpService otpService;

    private RegistrationService registrationService;


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndRegister(@RequestBody OtpVerificationDto otpVerificationDto) {
        try {
            String email = otpVerificationDto.getEmail();
            String otp = otpVerificationDto.getOtp();

            // Vérifiez si l'OTP est valide
            if (otpService.isOtpValid(otp)) {
                // Si l'OTP est valide, continuez avec l'inscription
                User user = new User();
               // user.setPassword(otpVerificationDto.getPassword());
                user.setEmail(email);

                registrationService.registerUser(user);

                return ResponseEntity.ok("Inscription réussie.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code OTP invalide.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la vérification de l'OTP.");
        }
    }

}
