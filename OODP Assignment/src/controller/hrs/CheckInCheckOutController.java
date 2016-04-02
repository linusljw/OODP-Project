package controller.hrs;

import java.util.Arrays;
import java.util.List;

import controller.PersistenceController;
import persistence.Persistence;
import view.View;

/**
 * CheckInCheckOutController is a controller that performs check-in check-out operations.
 * @author YingHao
 */
public class CheckInCheckOutController extends PersistenceController {
	private final ReservationInterface rInterface;

	/**
	 * CheckInCheckOutController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 * @param rInterface - The ReservationInterface to allow CheckInCheckOutController to interact with for handling reservations.
	 */
	public CheckInCheckOutController(Persistence persistence, ReservationInterface rInterface) {
		super(persistence);
		this.rInterface = rInterface;
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("Check in", "Check out");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		// TODO Auto-generated method stub
	}

}
