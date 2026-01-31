package com.example.user.service;

public interface BloomFilterService {
     boolean isEmailAvailable(String email);
     void addEmail(String email);
}
