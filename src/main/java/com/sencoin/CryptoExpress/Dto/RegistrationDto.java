package com.sencoin.CryptoExpress.Dto;


import lombok.Data;

@Data
public class RegistrationDto {
    private String email;
    private String password;
    private String confirmPassword;

}


