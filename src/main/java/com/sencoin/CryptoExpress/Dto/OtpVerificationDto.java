package com.sencoin.CryptoExpress.Dto;


import lombok.Data;

@Data
public class OtpVerificationDto {
    private String email;
    private String otp;
}
