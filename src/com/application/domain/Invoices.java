package com.application.domain;

import java.util.Date;

public class Invoices {
	private int invoiceId;
	private int bookingId;
	private double amount;
	private int clientId;
	private Date paymentDate;
	
	public Invoices() {
		this.invoiceId = 0;
		this.bookingId = 0;
		this.amount = 0.00;
		this.clientId = 0;
		this.paymentDate = new Date();
	}

	public Invoices(int invoiceId, int bookingId, double amount, int clientId, Date paymentDate) {
		super();
		this.invoiceId = invoiceId;
		this.bookingId = bookingId;
		this.amount = amount;
		this.clientId = clientId;
		this.paymentDate = paymentDate;
	}

	public int getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Override
	public String toString() {
		return "Invoices [invoiceId=" + invoiceId + ", bookingId=" + bookingId + ", amount=" + amount + ", clientId="
				+ clientId + ", paymentDate=" + paymentDate + "]";
	}
	
	
}