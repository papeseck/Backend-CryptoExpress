package com.sencoin.CryptoExpress.Service;


import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean emailExists(String email) {
        // Utilisez le UserRepository pour rechercher l'utilisateur par adresse e-mail
        User existingUser = userRepository.findByEmail(email);

        // Si un utilisateur est trouvé, l'adresse e-mail existe déjà
        return existingUser != null;
    }
    public void registerUser(User user) {
        // Logique d'enregistrement de l'utilisateur (à définir)
        // Assurez-vous de stocker l'OTP associé à l'utilisateur dans votre base de données
        userRepository.save(user);
    }

    public String getStoredOtpByEmail(String email) {
        // Logique pour obtenir l'OTP stocké en fonction de l'e-mail de l'utilisateur (à définir)
        User user = userRepository.findByEmail(email);
        return user != null ? user.getOtp() : null;
    }

    public void verifyUser(String email) {
        // Logique pour marquer l'utilisateur comme vérifié (à définir)
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    // Autres méthodes de service...
}


