package controller.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import model.service.fnb.MenuItem;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
import view.View;

/**
 * A controller responsible for managing MenuItem entity.
 * @author YingHao
 */
public class MenuItemController extends EntityController<MenuItem> {
	public final static String KEY_NAME = "item name";
	public final static String KEY_PRICE = "price";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_ID = "ID of menu item";
	
	/**
	 * MenuItemController constructor.
	 * @param persistence
	 */
	public MenuItemController(Persistence persistence) {
		super(persistence);
	}
	
	@Override
	protected String getEntityName() {
		return "Menu Item";
	}
	
	/**
	 * Prompts the user to enter relevant information required and creates a new MenuItem instance.
	 */
	@Override
	protected void create(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);
		inputMap.put(KEY_PRICE, null);
		inputMap.put(KEY_DESCRIPTION, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
			// Validate all fields, then create a new menu item
			try {
				MenuItem item = new MenuItem(inputMap.get(KEY_NAME));
				item.setPrice(Double.parseDouble(inputMap.get(KEY_PRICE)));
				item.setDescription(inputMap.get(KEY_DESCRIPTION));
				
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
					persistence.create(item, MenuItem.class);
					
					view.message("Menu item created successfully!");
					valid = true;
				}
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_PRICE));
			}
		} while(!valid && !view.bailout());
	}
	
	/**
	 * Retrieves and displays all MenuItem instances.
	 */
	@Override
	protected boolean retrieve(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		// Store the menu items in an array list
		List entityList = new ArrayList();
		Iterable<MenuItem> items = persistence.search(null, MenuItem.class, false);
		for(Entity entity: items)
			entityList.add(entity);
		
		// Call view display method for list of items
		view.display(entityList);
		
		return true;
	}
	
	/**
	 * Prompts the user to enter relevant information required and updates a MenuItem instance.
	 */
	@Override
	protected void update(View view) throws Exception {
		MenuItem item = select(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_PRICE, null);
		inputMap.put(KEY_DESCRIPTION, null);
		
		if(item != null) {
			boolean valid = false;
			Persistence persistence = this.getPersistenceImpl();
	
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
	}
	
	/**
	 * Prompts the user to enter relevant information required and deletes a MenuItem instance.
	 */
	@Override
	protected void delete(View view) throws Exception {
		MenuItem item = select(view);
		
		Persistence persistence = this.getPersistenceImpl();
		if(item != null && persistence.delete(item, MenuItem.class))
			view.message("Menu item deleted successfully!");
	}
	
	/**
	 * Prompts the user to select a MenuItem.
	 */
	@Override
	public MenuItem select(View view) throws Exception {
		MenuItem item = null;
		
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ID, null);

		Persistence persistence = this.getPersistenceImpl();
		do {
			view.input(inputMap);
			
			try {
				item = persistence.retrieveByID(Long.parseLong(inputMap.get(KEY_ID)), MenuItem.class);
				if(item == null)
					view.error(Arrays.asList(KEY_ID));
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_ID));
			}
		} while(item == null && !view.bailout());
		
		return item;
	}

}
