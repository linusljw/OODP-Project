package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Room;
import persistence.Persistence;
import view.View;

/**
 * A controller responsible for managing Room entity. 
 * @author yijie
 */
public class RoomController extends PersistenceController {
	private final static String KEY_NUMBER = "room number";
	private final static String KEY_VIEW = "room view";
	private final static String KEY_WIFI = "room wifi";
	private final static String KEY_SMOKING = "room smoking";
	private final static List<String> OPTIONS = Arrays.asList("Add new room", "Check room availability/details", "Update room details");
	
	public RoomController(Persistence persistence) {
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
			create(view);
			break;
		case 1:
			search(view);
			break;
		case 2:
			update(view);
			break;
		}
	}
	
	/**
	 * Creates a new Room.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void create(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NUMBER, null);
		inputMap.put(KEY_VIEW, null);
		inputMap.put(KEY_WIFI, null);
		inputMap.put(KEY_SMOKING, null);
		
		do {
			view.input(inputMap);
			
			try {
				Room room = new Room(inputMap.get(KEY_NUMBER));
				room.setView(inputMap.get(KEY_VIEW));
				room.setWifi(inputMap.get(KEY_WIFI));
				room.setSmoking(inputMap.get(KEY_SMOKING));
			}
		} while (!view.bailout());
	}
}
