package org.example.Core.GoogleModule;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import java.io.Reader;
import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public final class GmailAuthentication {
    private static final Logger LOG = Logger.getLogger(GmailAuthentication.class.getName());

    static final class LoggedReceiver implements VerificationCodeReceiver {
        private boolean started;
        private final LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setHost("127.0.0.1").setPort(0).setCallbackPath("/Callback").build();

        public String getRedirectUri() throws IOException {
            LOG.info("Starting OAuth callback receiver with loopback redirect host and an ephemeral port.");
            String redirect = receiver.getRedirectUri();
            started = true;
            LOG.info("OAuth callback receiver started: host=" + receiver.getHost()
                    + ", port=" + receiver.getPort() + ", path=" + receiver.getCallbackPath());
            return redirect;
        }
        public String waitForCode() throws IOException {
            LOG.info("OAuth callback receiver is waiting for the browser redirect.");
            try { return receiver.waitForCode(); }
            finally { LOG.info("OAuth callback wait ended."); }
        }
        public void stop() throws IOException {
            try { receiver.stop(); }
            finally { LOG.info(started ? "OAuth callback receiver stopped." : "OAuth callback receiver was not started."); }
        }
    }

    public Gmail connect(Path clientFile, Path tokenDirectory) throws Exception {
        Path repo = Path.of("").toAbsolutePath().normalize();
        Path tokens = tokenDirectory.toAbsolutePath().normalize();
        if (tokens.startsWith(repo)) throw new IllegalArgumentException("Token directory must be outside the working directory");
        Files.createDirectories(tokens);
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets secrets;
        try (Reader reader = Files.newBufferedReader(clientFile)) { secrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), reader); }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, GsonFactory.getDefaultInstance(), secrets, List.of(GmailScopes.GMAIL_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(tokens.toFile())).setAccessType("offline").build();
        LoggedReceiver receiver = new LoggedReceiver();
        LOG.info("OAuth authorization started; checking for a reusable stored credential.");
        var credential = new AuthorizationCodeInstalledApp(flow, receiver, url -> {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                throw new IOException("Browser launch is unavailable");
            }
            LOG.info("Opening the Google authorization page in the default browser.");
            Desktop.getDesktop().browse(URI.create(url));
        }).authorize("hotel");
        LOG.info(receiver.started ? "OAuth authorization completed." : "OAuth authorization completed using a stored credential; browser authorization was skipped.");
        Gmail gmail = new Gmail.Builder(transport, GsonFactory.getDefaultInstance(), credential).setApplicationName("RoomBookingApp").build();
        LOG.info("Gmail service created.");
        return gmail;
    }
}
