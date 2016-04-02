package model;

/**
 * OrderStatus is an enumeration that specifies the possible status of a {@link Reservation}.
 * @author YingHao
 */
public enum ReservationStatus {
	
	/**
	 * The reservation is processed and a room has been reserved.
	 */
	Confirmed,
	
	/**
	 * The reservation is processed but a room is not available, this reservation will be placed in a wait list
	 * until a Guest forfeits a reservation with the same room requirements.
	 */
	Waitlist,
	
	/**
	 * The Guest with this reservation has made a check-in with the assigned room.
	 */
	CheckedIn,
	
	/**
	 * The Guest with this reservation has made a check-out with the assigned room.
	 */
	CheckedOut,
	
	/**
	 * This reservation has been cancelled.
	 */
	Cancelled,
	
	/**
	 * This reservation has expired.
	 */
	Expired

}
