package com.sencoin.CryptoExpress.Dto;


import lombok.Data;

@Data
public class RegistrationDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}


