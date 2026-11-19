package org.example.Core.DatabaseModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Small JDBC connection factory used by the Gmail module and DB smoke/integration tests.
 * Supports SQL login (user/password) and Windows integrated security (null credentials).
 */
public final class JdbcConnections {
    private JdbcConnections() { }

    public static boolean usesIntegratedSecurity(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.contains("integratedsecurity=true")
                || lower.contains("integrated security=true")
                || lower.contains("authentication=integrated")
                || lower.contains("authenticationscheme=ntlm");
    }

    public static Connection open(String url, String user, String password) throws SQLException {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("JDBC URL is required");
        if (user == null || user.isBlank()) {
            // Integrated security / no SQL login.
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, user, password);
    }
}