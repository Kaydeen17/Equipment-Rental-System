package com.java.domain;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "assets")
public class Asset implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assetId")
    private int assetId;

    @Column(nullable = true, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private float pricePerDay;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @Column(name = "bookingId", insertable = false, updatable = false)
    private Integer bookingId;

    public enum Status {
        AVAILABLE,
        BOOKED,
        UNDER_MAINTENANCE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.AVAILABLE;

    // Default constructor
    public Asset() {
        this.status = Status.AVAILABLE;
    }

    // Constructor for creating an asset without a booking
    public Asset(String category, String name, float pricePerDay, String serialNumber) {
        this.serialNumber = serialNumber;
        this.category = category;
        this.name = name;
        this.pricePerDay = pricePerDay;
        this.status = Status.AVAILABLE;
    }

    // Constructor for creating an asset with a booking
    public Asset(Booking booking, String category, String name, float pricePerDay, String serialNumber, Status status) {
        this.booking = booking;
        this.bookingId = (booking != null) ? booking.getBookingId() : null;  // Ensure bookingId is set correctly
        this.serialNumber = serialNumber;
        this.name = name;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    // Getters
    public int getAssetId() {
        return assetId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(float pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        this.bookingId = (booking != null) ? booking.getBookingId() : null;  // Update bookingId when booking is set
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String toString() {
        return "\nAsset \nId: " + assetId +
                "\nSerial Number: " + serialNumber +
                "\nName: " + name +
                "\nCategory: " + category +
                "\nPrice Per Day: $" + pricePerDay +
                "\nBooking Id: " + (bookingId != null ? bookingId : "None") +
                "\nStatus: " + status + "\n";
    }
}
