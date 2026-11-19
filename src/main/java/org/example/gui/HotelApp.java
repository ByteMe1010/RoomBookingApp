package org.example.gui;

import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Display.FX.RootWindow;
import com.codehack.nngf.js.JS;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.db.SqlServerDatabaseModule;
import org.example.Core.DatabaseModule.model.ShopOwner;
import org.example.Core.GoogleModule.GmailIntegrationService;
import org.example.Core.TelegramModule.telegram.TelegramUpdateService;
import org.example.logic.AuthModule.AuthService;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HotelApp extends Application {
    public static DatabaseModule databaseModule = new SqlServerDatabaseModule(System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));
    public static AuthService authService = new AuthService(databaseModule);
    public static ShopOwner  shopOwner=null;
    public static GmailIntegrationService gmailService;
    @Override
    public void start(Stage primaryStage) throws Exception {
        gmailService = GmailIntegrationService.fromEnvironment(databaseModule);
        primaryStage.setOnCloseRequest(event -> {
            if (gmailService != null) gmailService.shutdown();
            Platform.exit();
            System.exit(0);
        });

        // Start Telegram bot polling (if token configured) on a daemon thread.
        String tgToken = System.getenv("TELEGRAM_BOT_TOKEN");
        if (tgToken != null && !tgToken.isBlank()) {
            Thread tgThread = new Thread(() -> {
                try {
                    new TelegramUpdateService(databaseModule).startPolling();
                } catch (Exception e) {
                    System.out.println("Telegram bot error: " + e.getMessage());
                }
            }, "telegram-bot");
            tgThread.setDaemon(true);
            tgThread.start();
        }

        // Scheduler: clear expired telegram link codes every minute.
        var codeScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "telegram-code-cleanup");
            t.setDaemon(true);
            return t;
        });
        codeScheduler.scheduleWithFixedDelay(() -> {
            try { databaseModule.clearExpiredTelegramLinkCodes(); }
            catch (Exception ignored) { }
        }, 1, 1, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new Thread(codeScheduler::shutdownNow));
        primaryStage.setMaximized(true);
        NWindow window = new NWindow("App");
        window.setExternalDocument(getClass().getResource("/com/codehack/nngf/App/app/index.html").toExternalForm());

        LoginHandler.setupLogin(window,win -> {
            DashboardHandler.onLogin(window);
        });

        RootWindow.setup(primaryStage);
        RootWindow.attach(window);
        RootWindow.Start();
    }
    public static void showToast(NWindow window, String message, String level) {
        window.executeJS(JS.Window().method("showToast", message, level, 4000).E().toString());
    }

    public static void showToastWarn(NWindow window, String message) {
        showToast(window, message, "warn");
    }
}
