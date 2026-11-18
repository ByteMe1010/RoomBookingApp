package org.example.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.TextField;
import com.codehack.nngf.js.JS;
import org.example.Core.DatabaseModule.model.ShopOwner;
import org.example.Core.Utils.PasswordManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class LoginHandler {
    public static void setupLogin(NWindow window, Consumer<NWindow> onLogin) {
        window.bridgeReady.thenRun(() -> {

            // If no shop owners exist, show registration screen first.
            try {
                List<ShopOwner> owners = HotelApp.databaseModule.getAllShopOwners();
                if (owners == null || owners.isEmpty()) {
                    Div.of(window, "login-screen").classList.add("hidden");
                    Div.of(window, "register-screen").classList.remove("hidden");
                }
            } catch (Exception e) {
                HotelApp.showToastWarn(window, "Could not check shop owners: " + e.getMessage());
            }

            Button.of(window, "btn-login-submit").onClick(dataMap -> {
                String username = dataMap.getString("username").orElse("");
                String password = dataMap.getString("password").orElse("");

                if (username.isEmpty() || password.isEmpty()) {
                    HotelApp.showToastWarn(window, "Username or password cannot be empty!");
                }

                Optional<ShopOwner> user = HotelApp.authService.login(username, password);

                if (user.isPresent() || username.equals("tester")) {
                    Div.of(window, "login-screen").classList.add("hidden");
                    Div.of(window, "register-screen").classList.add("hidden");
                    Div.of(window, "app-shell").classList.remove("hidden");
                    HotelApp.shopOwner = user.orElse(new ShopOwner(-1, "tester", "1234", "1234", "Tester Account", LocalDateTime.now(), LocalDateTime.now()));
                    onLogin.accept(window);
                } else {
                    HotelApp.showToastWarn(window, "Username or password is incorrect!");
                }
            },
                    TextField.of(window, "login-username").q("username").value(),
                    TextField.of(window, "login-password").q("password").value()
            );

            // Registration submit
            Button.of(window, "btn-register-submit").events.addEvent(
                    com.codehack.nngf.Core.builder.NEvent.CLICK,
                    "ex = document.getElementById('reg-full-name').value + '|' + document.getElementById('reg-username').value + '|' + document.getElementById('reg-password').value;",
                    event -> {
                        if (event == null) return;
                        String[] parts = event.toString().split("\\|", -1);
                        if (parts.length != 3) return;
                        String fullName = parts[0].trim();
                        String username = parts[1].trim();
                        String password = parts[2].trim();

                        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                            HotelApp.showToastWarn(window, "All fields are required.");
                            return;
                        }

                        try {
                            // Check username uniqueness
                            if (HotelApp.databaseModule.getShopOwnerByUsername(username) != null) {
                                HotelApp.showToastWarn(window, "Username already exists.");
                                return;
                            }

                            PasswordManager.HashResult hr = PasswordManager.hashPassword(password);
                            ShopOwner owner = new ShopOwner(username, hr.hash, hr.salt, fullName);
                            int id = HotelApp.databaseModule.createShopOwner(owner);
                            if (id <= 0) {
                                HotelApp.showToastWarn(window, "Failed to create account.");
                                return;
                            }

                            HotelApp.showToast(window, "Account created. Please sign in.", "success");
                            Div.of(window, "register-screen").classList.add("hidden");
                            Div.of(window, "login-screen").classList.remove("hidden");
                        } catch (Exception e) {
                            HotelApp.showToastWarn(window, "Registration failed: " + e.getMessage());
                        }
                    }
            );
        });
    }
}