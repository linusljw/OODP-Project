package controller.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import controller.EntityController;
import model.reservation.Reservation;
import model.reservation.ReservationStatus;
import model.room.BedType;
import model.room.Room;
import model.room.RoomStatus;
import model.room.RoomType;
import persistence.Entity;
import persistence.EntityIterator;
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
	public final static String KEY_STATUS = "room status";
	private EntityController<RoomType> rtController = null;
	
	/**
	 * RoomController constructor
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 * @param rtController - The RoomType EntityController to allow RoomController to interact with for information sharing.
	 */
	public RoomController(Persistence persistence, EntityController<RoomType> rtController) {
		super(persistence);
		this.rtController = rtController;
	}
	
	@Override
	protected String getEntityName() {
		return "Room";
	}
	
	@Override
	public List<String> getOptions() {
		return Arrays.asList(
					"Create " + this.getEntityName().toLowerCase(),
					"Retrieve/Search " + this.getEntityName().toLowerCase(),
					"Update " + this.getEntityName().toLowerCase(),
					"Update " + this.getEntityName().toLowerCase() + " status",
					"Delete " + this.getEntityName().toLowerCase()
				);
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			create(view);
			break;
		case 1:
			retrieve(view);
			break;
		case 2:
			update(view);
			break;
		case 3:
			updateStatus(view);
			break;
		case 4:
			delete(view);
			break;
		}
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
					
					if (Pattern.matches("0[2-7]0[1-8]", inputMap.get(KEY_NUMBER))) {
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
						view.message("Room Number must be <2 Level Digits><2 Running Digits>, <02-07><01-08>, e.g. 0201 or 0702\n");
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
	protected boolean retrieve(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();

		List entityList = new ArrayList();
		Iterable<Room> rooms = persistence.search(null, Room.class, false);
		for(Entity entity: rooms)
			entityList.add(entity);
		
		view.display(entityList);
		
		return entityList.size() > 0;
	}

	/**
	 * Prompts the user to enter relevant information required and updates a Room instance.
	 */
	@Override
	protected void update(View view) throws Exception {
		Room room = select(view);
		
		boolean valid = false;
		if (room != null) {
			Persistence persistence = this.getPersistenceImpl();
			
			Map<String, String> inputMap = new LinkedHashMap<String, String>();
			inputMap.put(KEY_VIEW, null);
			inputMap.put(KEY_WIFI, null);
			inputMap.put(KEY_SMOKING, null);
			do {
				view.input(inputMap);
				
				try {
					char wifi = inputMap.get(KEY_WIFI).charAt(0);
					try {
						char smoking = inputMap.get(KEY_SMOKING).charAt(0);
						room.setView(inputMap.get(KEY_VIEW));
						if (wifi == 'Y' || wifi == 'T')
							room.setWifi(true);
						else if (wifi == 'F' || wifi == 'N')
							room.setWifi(false);
						if (smoking == 'Y' || smoking == 'T')
							room.setSmoking(true);
						else if (smoking == 'F' || smoking == 'N')
							room.setSmoking(false);
						view.message("Enter bed type");
						room.setBedType(view.options(Arrays.asList(BedType.values())));
						room.setType(rtController.select(view));
						
						if (persistence.update(room, Room.class)) {
							valid = true;
							view.message("Room successfully updated!");
						}
					} catch (IndexOutOfBoundsException e) {
						view.error(Arrays.asList(KEY_SMOKING));
					}
				} catch (IndexOutOfBoundsException e) {
					view.error(Arrays.asList(KEY_WIFI));
				}
			} while (!valid);
		}
	}

	/**
	 * Prompts the user to enter relevant information required and deletes a Room instance.
	 */
	@Override
	protected void delete(View view) throws Exception {
		Room room = select(view);
		
		Persistence persistence = this.getPersistenceImpl();
		if(room != null && persistence.delete(room, Room.class))
			view.message("Room deleted successfully!");
	}

	/**
	 * Prompts the user to select a Room.
	 */
	@Override
	public Room select(View view) throws Exception {
		Room room = null;
		
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NUMBER, null);

		Persistence persistence = this.getPersistenceImpl();
		do {
			view.input(inputMap);
			
			EntityIterator<Room> rooms = (EntityIterator<Room>) persistence.search(new Predicate<Room>() {
				@Override
				public boolean test(Room item) {
					return item.getNumber().equals(inputMap.get(KEY_NUMBER));
				}
			}, Room.class, false).iterator();
			if(rooms.hasNext())
				room = rooms.next();
			else
				view.message("Room does not exist. Please try again.\n");
			rooms.close();
		} while(room == null && !view.bailout());
		
		return room;
	}
	
	/**
	 * Prompts the user to enter relevant information to update Room status.
	 */
	public void updateStatus(View view) throws Exception {
		Room room = select(view);
		
		if (room != null) {
			boolean ableMaintain = true;
			
			for(Reservation reservation:room.getReservationList())
				if (reservation.getStatus() == ReservationStatus.CheckedIn ||
					reservation.getStatus() == ReservationStatus.Confirmed ||
					reservation.getStatus() == ReservationStatus.Waitlist)
						ableMaintain = false;
			Persistence persistence = this.getPersistenceImpl();
			
			RoomStatus status = null;
			
			if (ableMaintain) {
				if (room.getStatus() == RoomStatus.Maintenance)
					status = view.options(Arrays.asList(RoomStatus.Vacant, RoomStatus.Exit));
				else
					status = view.options(Arrays.asList(RoomStatus.Maintenance, RoomStatus.Exit));
				
				if (status != RoomStatus.Exit) {
					room.setStatus(status);
					if (persistence.update(room, Room.class)) {
						view.message("Room status has been updated successfully!");
					}
				}
			} else {
				view.message("Room is unable to maintain as it is currently Occupied/Reserved.");
			}
		}
	}
}
