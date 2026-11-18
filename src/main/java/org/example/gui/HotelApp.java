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
import org.example.logic.AuthModule.AuthService;

public class HotelApp extends Application {
    public static DatabaseModule databaseModule = new SqlServerDatabaseModule("jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB;user=java_dev;password=123456789;encrypt=true;trustServerCertificate=true;","java_dev","123456789");
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
