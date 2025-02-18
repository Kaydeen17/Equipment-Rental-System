package com.application.domain;


import java.util.Date;

public class Payments extends Invoices {
	private int paymentId;
	private double amount;
	private String paymentMethod;
	private Date paymentDate;
	
	
	public Payments() {
		this.paymentId = 0;
		this.amount = 0.00;
		this.paymentMethod = "";
		this.paymentDate = new Date();
	}
	public Payments(int invoiceId, int bookingId, double amount, int clientId, Date paymentDate, int paymentId,
			double amount2, String paymentMethod, Date paymentDate2, Invoices i) {
		super(invoiceId, bookingId, amount, clientId, paymentDate);
		this.paymentId = paymentId;
		amount = amount2;
		this.paymentMethod = paymentMethod;
		paymentDate = paymentDate2;
	}
	public int getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	@Override
	public String toString() {
		return "Payment [paymentId=" + paymentId + ", amount=" + amount + ", paymentMethod=" + paymentMethod
				+ ", paymentDate=" + paymentDate + "]";
	}
	
}
