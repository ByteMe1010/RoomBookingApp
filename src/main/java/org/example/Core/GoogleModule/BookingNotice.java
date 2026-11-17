package org.example.Core.GoogleModule;

import java.time.LocalDate;

/** Fields supported by the simulated notification format. */
public record BookingNotice(String reference, String guestName, LocalDate checkIn,
                            LocalDate checkOut, String room, Integer rooms, String phone) { }
