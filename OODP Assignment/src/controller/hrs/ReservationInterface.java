package controller.hrs;

import model.reservation.Reservation;
import view.View;

/**
 * ReservationInterface is an interface representing a generic contract for all implementing classes
 * to expose method(s) to handle reservation logic.
 * @author YingHao
 */
public interface ReservationInterface {
	
	/**
	 * Prompts the user to enter relevant information to cancel a reservation.
	 * @param view - A view interface that provides input/output.
	 * @return A flag indicating if the cancellation was successful.
	 */
	public boolean cancelReservation(View view) throws Exception;
	
	/**
	 * Prompts the user to enter relevant information to search for a reservation.
	 * @param view - A view interface that provides input/output.
	 */
	public void searchReservation(View view) throws Exception;
	
	/**
	 * Reserves a room for the specified Reservation.
	 * @param reservation - The reservation used to reserve a room.
	 * @return A flag indicating if a room was reserved.
	 */
	public boolean reserveRoomForReservation(Reservation reservation) throws Exception;

	/**
	 * Prompts the user to enter relevant information to check for room availability and
	 * allows user to make a reservation if desired.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - The {@link Reservation} instance that should be populated with user chosen data.
	 */
	public void checkRoomAvailability(View view, Reservation reservation) throws Exception;

	/**
	 * Prompts the user relevant information to make a reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - The reservation that should be made.
	 * @return A flag indicating if the reservation has been made.
	 * @see #checkRoomAvailability(View, Reservation)
	 */
	public boolean makeReservation(View view, Reservation reservation) throws Exception;
}
