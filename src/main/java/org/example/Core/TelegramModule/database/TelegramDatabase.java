package org.example.Core.TelegramModule.database;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.db.SqlServerDatabaseModule;
import org.example.Core.DatabaseModule.model.BookingRequest;
import org.example.Core.DatabaseModule.model.Customer;
import org.example.Core.DatabaseModule.model.Message;

import java.sql.SQLException;
import java.util.List;

public class TelegramDatabase {

    // Instantiate your team's official database module using their standard environment configuration
    private final DatabaseModule db;

    public TelegramDatabase() {
        String dbUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB;encrypt=true;trustServerCertificate=true;";
        String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "sa";
        String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "YOUR_PASSWORD_HERE";

        this.db = new SqlServerDatabaseModule(dbUrl, dbUser, dbPassword);
    }

    /**
     * Links a customer to their Telegram chat ID using the booking reference.
     * Uses your team's pre-built methods: getPendingBookingRequests(), saveCustomer(), updateCustomer().
     */
    public boolean linkCustomerTelegram(String bookingRef, long chatId) {
        try {
            // 1. Find the booking request matching the reference
            List<BookingRequest> requests = db.getAllBookingRequests();
            BookingRequest targetRequest = null;

            for (BookingRequest req : requests) {
                if (bookingRef.equals(req.getBookingRef())) {
                    targetRequest = req;
                    break;
                }
            }

            if (targetRequest == null) {
                System.out.println("No booking request found for reference: " + bookingRef);
                return false;
            }

            Customer customer = null;

            // 2. Check if the customer is already linked to the request
            if (targetRequest.getCustomerId() != null) {
                customer = db.getCustomerById(targetRequest.getCustomerId());
            }

            if (customer != null) {
                // Update their existing telegram chat ID using your team's method
                db.updateTelegramChatId(customer.getCustomerId(), chatId);
            } else {
                // Otherwise, create a new customer profile using your team's model and saveCustomer()
                Customer newCustomer = new Customer();
                newCustomer.setFullName(targetRequest.getGuestName() != null ? targetRequest.getGuestName() : "Unknown Guest");
                newCustomer.setPhone(targetRequest.getGuestPhone());
                newCustomer.setTelegramChatId(chatId);
                newCustomer.setTimesDenied(0);

                int newCustomerId = db.saveCustomer(newCustomer);

                // Link customer back to the booking request
                targetRequest.setCustomerId(newCustomerId);
                db.updateBookingRequest(targetRequest);
            }

            System.out.println("Successfully linked Telegram Chat ID " + chatId + " to booking reference " + bookingRef);
            return true;

        } catch (SQLException e) {
            System.err.println("Database error linking Telegram chat ID: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves a notification message record into the database MESSAGE table
     * using your team's logMessage() method.
     */
    public void saveMessageRecord(long chatId, String content, String status) {
        try {
            // Find customer ID by matching their telegram chat ID
            List<Customer> customers = db.getAllCustomers();
            int customerId = -1;

            for (Customer c : customers) {
                if (c.getTelegramChatId() != null && c.getTelegramChatId() == chatId) {
                    customerId = c.getCustomerId();
                    break;
                }
            }

            if (customerId == -1) {
                System.out.println("Cannot log message: Customer with chat ID " + chatId + " not found.");
                return;
            }

            // Use your team's Message model and logMessage method
            Message message = new Message(customerId, content, status);
            db.logMessage(message);

            System.out.println("Message record logged successfully in database.");

        } catch (SQLException e) {
            System.err.println("Database error saving message log: " + e.getMessage());
        }
    }
}