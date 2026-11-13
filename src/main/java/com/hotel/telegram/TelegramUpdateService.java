package com.hotel.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramUpdateService {

    private final String botToken;
    private final HttpClient client;
    private final ObjectMapper mapper;

    private final TelegramBotService telegram =
            new TelegramBotService();

    private long lastUpdateId = 0;

    public TelegramUpdateService() {

        botToken = System.getenv("TELEGRAM_BOT_TOKEN");

        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException(
                    "Telegram Bot Token is missing!"
            );
        }

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        mapper = new ObjectMapper();
    }

    public void startPolling() {

        System.out.println("Telegram Bot is running...");

        while (true) {

            try {

                String url =
                        "https://api.telegram.org/bot"
                                + botToken
                                + "/getUpdates?offset="
                                + (lastUpdateId + 1)
                                + "&timeout=25";

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .timeout(Duration.ofSeconds(35))
                                .GET()
                                .build();

                HttpResponse<String> response =
                        client.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                JsonNode root =
                        mapper.readTree(response.body());

                if (!root.path("ok").asBoolean()) {

                    System.out.println(
                            "Telegram Error: " + response.body()
                    );

                    Thread.sleep(3000);
                    continue;
                }

                JsonNode results = root.path("result");

                for (JsonNode update : results) {

                    lastUpdateId =
                            update.path("update_id").asLong();

                    JsonNode message =
                            update.path("message");

                    if (message.isMissingNode()) {
                        continue;
                    }

                    String text =
                            message.path("text").asText("");

                    String chatId =
                            message.path("chat")
                                    .path("id").asText("");

                    if (chatId.isBlank()) {
                        continue;
                    }

                    if (text.equals("/start")) {

                        System.out.println(
                                "Customer Chat ID: " + chatId
                        );

                        telegram.sendMessage(
                                chatId,
                                "Hello! Welcome to our Hotel "
                                        + "Booking System.\n\n"
                                        + "Your Telegram account is ready."
                        );

                    } else if (text.startsWith("/start@")) {

                        telegram.sendMessage(
                                chatId,
                                "Hello! Welcome to our Hotel "
                                        + "Booking System."
                        );
                    }
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                System.out.println("Bot stopped.");
                return;

            } catch (Exception e) {

                System.out.println(
                        "Error: " + e.getMessage()
                );

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
