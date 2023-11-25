package com.sencoin.CryptoExpress.Controller;

import com.sencoin.CryptoExpress.Dto.OtpVerificationDto;
import com.sencoin.CryptoExpress.Service.OtpService;
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

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationDto otpVerificationDto) {
        if (otpService.isOtpValid(otpVerificationDto.getEmail(), otpVerificationDto.getOtp())) {
            return ResponseEntity.ok("Vérification OTP réussie. Utilisateur enregistré avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code OTP invalide.");
        }
    }
}
