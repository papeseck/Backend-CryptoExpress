package com.sencoin.CryptoExpress.Service;


import java.util.Random;

public class OTPManager {

    public static String generateOTP() {
        Random random = new Random();
        int otp = 100_000 + random.nextInt(900_000);
        return String.valueOf(otp);
    }
}