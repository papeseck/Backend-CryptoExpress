package com.sencoin.CryptoExpress.Dto;


import lombok.Data;

@Data
public class OtpVerificationDto {
    private String otp;
    private String email;

    //private String password;
}
