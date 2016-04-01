package controller.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import model.RoomType;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
import persistence.file.text.EntityIterator;
import view.View;

/**
 * A controller responsible for managing RoomType entity.
 * @author Yijie
 */
public class RoomTypeController extends EntityController<RoomType> {
	private final static String KEY_NAME = "room type name";
	private final static String KEY_PRICE = "room price";

	/**
	 * RoomTypeController constructor
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 */
	public RoomTypeController(Persistence persistence) {
		super(persistence);
	}
	
	@Override
	protected String getEntityName() {
		return "Room Type";
	}
	
	/**
	 * Prompts the user to enter relevant information required and creates a new RoomType instance.
	 */
	@Override
	protected void create(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);
		inputMap.put(KEY_PRICE, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			try {
				RoomType roomType =  new RoomType(inputMap.get(KEY_NAME));
				roomType.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
				
				long count = persistence.getCount(new Predicate<RoomType>() {
					@Override
					public boolean test(RoomType item) {
						return item.getName().toUpperCase().equals(inputMap.get(KEY_NAME).toUpperCase());
					}
				}, RoomType.class, false);
	
				if (count > 0) {
					view.message("Room Type already exists, please update it instead.");
					valid = true;
				}
				else {
					persistence.create(roomType, RoomType.class);
					
					view.message("Room Type created successfully.");
					valid = true;
				}
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_PRICE));
			}
			
			
		} while(!valid && !view.bailout());
	}
	
	/**
	 * Retrieves and displays all RoomType instances.
	 */
	@Override
	protected void retrieve(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		List entityList = new ArrayList();
		Iterable<RoomType> roomTypes = persistence.search(null, RoomType.class, false);
		for(Entity entity: roomTypes)
			entityList.add(entity);
		
		view.display(entityList);
	}
	
	/**
	 * Prompts the user to enter relevant information required and updates a RoomType instance.
	 */
	@Override
	protected void update(View view) throws Exception {
		RoomType roomType = select(view);
		
		boolean valid = false;
		if(roomType != null) {
			Persistence persistence = this.getPersistenceImpl();
			
			Map<String, String> inputMap = new LinkedHashMap<String, String>();
			inputMap.put(KEY_PRICE, null);
			do {
				view.input(inputMap);
				
				try {
					roomType.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
					
					if(persistence.update(roomType, RoomType.class)) {
						valid = true;
						view.message("Room Type successfully updated!");
					}
				} catch(NumberFormatException e) {
					view.error(Arrays.asList(KEY_PRICE));
				}
			} while(!valid);
		}
	}

	/**
	 * Prompts the user to enter relevant information required and deletes a RoomType instance.
	 */
	@Override
	protected void delete(View view) throws Exception {
		RoomType roomType = select(view);
		
		Persistence persistence = this.getPersistenceImpl();
		if(roomType != null && persistence.delete(roomType, RoomType.class))
			view.message("Room Type deleted successfully!");
	}

	/**
	 * Prompts the user to select a RoomType.
	 */
	@Override
	public RoomType select(View view) throws Exception {
		RoomType roomType = null;
		
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);

		Persistence persistence = this.getPersistenceImpl();
		do {
			view.input(inputMap);
			
			EntityIterator<RoomType> roomTypes = (EntityIterator<RoomType>) persistence.search(new Predicate<RoomType>() {
				@Override
				public boolean test(RoomType item) {
					return item.getName().toUpperCase().equals(inputMap.get(KEY_NAME).toUpperCase());
				}
			}, RoomType.class, false).iterator();
			if(roomTypes.hasNext())
				roomType = roomTypes.next();
			else
				view.message("Room Type does not exist. Please try again.\n");
			roomTypes.close();
		} while(roomType == null && !view.bailout());
		
		return roomType;
	}
}
