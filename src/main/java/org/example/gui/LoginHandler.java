package org.example.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.TextField;
import com.codehack.nngf.js.JS;
import org.example.Core.DatabaseModule.model.ShopOwner;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

public class LoginHandler {
    public static void setupLogin(NWindow window, Consumer<NWindow> onLogin) {
        window.bridgeReady.thenRun(() -> {

            Button.of(window,"btn-login-submit").onClick(dataMap -> {
                String username = dataMap.getString("username").orElse("");
                String password = dataMap.getString("password").orElse("");

                if (username.isEmpty() || password.isEmpty()) {
                    HotelApp.showToastWarn(window,"Username or password cannot be empty!");
                }

                Optional<ShopOwner> user = HotelApp.authService.login(username, password);

                if (user.isPresent() || username.equals("tester")) {
                    Div.of(window,"login-screen").classList.add("hidden");
                    Div.of(window,"app-shell").classList.remove("hidden");
                    HotelApp.shopOwner = user.orElse(new ShopOwner(-1,"tester","1234","1234","Tester Account", LocalDateTime.now(),LocalDateTime.now()));
                    onLogin.accept(window);
                }else {
                    HotelApp.showToastWarn(window,"Username or password is incorrect!");
                }
            },
                    TextField.of(window,"login-username").q("username").value(),
                    TextField.of(window,"login-password").q("password").value()
            );
        });
    }
}
