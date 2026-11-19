package org.example.Core.DatabaseModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Pattern;

public final class JdbcConnections {
    private JdbcConnections() { }

    public static boolean usesIntegratedSecurity(String url) {
        return url != null && Pattern.compile("(?i)(?:^|;)\\s*integratedSecurity\\s*=\\s*true\\s*(?:;|$)").matcher(url).find();
    }

    public static Connection open(String url, String user, String password) throws SQLException {
        if (url == null || url.isBlank()) throw new SQLException("Database URL is required");
        if (usesIntegratedSecurity(url)) return DriverManager.getConnection(url);
        if (user == null || user.isBlank() || password == null || password.isBlank())
            throw new SQLException("Database user and password are required");
        return DriverManager.getConnection(url, user, password);
    }
}
