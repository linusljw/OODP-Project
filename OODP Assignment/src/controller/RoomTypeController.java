package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
public class RoomTypeController extends PersistenceController implements SelectableController<RoomType> {
	private final static String KEY_NAME = "room type name";
	private final static String KEY_PRICE = "room price";
	private final static String KEY_NAME_UPDATE = "name of room type to update";
	private final static String KEY_NAME_DELETE = "name of room type to delete";
	private final static List<String> OPTIONS = Arrays.asList("Add Room Type", "Update Room Price", "Delete Room Type");

	/**
	 * RoomTypeController constructor
	 * @param persistence
	 */
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
			create(view);
			break;
		case 1:
			update(view);
			break;
		case 2:
			delete(view);
			break;
		}
	}
	
	@Override
	public RoomType select(View view) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Creates a new RoomType.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void create(View view) throws Exception {
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
	 * Updates a RoomType
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void update(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME_UPDATE, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			try {
				EntityIterator<RoomType> roomTypes = (EntityIterator<RoomType>) persistence.search(new Predicate<RoomType>() {
					@Override
					public boolean test(RoomType item) {
						return item.getName().toUpperCase().equals(inputMap.get(KEY_NAME_UPDATE).toUpperCase());
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
	
	public void delete(View view) {
		
	}
}
