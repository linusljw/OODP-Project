package controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import model.RoomType;
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
	 * @param persistence
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
	
	@Override
	protected void retrieve(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Prompts the user to enter relevant information required and updates a RoomType instance.
	 */
	@Override
	protected void update(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			try {
				EntityIterator<RoomType> roomTypes = (EntityIterator<RoomType>) persistence.search(new Predicate<RoomType>() {
					@Override
					public boolean test(RoomType item) {
						return item.getName().toUpperCase().equals(inputMap.get(KEY_NAME).toUpperCase());
					}
				}, RoomType.class, false).iterator();
				
				if(roomTypes.hasNext()) {
					// RoomType is present
					RoomType chosen = roomTypes.next();
					inputMap.clear();
					inputMap.put(KEY_PRICE, null);
					view.input(inputMap);;
					chosen.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
					if (persistence.update(chosen, RoomType.class)) {
						valid = true;
						view.message("Room Type sucessfully updated!");
					}
				}
				else {
					view.message("Room Type does not exist. Please try again");;
				}
				
				roomTypes.close();
			} catch (NumberFormatException e) {
				view.error(Arrays.asList(KEY_PRICE));
			}
			
		} while(!valid && !view.bailout());
	}

	/**
	 * Prompts the user to enter relevant information required and deletes a RoomType instance.
	 */
	@Override
	protected void delete(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Prompts the user to select a RoomType.
	 */
	@Override
	public RoomType select(View view) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
