package com.sencoin.CryptoExpress.Service;

import com.sencoin.CryptoExpress.Entities.OtpInfo;
import com.sencoin.CryptoExpress.Entities.User;
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

    @Autowired
    private OtpInfoRepository otpInfoRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendOtp(String email) {
        // Vérifier si l'OTP est déjà activé pour cet utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé pour cet e-mail."));

        if (!user.isOtpEnabled()) {
            // Générer un OTP
            String otp = generateOtp();

            // Enregistrer l'OTP dans la base de données (à associer à l'utilisateur) avec expiration en 5 minutes
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            otpInfoRepository.save(new OtpInfo(email, otp, expirationTime));

            // Envoyer l'OTP par e-mail
            sendEmail(email, "Votre OTP est : " + otp);
        } else {
            throw new RuntimeException("L'OTP est déjà activé pour cet utilisateur.");
        }
    }

    public boolean isOtpValid(String email, String otp) {
        // Récupérer l'OTP associé à l'utilisateur depuis la base de données
        OtpInfo otpInfo = otpInfoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun OTP trouvé pour cet e-mail."));

        // Vérifier si l'OTP est expiré
        if (otpInfo.getExpirationTime().isBefore(LocalDateTime.now())) {
            // Supprimer l'OTP expiré de la base de données
            otpInfoRepository.delete(otpInfo);
            throw new RuntimeException("L'OTP est expiré.");
        }

        // Comparer l'OTP fourni avec celui stocké
        boolean isOtpValid = otp.equals(otpInfo.getOtp());

        // Si l'OTP est valide, désactiver l'OTP pour cet utilisateur
        if (isOtpValid) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé pour cet e-mail."));
            user.setOtpEnabled(false);
            userRepository.save(user);
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
            // Log l'exception pour un débogage ultérieur
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail.");
        }
    }
}
