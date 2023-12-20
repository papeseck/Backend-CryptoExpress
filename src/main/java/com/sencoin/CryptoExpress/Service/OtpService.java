package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.OtpInfo;
import com.sencoin.CryptoExpress.repository.OtpInfoRepository;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender javaMailSender;

    private String verifiedEmail;

    @Autowired
    private OtpInfoRepository otpInfoRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendOtp(String email) {

        System.out.println("userRepository.existsByEmail(email)" + userRepository.existsByEmail(email));

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet e-mail est déjà utilisé.");
        }

        String otp = generateOtp();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        // Sauvegarder l'e-mail vérifié
        setVerifiedEmail(email);

        // Sauvegarder l'OTP avec l'e-mail associé
        otpInfoRepository.save(new OtpInfo(email, otp, expirationTime));
        System.out.println("OTP généré et sauvegardé : " + otp);
    }

    public void setVerifiedEmail(String email) {
        this.verifiedEmail = email;
    }

    public String getVerifiedEmail() {
        return verifiedEmail;
    }

    public boolean isOtpValid(String otp) {
        // Récupérer l'e-mail vérifié
        String verifiedEmail = getVerifiedEmail();

        // Récupérer l'OTP associé à l'e-mail vérifié depuis la base de données
        OtpInfo otpInfo = otpInfoRepository.findByEmail(verifiedEmail)
                .orElseThrow(() -> {
                    System.out.println("Aucun OTP trouvé pour cet e-mail : " + verifiedEmail);
                    return new RuntimeException("Aucun OTP trouvé pour cet e-mail.");
                });

        if (otpInfo.getExpirationTime().isBefore(LocalDateTime.now())) {
            // Supprimer l'OTP expiré de la base de données
            otpInfoRepository.delete(otpInfo);
            System.out.println("L'OTP est expiré.");
            throw new RuntimeException("L'OTP est expiré.");
        }

        // Comparer l'OTP fourni avec celui stocké
        boolean isOtpValid = otp.equals(otpInfo.getOtp());

        // Si l'OTP est valide, désactiver l'OTP pour cet utilisateur
        if (isOtpValid) {
            // Supprimer l'OTP de la base de données
            otpInfoRepository.delete(otpInfo);
        }

        return isOtpValid;
    }

    private String generateOtp() {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(9000000);
        return String.valueOf(otpValue);
    }

    private void sendEmail(String to, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Code OTP");
            message.setText(body);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail.");
        }
    }
}
