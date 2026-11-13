package com.hotel;

import com.hotel.notification.BookingNotification;

public class Main {

    public static void main(String[] args) {

        try {

            BookingNotification notification =
                    new BookingNotification();

            notification.sendCustomerConfirmation(
                    "1718525359",
                    "Kamal",
                    "Deluxe Room",
                    "2026-12-01",
                    "2026-12-05",
                    "B001"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
    }
}