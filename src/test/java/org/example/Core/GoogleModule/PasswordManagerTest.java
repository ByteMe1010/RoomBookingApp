package org.example.Core.GoogleModule;

import org.example.Core.Utils.PasswordManager;

import java.util.Base64;

public class PasswordManagerTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testHashPasswordGeneratesValidSaltAndHash();
        testHashPasswordGeneratesUniqueSaltAndHash();
        testVerifyPasswordReturnsTrueForMatch();
        testVerifyPasswordReturnsFalseForWrongPassword();
        testVerifyPasswordReturnsFalseForTamperedHash();
        testHashPasswordThrowsExceptionForNull();

        System.out.println("\n----------------------------------------");
        System.out.println("Passed: " + passed + " | Failed: " + failed);
        System.out.println("----------------------------------------");
    }

    private static void testHashPasswordGeneratesValidSaltAndHash() {
        try {
            PasswordManager.HashResult result = PasswordManager.hashPassword("MySecret@123");

            assertTrue(result != null, "HashResult should not be null");
            assertTrue(result.salt != null && !result.salt.isBlank(), "Salt should not be null or blank");
            assertTrue(result.hash != null && !result.hash.isBlank(), "Hash should not be null or blank");

            // Decode Base64 and verify salt (16 bytes) and hash (32 bytes = 256 bits) lengths
            byte[] decodedSalt = Base64.getDecoder().decode(result.salt);
            byte[] decodedHash = Base64.getDecoder().decode(result.hash);

            assertTrue(decodedSalt.length == 16, "Decoded salt length should be 16 bytes");
            assertTrue(decodedHash.length == 32, "Decoded hash length should be 32 bytes");

            markPassed("testHashPasswordGeneratesValidSaltAndHash");
        } catch (Exception e) {
            markFailed("testHashPasswordGeneratesValidSaltAndHash", e.getMessage());
        }
    }

    private static void testHashPasswordGeneratesUniqueSaltAndHash() {
        try {
            String password = "SamePassword123!";

            PasswordManager.HashResult first = PasswordManager.hashPassword(password);
            PasswordManager.HashResult second = PasswordManager.hashPassword(password);

            // Random salt should produce different salt and hash values for the same password
            assertTrue(!first.salt.equals(second.salt), "Salts should be unique");
            assertTrue(!first.hash.equals(second.hash), "Hashes should be unique");

            markPassed("testHashPasswordGeneratesUniqueSaltAndHash");
        } catch (Exception e) {
            markFailed("testHashPasswordGeneratesUniqueSaltAndHash", e.getMessage());
        }
    }

    private static void testVerifyPasswordReturnsTrueForMatch() {
        try {
            String plainPassword = "HotelAdmin#2026";
            PasswordManager.HashResult hashed = PasswordManager.hashPassword(plainPassword);

            boolean isValid = PasswordManager.verifyPassword(plainPassword, hashed.salt, hashed.hash);
            assertTrue(isValid, "Matching password should return true");

            markPassed("testVerifyPasswordReturnsTrueForMatch");
        } catch (Exception e) {
            markFailed("testVerifyPasswordReturnsTrueForMatch", e.getMessage());
        }
    }

    private static void testVerifyPasswordReturnsFalseForWrongPassword() {
        try {
            PasswordManager.HashResult hashed = PasswordManager.hashPassword("CorrectPassword123");

            boolean isValid = PasswordManager.verifyPassword("WrongPassword123", hashed.salt, hashed.hash);
            assertTrue(!isValid, "Wrong password should return false");

            markPassed("testVerifyPasswordReturnsFalseForWrongPassword");
        } catch (Exception e) {
            markFailed("testVerifyPasswordReturnsFalseForWrongPassword", e.getMessage());
        }
    }

    private static void testVerifyPasswordReturnsFalseForTamperedHash() {
        try {
            PasswordManager.HashResult hashed = PasswordManager.hashPassword("CorrectPassword123");

            // Modify the first character of the hash to simulate tampering
            String tamperedHash = hashed.hash.charAt(0) == 'A'
                    ? "B" + hashed.hash.substring(1)
                    : "A" + hashed.hash.substring(1);

            boolean isValid = PasswordManager.verifyPassword("CorrectPassword123", hashed.salt, tamperedHash);
            assertTrue(!isValid, "Tampered hash should return false");

            markPassed("testVerifyPasswordReturnsFalseForTamperedHash");
        } catch (Exception e) {
            markFailed("testVerifyPasswordReturnsFalseForTamperedHash", e.getMessage());
        }
    }

    private static void testHashPasswordThrowsExceptionForNull() {
        try {
            String nullPassword = null;
            PasswordManager.hashPassword(nullPassword);
            markFailed("testHashPasswordThrowsExceptionForNull", "Expected an exception for null password");
        } catch (RuntimeException e) {
            // PasswordManager wraps NullPointerException inside RuntimeException
            markPassed("testHashPasswordThrowsExceptionForNull");
        } catch (Exception e) {
            markFailed("testHashPasswordThrowsExceptionForNull", "Unexpected exception: " + e.getMessage());
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void markPassed(String testName) {
        passed++;
        System.out.println("[PASS] " + testName);
    }

    private static void markFailed(String testName, String reason) {
        failed++;
        System.out.println("[FAIL] " + testName + " -> " + reason);
    }
}