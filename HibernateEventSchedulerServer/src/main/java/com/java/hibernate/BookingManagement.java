package com.java.hibernate;

import java.util.List;

import com.java.domain.Booking;

public interface BookingManagement {


		public abstract void exit();
		
		public abstract void createBooking(Booking booking);
		
		public abstract Booking readBooking(int BookingId);
		
		public abstract void updateBooking(Booking booking);
		
		public abstract void closeBooking(int bookingId);
		
		public abstract List<?> showAllBooking();
		
		public abstract void delete(int id);
	
		public abstract int ongoingBookings();
		
		public abstract int completedBookings();
	
}
