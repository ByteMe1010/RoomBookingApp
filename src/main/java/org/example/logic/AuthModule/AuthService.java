package org.example.logic.AuthModule;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.model.ShopOwner;
import org.example.Core.Utils.PasswordManager;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final DatabaseModule databaseModule;
    public AuthService(DatabaseModule databaseModule) {
        this.databaseModule = databaseModule;
    }

    public Optional<ShopOwner> login(String username, String password) {
        try {
            ShopOwner shopOwner = databaseModule.getShopOwnerByUsername(username);
            if (shopOwner == null) return Optional.empty();

            if(PasswordManager.verifyPassword(password,shopOwner.getSalt(), shopOwner.getPasswordHash()))
                return Optional.of(shopOwner);
            else return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Login failed, SQL Exception: " + e.getMessage());
            return Optional.empty();
        }
    }
}
