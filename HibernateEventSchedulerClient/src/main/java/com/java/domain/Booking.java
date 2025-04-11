package com.java.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
@Entity
@Table(name = "booking")
public class Booking implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "bookingId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookingId;
	
	@Column(nullable = false)
	private String clientName;

	@Column(nullable = false)
	private String clientContact;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date bookDate;

	@PrePersist
	protected void onCreate() {
	    if (this.bookDate == null) {
	        this.bookDate = new Date();
	    }
	    this.dayCount = calculateDayCount();
	}

	@Column(nullable = false)
	private Date returnDate;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Asset> assetList;

	private Integer invoiceId, quotationId;
	private boolean late = false;

	public enum Status {
        ONGOING,
        UNCOLLECTED,
        CLOSED
    }

	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private Status status = Status.ONGOING;

	@Column(name = "dayCount")
	private long dayCount;

	public Booking() {}

	public Booking(Integer bookingId, String clientName, String clientContact, String userId, Date bookDate,
			Date returnDate, List<Asset> assetList, Integer invoiceId, boolean isLate, Status status) {
		this.bookingId = bookingId;
		this.clientName = clientName;
		this.clientContact = clientContact;
		this.userId = userId;
		this.bookDate = bookDate;
		this.returnDate = returnDate;
		this.assetList = assetList;
		this.invoiceId = invoiceId;
		this.quotationId = null;
		this.late = isLate;
		this.status = status;
		calculateDayCount();
	}

	public Booking(Integer bookingId, String clientName, String clientContact, String userId, Date bookDate,
			Date returnDate, List<Asset> assetList, Integer invoiceId, Integer quotationId, boolean isLate, Status status) {
		this.bookingId = bookingId;
		this.clientName = clientName;
		this.clientContact = clientContact;
		this.userId = userId;
		this.bookDate = bookDate;
		this.returnDate = returnDate;
		this.assetList = assetList;
		this.invoiceId = invoiceId;
		this.quotationId = quotationId;
		this.late = isLate;
		this.status = status;
		calculateDayCount();
	}

	public long calculateDayCount() {
	    if (this.bookDate != null && this.returnDate != null) {
	        long diff = this.returnDate.getTime() - this.bookDate.getTime();
	        return diff / (1000L * 60 * 60 * 24);
	    }
	    return 0L;
	}

	// Getters and Setters
	public Integer getBookingId() { return bookingId; }
	public void setBookingId(int bookingId) { this.bookingId = bookingId; }

	
	public String getClientName() { 
		return clientName; 
		}
	public void setClientName(String clientName) {
		this.clientName = clientName; 
		}

	public String getClientContact() { return clientContact; }
	public void setClientContact(String clientContact) { this.clientContact = clientContact; }

	public String getUserId() { return userId; }
	public void setUserId(String userId) { this.userId = userId; }

	public Date getBookDate() { return bookDate; }
	public void setBookDate(Date bookDate) { this.bookDate = bookDate; }

	public Date getReturnDate() { return returnDate; }
	public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

	public List<Asset> getAssetList() { return assetList; }
	public void setAssetList(List<Asset> assetList) { this.assetList = assetList; }

	public Integer getInvoiceId() { return invoiceId; }
	public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

	public boolean isLate() { return late; }
	public void setLate(boolean isLate) { late = isLate; }

	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }

	public long getDayCount() { return dayCount; }

	public Integer getQuotationId() { return quotationId; }
	public void setQuotationId(Integer quotationId) { this.quotationId = quotationId; }

	@Override
	public String toString() {
		return "\nBooking \nId: " + bookingId  + "\nClient Name: " + clientName + "\nContact: " + clientContact +
				"\nUser Id: " + userId + "\nDate Booked: " + bookDate + "\nReturn Date: " + returnDate + "\nAsset List: " + assetList +
				"\nInvoice Id: " + invoiceId + "\nReturned Late: " + late + "\nStatus=" + status + "]";
	}
}
