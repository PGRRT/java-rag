package com.example.user.utility;

public class NormalizeEmail {
    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.toLowerCase().trim();
    }
}
