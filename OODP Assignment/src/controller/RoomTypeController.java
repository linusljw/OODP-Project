package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import persistence.Persistence;
import view.View;

/**
 * A controller responsible for managing RoomType entity.
 * @author Yijie
 */
public class RoomTypeController extends PersistenceController {
	private final static String KEY_NAME = "room type name";
	private final static String KEY_PRICE = "room price";
	private final static List<String> OPTIONS = Arrays.asList("Add Room Type", "Update Room Price", "Delete Room Type");

	public RoomTypeController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		return new ArrayList<String>(OPTIONS);
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			//create(view);
			break;
		case 1:
			//update(view);
			break;
		case 2:
			//delete(view);
			break;
		}
	}

}
