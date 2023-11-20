package com.sencoin.CryptoExpress.Entities;
import lombok.Data;

import javax.persistence.*;


@Entity
@Data
@Table(name = "ts_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username; // Vous pouvez utiliser l'e-mail comme nom d'utilisateur
    private String password;
    private String confirmPassword;
    private String email;
    private String otp; // Le code OTP
    private boolean verified; // Un indicateur pour savoir si l'OTP a été vérifié avec succès
}
