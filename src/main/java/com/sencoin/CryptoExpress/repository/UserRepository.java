package com.sencoin.CryptoExpress.repository;

import com.sencoin.CryptoExpress.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Vous pouvez ajouter des méthodes personnalisées pour la recherche d'utilisateurs si nécessaire
    User findByEmail(String email);

    User findByUsername(String username);

    // Ajouter une méthode pour récupérer un utilisateur par code OTP
    User findByOtp(String otp);
}