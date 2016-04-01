package controller.hrs;

import java.util.Arrays;
import java.util.List;

import controller.PersistenceController;
import persistence.Persistence;
import view.View;

/**
 * ReservationController is a controller that performs reservation operations.
 * @author YingHao
 */
public class ReservationController extends PersistenceController {
	/**
	 * ReservationController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 */
	public ReservationController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("Make a reservation");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			break;
		}
	}
	
	/**
	 * Prompts the user to enter relevant information to make a reservation.
	 * @param view - A view interface that provides input/output.
	 */
	protected void makeReservation(View view) {
		
	}

}
