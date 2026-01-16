package com.example.common.jwt.dto;

public enum JwtType {
    ACCESS,
    REFRESH;

    public static JwtType fromString(String value) {
        for (JwtType t : JwtType.values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }
}
