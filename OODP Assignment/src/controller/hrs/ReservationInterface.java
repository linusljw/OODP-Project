package controller.hrs;

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

}
