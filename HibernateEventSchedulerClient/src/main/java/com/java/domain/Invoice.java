package com.java.domain;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceId")
    private int invoiceId;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String userId;

    @OneToOne
    @JoinColumn(name = "bookingId", nullable = false)
    private Booking booking;
    
    @Column(name = "bookingId", insertable = false, updatable = false) // Prevent manual insert/update issues
    private Integer bookingId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private float tax;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Column(nullable = false)
    private boolean isQuote;
    
    @Column(nullable = false)
	private float total;

    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date(); // Auto-set creation date
    }

    // Default constructor
    public Invoice() {}

 // Constructor without payment date 
    public Invoice(String clientName, String userId, Booking booking, String status, boolean isQuote) {
        this.clientName = clientName;
        this.userId = userId;
        this.booking = booking;
        this.status = status;
        this.isQuote = true; // Automatically set to true when no payment date
        // Automatically calculate price and tax
        calculateInvoiceTotal();
    }

    // Constructor with all fields including payment date 
    public Invoice(int invoiceId, String clientName, String userId, Booking booking, String status, 
                   Date paymentDate, boolean isQuote) {
        this.invoiceId = invoiceId;
        this.clientName = clientName;
        this.userId = userId;
        this.booking = booking;
        this.status = status;
        this.paymentDate = paymentDate;
        this.isQuote = false; 
        calculateInvoiceTotal();
    }

    
    public void calculateInvoiceTotal() {
        if (booking == null || booking.getAssetList() == null || booking.getAssetList().isEmpty()) {
            System.out.println("No assets found for this booking. Invoice total remains 0.");
            this.price = 0;
            this.tax = 0;
            this.total = 0;
            return;
        }

        long dayCount = booking.getDayCount(); // Number of rental days
        float subtotal = 0;

        for (Asset asset : booking.getAssetList()) {
            subtotal += asset.getPricePerDay() * dayCount;
        }

        float taxAmount = subtotal * 0.15f; // 15% tax
        float totalAmount = subtotal + taxAmount;

        // Save the calculated values
        this.price = subtotal;
        this.tax = taxAmount;
        this.total = totalAmount;

    }


    public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	// Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientId(String clientId) {
        this.clientName = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isQuote() {
        return isQuote;
    }

    public void setQuote(boolean isQuote) {
        this.isQuote = isQuote;
    }


    public void setCreationDate() {
        this.creationDate = new java.util.Date(); // Sets the current system date and time
    }
    
    @Override
    public String toString() {
        return "Invoice [invoiceId=" + invoiceId + ", clientName=" + clientName + ", userId=" + userId + ", booking="
                + booking + ", status=" + status + ", price=" + price + ", tax=" + tax + ", paymentDate="
                + paymentDate + ", creationDate=" + creationDate + ", isQuote=" + isQuote + "]";
    }

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}



}
