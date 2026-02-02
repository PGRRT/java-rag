package com.example.user.service.impl;

import com.example.user.service.BloomFilterService;
import com.example.user.utility.NormalizeEmail;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class BloomFilterServiceImpl implements BloomFilterService {
    private final RedissonClient redissonClient;
    private RBloomFilter<String> emailBloomFilter;

    @PostConstruct
    public void init() {
        try {
            emailBloomFilter = redissonClient
                    .getBloomFilter("user:emails:bloomfilter");

            boolean initialized = emailBloomFilter.tryInit(1_000_000L, 0.001);

            if (initialized) {
                log.info("New Bloom Filter initialized with 1M capacity.");
            } else {
                log.info("Connected to existing Bloom Filter. Current estimated size: {}", emailBloomFilter.count());
            }
        } catch (Exception e) {
            log.error("Failed to initialize Bloom Filter. Check your spring.data.redis settings!");
        }
    }

    /**
     * Checks email availability using Bloom Filter logic.
     * TRUE:  Guaranteed NOT in DB. 100% safe to register.
     * FALSE: MIGHT be in DB. DB check MANDATORY.
     * * @param String email to verify.
     * @return true if definitely available, false if possibly taken.
     */
    public boolean isEmailAvailable(String email) {
        if (email == null) return true;
        String normalizedEmail = NormalizeEmail.normalize(email);

        return !emailBloomFilter.contains(normalizedEmail);
    }

    public void addEmail(String email) {
        if (email == null) {
            return;
        }
        String normalizedEmail = NormalizeEmail.normalize(email);
        emailBloomFilter.add(normalizedEmail);
    }

}
