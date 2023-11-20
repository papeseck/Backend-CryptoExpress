package com.sencoin.CryptoExpress.Dto;

import com.sencoin.CryptoExpress.Entities.User;

import lombok.Data;

@Data
public class UserDTO {

    private String email;
    private String password;
    private String confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }
    // Getter and setter for the 'email' field
    public String getEmail() {
        return email;
    }


    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setVerified(false);
        return user;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}




