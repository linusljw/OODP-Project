package controller.hrs;

import java.util.List;

import controller.PersistenceController;
import persistence.Persistence;
import view.View;

/**
 * ReservationController is a controller that performs reservation operations.
 * @author YingHao
 */
public class ReservationController extends PersistenceController {

	public ReservationController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		// TODO Auto-generated method stub
		
	}

}