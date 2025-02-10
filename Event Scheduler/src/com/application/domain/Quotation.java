package com.application.domain;


import java.util.Date;

public class Quotation extends Invoices{
	private int quoationId;
	private double totalAmount;
	private String status;
	private Date quoationDate;
	
	public Quotation() {
		this.quoationId = 0;
		this.totalAmount = 0.00;
		this.status = "";
		this.quoationDate = new Date();
	}

	public Quotation(int invoiceId, int bookingId, double amount, int clientId, Date paymentDate, int quoationId,
			double totalAmount, String status, Date quoationDate) {
		super(invoiceId, bookingId, amount, clientId, paymentDate);
		this.quoationId = quoationId;
		this.totalAmount = totalAmount;
		this.status = status;
		this.quoationDate = quoationDate;
	}

	public int getQuoationId() {
		return quoationId;
	}

	public void setQuoationId(int quoationId) {
		this.quoationId = quoationId;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getQuoationDate() {
		return quoationDate;
	}

	public void setQuoationDate(Date quoationDate) {
		this.quoationDate = quoationDate;
	}

	@Override
	public String toString() {
		return "Quoation [quoationId=" + quoationId + ", totalAmount=" + totalAmount + ", status=" + status
				+ ", quoationDate=" + quoationDate + ", getInvoiceId()=" + getInvoiceId() + "]";
	}
	
	
}