package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.MenuItem;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
import view.View;

/**
 * A controller responsible for managing MenuItem entity.
 * @author YingHao
 */
public class MenuItemController extends PersistenceController {
	public final static String KEY_NAME = "item name";
	public final static String KEY_PRICE = "price";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_ID_UPDATE = "ID of menu item to update";
	public final static String KEY_ID_DELETE = "ID of menu item to delete";
	private final static List<String> OPTIONS = Arrays.asList("Create new menu item", "Retrieve menu items", "Update menu items", "Delete menu items");
	
	/**
	 * MenuItemController constructor.
	 * @param persistence
	 */
	public MenuItemController(Persistence persistence) {
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
			retrieve(view);
			break;
		case 2:
			update(view);
			break;
		case 3:
			delete(view);
			break;
		}
	}
	
	/**
	 * Creates a new MenuItem.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	public void create(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);
		inputMap.put(KEY_PRICE, null);
		inputMap.put(KEY_DESCRIPTION, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			// Search through the list ensuring there are no duplicate entries
			long count = persistence.getCount(new Predicate<MenuItem>() {

							@Override
							public boolean test(MenuItem item) {
								return item.getName().equals(inputMap.get(KEY_NAME));
							}
				
						}, MenuItem.class, false);
			
			if(count > 0) {
				view.message("The specified item name already exists, please update it instead");
				valid = true;
			}
			else {
				// Validate all fields, then create a new menu item
				try {
					MenuItem item = new MenuItem(inputMap.get(KEY_NAME));
					item.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
					item.setDescription(inputMap.get(KEY_DESCRIPTION));
					
					persistence.create(item, MenuItem.class);
					
					view.message("Menu item created successfully!");
					valid = true;
				} catch(NumberFormatException e) {
					view.error(Arrays.asList(KEY_PRICE));
				}
			}
		} while(!valid && !view.bailout());
	}
	
	/**
	 * Retrieves all menu items.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void retrieve(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		// Store the menu items in an array list
		List entityList = new ArrayList();
		Iterable<MenuItem> items = persistence.search(null, MenuItem.class, false);
		for(Entity entity: items)
			entityList.add(entity);
		
		// Call view display method for list of items
		view.display(entityList);
	}
	
	/**
	 * Updates a menu item.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void update(View view) throws Exception {
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ID_UPDATE, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			// Retrieve menu item
			try {
				MenuItem item = persistence.retrieveByID(Long.parseLong(inputMap.get(KEY_ID_UPDATE)), MenuItem.class);
				
				if(item == null)
					view.error(Arrays.asList(KEY_ID_UPDATE));
				else {
					inputMap.clear();
					inputMap.put(KEY_PRICE, null);
					inputMap.put(KEY_DESCRIPTION, null);
					
					do {
						view.input(inputMap);
					
						// Validate all fields, then update menu item.
						try {
							item.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
							item.setDescription(inputMap.get(KEY_DESCRIPTION));
							
							if(persistence.update(item, MenuItem.class)) {
								view.message("Menu item updated successfully!");
								valid = true;
							}
						} catch(NumberFormatException e) {
							view.error(Arrays.asList(KEY_PRICE));
						}
					} while(!valid && !view.bailout());
				}
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_ID_UPDATE));
			}
		} while(!valid && !view.bailout());
	}
	
	/**
	 * Deletes a menu item.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception
	 */
	public void delete(View view) throws Exception {
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ID_DELETE, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			// Retrieve menu item and delete.
			try {
				MenuItem item = persistence.retrieveByID(Long.parseLong(inputMap.get(KEY_ID_DELETE)), MenuItem.class);
				if(item == null)
					view.error(Arrays.asList(KEY_ID_DELETE));
				else if(persistence.delete(item, MenuItem.class)) {
					view.message("Menu item deleted successfully!");
					valid = true;
				}
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_ID_DELETE));
			}
		} while(!valid && !view.bailout());
	}

}
