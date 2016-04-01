package controller.hrs;

import java.util.Arrays;
import java.util.List;

import controller.EntityController;
import controller.PersistenceController;
import model.Guest;
import persistence.Persistence;
import view.View;

/**
 * ReservationController is a controller that performs reservation operations.
 * @author YingHao
 */
public class ReservationController extends PersistenceController {
	private EntityController<Guest> gController;
	
	/**
	 * ReservationController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 * @param gController - The Guest EntityController to allow ReservationController to interact with for information sharing.
	 */
	public ReservationController(Persistence persistence, EntityController<Guest> gController) {
		super(persistence);
		this.gController = gController;
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("Make a reservation");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			makeReservation(view);
			break;
		}
	}
	
	/**
	 * Prompts the user to enter relevant information to make a reservation.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	protected void makeReservation(View view) throws Exception {
		Guest guest = gController.select(view);
	}

}
