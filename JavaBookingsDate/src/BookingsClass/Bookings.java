package BookingsClass;

public class Bookings {
	
	private int booking_id;
	private int qty;
	private String status;
	private Date booking_date;
	private Date return_date;
	
	//Default Constructor
	public Bookings() {
		super();
		this.booking_id = 0000;
		this.qty = 0000;
		this.status = " ";
		this.booking_date = new Date (1, 1, 1100);
		this.return_date = new Date (1, 1, 1100);
	}

	//Primary Constructor
	public Bookings(int booking_id, int qty, String status, Date booking_date, Date return_date) {
		super();
		this.booking_id = booking_id;
		this.qty = qty;
		this.status = status;
		this.booking_date = booking_date;
		this.return_date = return_date;
	}

	public int getBooking_id() {
		return booking_id;
	}

	public void setBooking_id(int booking_id) {
		this.booking_id = booking_id;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBooking_date() {
		return booking_date;
	}

	public void setBooking_date(Date booking_date) {
		this.booking_date = booking_date;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}
	

	@Override
	public String toString() {
		return "Booking: [Booking ID:" + booking_id + ", Quantity:" + qty + ", Status:" + status + ", "
				+ "Booking Date:" + booking_date + ", Return Date:" + return_date + "]";
	}
}
