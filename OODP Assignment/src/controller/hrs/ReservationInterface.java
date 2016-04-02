package controller.hrs;

import model.Guest;
import model.Reservation;
import view.View;

/**
 * ReservationInterface is an interface representing a generic contract for all implementing classes
 * to expose method(s) to handle reservation logic.
 * @author YingHao
 */
public interface ReservationInterface {
	
	/**
	 * Prompts the user to enter relevant information to make a reservation.
	 * @param view - A view interface that provides input/output.
	 * @return Reservation instance that was created.
	 */
	public Reservation makeReservation(View view) throws Exception;
	
	/**
	 * Prompts the user to enter relevant information to make a reservation for the specified guest.
	 * @param view - A view interface that provides input/output.
	 * @param guest - The guest to make the reservation for.
	 * @return A reservation instance that was made for the guest.
	 */
	public Reservation makeReservation(View view, Guest guest) throws Exception;
	
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

}
