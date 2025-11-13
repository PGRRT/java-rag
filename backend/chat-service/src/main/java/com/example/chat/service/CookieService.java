//package com.example.medai.services;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseCookie;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CookieService {
//
//    @Value("${spring.profiles.active}")
//    private String activeProfile;
//
//    /**
//     * Creates a ResponseCookie that can be directly used in a ResponseEntity header
//     * @param name the cookie name
//     * @param value the cookie value
//     * @param maxAge maximum age in milliseconds
//     * @return ResponseCookie
//     */
//    public ResponseCookie createCookie(String name, String value, int maxAge) {
//        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
//                .path("/")
//                .httpOnly(true)
//                .maxAge(Duration.ofMillis(maxAge)); // maxAge in milliseconds
//
//        if ("prod".equals(activeProfile)) {
//            builder.secure(true); // HTTPS only in production
//        }
//
//        // optional: add SameSite Strict for security
////        builder.sameSite("Strict");
//
//        return builder.build();
//    }
//
//    /**
//     * Creates a cookie to clear/delete a value (for logout)
//     * @param name the cookie name
//     * @return ResponseCookie with zero max age
//     */
//    public ResponseCookie clearCookie(String name) {
//        return ResponseCookie.from(name, "")
//                .path("/")
//                .httpOnly(true)
//                .maxAge(Duration.ZERO)
//                .build();
//    }
//
//    public ResponseCookie clearAccessTokenCookie() {
//        return clearCookie("accessToken");
//    }
//
//    public ResponseCookie clearRefreshTokenCookie() {
//        return clearCookie("refreshToken");
//    }
//
//
//}
