package org.example.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;

import java.sql.SQLException;

public class DashboardHandler {
    public static void onLogin(NWindow window) {
        Div.of(window,"owner-full-name").setInnerText(HotelApp.shopOwner.getFullName());
        Div.of(window,"owner-username").setInnerText(HotelApp.shopOwner.getUsername());

        Button.of(window,"btn-logout").onClick(evt -> {
            HotelApp.shopOwner = null;
            Div.of(window,"login-screen").classList.remove("hidden");
            Div.of(window,"app-shell").classList.add("hidden");
        });

        try {
            Div.of(window,"topbar-pending-count").setInnerText(HotelApp.databaseModule.getPendingBookingRequests().size()+"");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Div bookingPanel = Div.of(window,"view-requests");
        Div staysPanel = Div.of(window,"view-stays");
        Div roomPanel = Div.of(window,"view-rooms");
        Div inventoryPanel = Div.of(window,"view-inventory");
        Div customerPanel = Div.of(window,"view-customers");

        Button bookingBtn =  Button.of(window,"requests");
        Button staysBtn =  Button.of(window,"stays");
        Button roomsBtn =  Button.of(window,"rooms");
        Button inventoryBtn =  Button.of(window,"inventory");
        Button customerBtn =  Button.of(window,"customers");

        bookingBtn.onClick(evt -> {
            helper_removeClass("active",bookingPanel,staysPanel,roomPanel,inventoryPanel,customerPanel,bookingBtn,staysBtn,roomsBtn,inventoryBtn,customerBtn);
            bookingPanel.classList.add("active");
            bookingBtn.classList.add("active");
        });
        staysBtn.onClick(evt -> {
            helper_removeClass("active",bookingPanel,staysPanel,roomPanel,inventoryPanel,customerPanel,bookingBtn,staysBtn,roomsBtn,inventoryBtn,customerBtn);
            staysPanel.classList.add("active");
            staysBtn.classList.add("active");
            StayHandler.updateTable(window);
        });
        roomsBtn.onClick(evt -> {
            helper_removeClass("active",bookingPanel,staysPanel,roomPanel,inventoryPanel,customerPanel,bookingBtn,staysBtn,roomsBtn,inventoryBtn,customerBtn);
            roomPanel.classList.add("active");
            roomsBtn.classList.add("active");
            RoomHandler.updateView(window);
        });
        inventoryBtn.onClick(evt -> {
            helper_removeClass("active",bookingPanel,staysPanel,roomPanel,inventoryPanel,customerPanel,bookingBtn,staysBtn,roomsBtn,inventoryBtn,customerBtn);
            inventoryPanel.classList.add("active");
            inventoryBtn.classList.add("active");
            InventoryHandler.updateView(window);
        });
        customerBtn.onClick(evt -> {
            helper_removeClass("active",bookingPanel,staysPanel,roomPanel,inventoryPanel,customerPanel,bookingBtn,staysBtn,roomsBtn,inventoryBtn,customerBtn);
            customerPanel.classList.add("active");
            customerBtn.classList.add("active");
            TelegramHandler.updateCustomersTable(window);
        });

        BookingRequestHandler.handle(window);
        StayHandler.handle(window);
        RoomHandler.handle(window);
        InventoryHandler.handle(window);
        TelegramHandler.handle(window);
    }
    private static void helper_removeClass(String className, NComponet... items) {
        for (NComponet item : items) {
            item.classList.remove(className);
        }
    }
}
