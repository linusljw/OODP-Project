package controller.management;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import controller.EntityController;
import model.Room;
import model.RoomType;
import persistence.Persistence;
import view.View;

/**
 * A controller responsible for managing Room entity. 
 * @author Yijie
 */
public class RoomController extends EntityController<Room> {
	private final static String KEY_NUMBER = "room number";
	private final static String KEY_VIEW = "room view";
	private final static String KEY_WIFI = "room wifi";
	private final static String KEY_SMOKING = "room smoking";
	private final static String KEY_ROOM_TYPE = "room type";
	private final static String KEY_BED_TYPE = "room bed type";
	private EntityController<RoomType> rtController = null;
	
	public RoomController(Persistence persistence, EntityController<RoomType> rtController) {
		super(persistence);
		this.rtController = rtController;
	}
	
	@Override
	protected String getEntityName() {
		return "Room";
	}
	
	/**
	 * Prompts the user to enter relevant information required and creates a new Room instance.
	 */
	@Override
	protected void create(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NUMBER, null);
		inputMap.put(KEY_VIEW, null);
		inputMap.put(KEY_WIFI, null);
		inputMap.put(KEY_SMOKING, null);
		inputMap.put(KEY_ROOM_TYPE, rtController.select(view).getName());
		
		
		do {
			view.input(inputMap);
			
			try {
				char wifi = Character.toUpperCase(inputMap.get(KEY_WIFI).charAt(0));
				try {
					char smoking = Character.toUpperCase(inputMap.get(KEY_SMOKING).charAt(0));
					Room room = new Room(inputMap.get(KEY_NUMBER));
					room.setView(inputMap.get(KEY_VIEW));
					if (wifi == 'Y' || wifi == 'T')
						room.setWifi(true);
					else if (wifi == 'F' || wifi == 'N')
						room.setWifi(false);
					if (smoking == 'Y' || smoking == 'T')
						room.setSmoking(true);
					else if (smoking == 'F' || smoking == 'N')
						room.setSmoking(false);
				} catch(IndexOutOfBoundsException e) {
					view.error(Arrays.asList(KEY_SMOKING));
				}
			} catch (IndexOutOfBoundsException e) {
				view.error(Arrays.asList(KEY_WIFI));
			}
		} while (!view.bailout());
	}
	
	@Override
	protected void retrieve(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void delete(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Room select(View view) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
