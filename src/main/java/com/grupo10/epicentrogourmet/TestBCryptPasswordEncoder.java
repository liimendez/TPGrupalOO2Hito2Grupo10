package com.grupo10.epicentrogourmet;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCryptPasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "user";
        String hash = encoder.encode(password);

        System.out.println("Password original: " + password);
        System.out.println("Hash BCrypt: " + hash);

        // Para mas usuarios
        System.out.println("\n--- Para admin2 ---");
        System.out.println(encoder.encode("admin"));
    }
}