package com.sencoin.CryptoExpress.Entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_info")
public class OtpInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;


    public OtpInfo() {
    }

    // Constructeur avec les paramètres email et otp
    public OtpInfo(String email, String otp ,LocalDateTime expirationTime) {
        this.email = email;
        this.otp = otp;
        this.expirationTime = expirationTime;
    }
}

