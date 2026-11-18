package org.example.Core.TelegramModule;

import org.example.Core.TelegramModule.notification.BookingNotification;
import org.example.Core.TelegramModule.telegram.TelegramUpdateService;

public class Main {

    public static void main(String[] args) {
        // 1. Start the Telegram Bot Polling Service in a separate background thread
        // This allows the bot to listen when customers click the /start link
        Thread botThread = new Thread(() -> {
            try {
                TelegramUpdateService updateService = new TelegramUpdateService();
                updateService.startPolling();
            } catch (Exception e) {
                System.out.println("Bot Error: " + e.getMessage());
            }
        });

        botThread.start();
        System.out.println("Background Telegram listener started...");

        // 2. Example: Send a booking confirmation message manually
        // (In real integration, this is triggered when the owner clicks "Accept" on a booking request)
        try {
            BookingNotification notification = new BookingNotification();

            // Replace with a real chat ID once the customer has clicked /start on your bot
            String customerChatId = "1718525359";

            notification.sendCustomerConfirmation(
                    customerChatId,
                    "Kamal",
                    "Deluxe Room",
                    "2026-12-01",
                    "2026-12-05",
                    "B001"
            );

        } catch (Exception e) {
            System.out.println("Notification Error: " + e.getMessage());
        }
    }
}