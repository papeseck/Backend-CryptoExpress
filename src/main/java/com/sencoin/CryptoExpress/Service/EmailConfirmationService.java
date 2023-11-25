package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class EmailConfirmationService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmation d'e-mail");
        message.setText("Votre OTP de confirmation d'e-mail est : " + otp);
        javaMailSender.send(message);
    }
    @Autowired
    private UserRepository userRepository;
    public boolean isEmailConfirmed(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(User::isEmailConfirmed).orElse(false);
    }
}
