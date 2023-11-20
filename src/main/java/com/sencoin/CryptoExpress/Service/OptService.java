package com.sencoin.CryptoExpress.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OptService {

    @Autowired
    private JavaMailSender javaMailSender;

    public String generateAndSendOtp(String email) {
        String otp = OTPManager.generateOTP();
        sendOtpByEmail(email, otp);
        return otp;
    }

    public void sendOtpByEmail(String recipient, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("diabel2298@gmal.com");
        message.setTo(recipient);
        message.setSubject("Code OTP de votre application");
        message.setText("Votre code OTP est : " + otp);

        javaMailSender.send(message);
    }

    public boolean verifyOtp(String userInput, String storedOtp) {
        return userInput.equals(storedOtp);
    }
}
