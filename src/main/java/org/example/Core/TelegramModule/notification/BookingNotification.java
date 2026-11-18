package org.example.Core.TelegramModule.notification;

import org.example.Core.TelegramModule.telegram.TelegramBotService;

public class BookingNotification {

    private final TelegramBotService telegram =
            new TelegramBotService();

    public void sendCustomerConfirmation(
            String chatId,
            String customerName,
            String roomName,
            String checkIn,
            String checkOut,
            String bookingId
    ) throws Exception {

        String message =
                "Booking Confirmed!\n\n"
                        + "Hello " + customerName + ",\n\n"
                        + "Your room booking is confirmed.\n\n"
                        + "Booking ID: " + bookingId + "\n"
                        + "Room: " + roomName + "\n"
                        + "Check-in: " + checkIn + "\n"
                        + "Check-out: " + checkOut + "\n\n"
                        + "Thank you for choosing our hotel!";

        telegram.sendMessage(chatId, message);

        System.out.println(
                "Booking confirmation sent successfully!"
        );
    }
}
