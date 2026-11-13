package com.hotel.telegram;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TelegramBotService {

    private final String botToken;
    private final HttpClient client;

    public TelegramBotService() {

        botToken = System.getenv("TELEGRAM_BOT_TOKEN");

        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException(
                    "Telegram Bot Token is missing!"
            );
        }

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendMessage(
            String chatId,
            String message
    ) throws Exception {

        String url =
                "https://api.telegram.org/bot"
                        + botToken
                        + "/sendMessage";

        String requestBody =
                "chat_id=" + URLEncoder.encode(
                        chatId,
                        StandardCharsets.UTF_8
                )
                        + "&text=" + URLEncoder.encode(
                        message,
                        StandardCharsets.UTF_8
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(requestBody)
                )
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() != 200
                || !response.body().contains("\"ok\":true")) {

            throw new RuntimeException(
                    "Telegram API Error: " + response.body()
            );
        }

        System.out.println(
                "Telegram message sent successfully!"
        );
    }
}
