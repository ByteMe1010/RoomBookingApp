package org.example.Core.Utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordManager {

    // Configuration for the hashing algorithm
    private static final int ITERATIONS = 65536; // High iteration count slows down brute-force attacks
    private static final int SALT_LENGTH = 16;   // 16 bytes (128 bits) salt
    private static final int KEY_LENGTH = 256;   // 256 bits hash output

    /**
     * Simple class to hold both the salt and the hash together.
     */
    public static class HashResult {
        public final String salt;
        public final String hash;

        public HashResult(String salt, String hash) {
            this.salt = salt;
            this.hash = hash;
        }
    }

    /**
     * Takes a plain text password and returns a Base64 encoded salt and hash.
     */
    public static HashResult hashPassword(String password) {
        try {
            // 1. Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // 2. Hash the password with the salt
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // 3. Encode to Base64 strings so they can be easily saved to a database
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return new HashResult(saltBase64, hashBase64);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Takes a plain text password, the stored salt, and the stored hash.
     * Returns true if the password matches the hash, false otherwise.
     */
    public static boolean verifyPassword(String password, String storedSaltBase64, String storedHashBase64) {
        try {
            // 1. Decode the Base64 strings back to byte arrays
            byte[] salt = Base64.getDecoder().decode(storedSaltBase64);
            byte[] expectedHash = Base64.getDecoder().decode(storedHashBase64);

            // 2. Hash the provided password using the stored salt
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] actualHash = factory.generateSecret(spec).getEncoded();

            // 3. Compare the hashes using constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }
}