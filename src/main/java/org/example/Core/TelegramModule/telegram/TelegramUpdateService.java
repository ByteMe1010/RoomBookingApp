package org.example.Core.TelegramModule.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Core.TelegramModule.database.TelegramDatabase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramUpdateService {

    private final String botToken;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final TelegramBotService telegram = new TelegramBotService();
    private final TelegramDatabase telegramDb;

    private long lastUpdateId = 0;

    public TelegramUpdateService() {
        this(null);
    }

    public TelegramUpdateService(org.example.Core.DatabaseModule.db.DatabaseModule db) {
        botToken = System.getenv("TELEGRAM_BOT_TOKEN");

        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("Telegram Bot Token is missing!");
        }

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        mapper = new ObjectMapper();
        telegramDb = new TelegramDatabase(db);
    }

    public void startPolling() {
        System.out.println("Telegram Bot is running and listening for customer messages...");

        while (true) {
            try {
                String url = "https://api.telegram.org/bot"
                        + botToken
                        + "/getUpdates?offset="
                        + (lastUpdateId + 1)
                        + "&timeout=25";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(35))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                JsonNode root = mapper.readTree(response.body());

                if (!root.path("ok").asBoolean()) {
                    System.out.println("Telegram Error: " + response.body());
                    Thread.sleep(3000);
                    continue;
                }

                JsonNode results = root.path("result");

                for (JsonNode update : results) {
                    lastUpdateId = update.path("update_id").asLong();

                    JsonNode message = update.path("message");
                    if (message.isMissingNode()) {
                        continue;
                    }

                    String text = message.path("text").asText("");
                    String chatId = message.path("chat").path("id").asText("");

                    if (chatId.isBlank()) {
                        continue;
                    }

                    // Handle /start register_<code> deep link first (must precede generic /start)
                    if (text.startsWith("/start register_")) {
                        String rcode = text.substring("/start register_".length()).trim();
                        if (!rcode.isEmpty()) {
                            boolean rok = telegramDb.consumeLinkCode(rcode, Long.parseLong(chatId));
                            if (rok) {
                                telegram.sendMessage(chatId, "Successfully linked! You will now receive booking notifications here.");
                                telegramDb.saveMessageRecord(Long.parseLong(chatId), "Linked via deep link " + rcode, "SENT");
                            } else {
                                telegram.sendMessage(chatId, "Invalid or expired code. Please ask the front desk for a new one.");
                            }
                        }
                    } else if (text.startsWith("/start")) {
                        String[] parts = text.split(" ");

                        if (parts.length > 1) {
                            String bookingRef = parts[1]; // This is the booking reference (e.g., B001)
                            System.out.println("Customer started bot with Booking Ref: " + bookingRef);

                            // Save the Telegram Chat ID into the database linked to this booking
                            boolean isLinked = telegramDb.linkCustomerTelegram(bookingRef, Long.parseLong(chatId));

                            if (isLinked) {
                                telegram.sendMessage(
                                        chatId,
                                        "Hello! Your Telegram account is successfully linked to your hotel booking (" + bookingRef + ").\n\n"
                                                + "You will receive your confirmation details here!"
                                );
                                telegramDb.saveMessageRecord(Long.parseLong(chatId), "Linked booking " + bookingRef, "SENT");
                            } else {
                                telegram.sendMessage(
                                        chatId,
                                        "Welcome! However, we could not find a pending booking matching reference: " + bookingRef
                                );
                            }
                        } else {
                            telegram.sendMessage(
                                    chatId,
                                    "Hello! Welcome to our Hotel Booking System.\n\nPlease use your unique booking link to register."
                            );
                        }
                    } else if (text.startsWith("/register")) {
                        String[] rparts = text.split(" ");
                        String rcode = (rparts.length > 1) ? rparts[1].trim() : "";
                        if (rcode.isEmpty()) {
                            telegram.sendMessage(chatId, "Usage: /register <code> (code given at the front desk).");
                        } else {
                            boolean rok = telegramDb.consumeLinkCode(rcode, Long.parseLong(chatId));
                            if (rok) {
                                telegram.sendMessage(chatId, "Successfully linked! You will now receive booking notifications here.");
                                telegramDb.saveMessageRecord(Long.parseLong(chatId), "Linked via code " + rcode, "SENT");
                            } else {
                                telegram.sendMessage(chatId, "Invalid or expired code. Please ask the front desk for a new one.");
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Bot polling stopped.");
                return;
            } catch (Exception e) {
                System.out.println("Error in Telegram polling: " + e.getMessage());
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