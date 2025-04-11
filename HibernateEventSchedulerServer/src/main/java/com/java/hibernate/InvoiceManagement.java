package com.java.hibernate;

import java.sql.Date;
import java.util.List;
import com.java.domain.Invoice;

public abstract class InvoiceManagement {

	public abstract void exit();
	
	public abstract Invoice readInvoice(int invoiceId);
	
	public abstract void recievePayment(int invoice);
	
	public abstract List<?> showAllInvoices();

	public abstract void deleteInvoice(int invoiceId);

	public abstract void exportInvoice(int invoiceId, String filePath);
	
	public abstract void invoiceReport(Date startDate, Date endDate, String filepath);
	
}
