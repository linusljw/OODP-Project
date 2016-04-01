package controller.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import model.BedType;
import model.Room;
import model.RoomType;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
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
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NUMBER, null);
		inputMap.put(KEY_VIEW, null);
		inputMap.put(KEY_WIFI, null);
		inputMap.put(KEY_SMOKING, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			try {
				char wifi = Character.toUpperCase(inputMap.get(KEY_WIFI).charAt(0));
				try {
					char smoking = Character.toUpperCase(inputMap.get(KEY_SMOKING).charAt(0));
					int roomNumber = Integer.parseInt(inputMap.get(KEY_NUMBER));
					
					if (roomNumber > 200 && roomNumber < 208 ||
						roomNumber > 300 && roomNumber < 308 ||
						roomNumber > 400 && roomNumber < 408 ||
						roomNumber > 500 && roomNumber < 508 ||
						roomNumber > 600 && roomNumber < 608 ||
						roomNumber > 700 && roomNumber < 708) {
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
							view.message("Enter Bed Type");
							room.setBedType(view.options(Arrays.asList(BedType.values())));
							room.setType(rtController.select(view));
							
							long count = persistence.getCount(new Predicate<Room>() {
								@Override
								public boolean test(Room item) {
									return item.getNumber().equals(room.getNumber());
								}
							}, Room.class, false);
							
							if (count > 0) {
								valid = true;
								view.message("Room already exist. Please try again.");
							}
							else {
								persistence.create(room, Room.class);
								
								view.message("Room created successfully!");
								valid = true;
							}
					} else {
						view.message("Room Number must be <2 Level Digits><2 Running Digits>, <02-07><01-07>, e.g. 0201 or 0702\n");
					}
				} catch(IndexOutOfBoundsException e) {
					view.error(Arrays.asList(KEY_SMOKING));
				}
			} catch (IndexOutOfBoundsException e) {
				view.error(Arrays.asList(KEY_WIFI));
			}
		} while (!valid && !view.bailout());
	}
	
	/**
	 * Retrieves and display all Room instances.
	 */
	@Override
	protected void retrieve(View view) throws Exception {
			Persistence persistence = this.getPersistenceImpl();

			List entityList = new ArrayList();
			Iterable<Room> rooms = persistence.search(null, Room.class, false);
			for(Entity entity: rooms)
				entityList.add(entity);
			
			view.display(entityList);
	}

	/**
	 * Prompts the user to enter relevant information required and updates a Room instance.
	 */
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
