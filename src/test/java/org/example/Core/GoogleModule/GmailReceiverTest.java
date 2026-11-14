package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

class GmailReceiverTest {
    @Test void receiverUsesMatchingLoopbackRedirectAndStaysOpenUntilStopped() throws Exception {
        var receiver = new GmailAuthentication.LoggedReceiver();
        URI redirect = URI.create(receiver.getRedirectUri());
        try {
            assertEquals("http", redirect.getScheme());
            assertEquals("127.0.0.1", redirect.getHost());
            assertEquals("/Callback", redirect.getPath());
            assertTrue(redirect.getPort() > 0);
            try (Socket socket = new Socket(redirect.getHost(), redirect.getPort())) {
                assertTrue(socket.isConnected());
            }
        } finally { receiver.stop(); }
        assertThrows(java.io.IOException.class, () -> {
            try (Socket ignored = new Socket(redirect.getHost(), redirect.getPort())) { }
        });
    }
}
