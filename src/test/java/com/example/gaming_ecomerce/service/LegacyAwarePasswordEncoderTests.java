package com.example.gaming_ecomerce.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyAwarePasswordEncoderTests {

    private final LegacyAwarePasswordEncoder encoder = new LegacyAwarePasswordEncoder();

    @Test
    void verifiesAndUpgradesLegacyUnhashedPasswords() {
        assertTrue(encoder.matches("old-password", "old-password"));
        assertTrue(encoder.upgradeEncoding("old-password"));
    }

    @Test
    void newPasswordsAreStoredAsBcryptHashes() {
        String encoded = encoder.encode("secure-password");

        assertTrue(encoded.startsWith("$2"));
        assertTrue(encoder.matches("secure-password", encoded));
        assertFalse(encoder.matches("incorrect-password", encoded));
        assertFalse(encoder.upgradeEncoding(encoded));
    }
}
