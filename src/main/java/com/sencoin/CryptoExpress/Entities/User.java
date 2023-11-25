package com.sencoin.CryptoExpress.Entities;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;


@Entity
@Data
@Table(name = "ts_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    @Column(name = "email_confirmed")
    private boolean emailConfirmed;

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    @Transient // Ne pas persister dans la base de données
    private String confirmPassword;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "otp_enabled")
    private boolean otpEnabled;

    public void setVerified(boolean b) {
    }


}
