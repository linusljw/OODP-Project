package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import model.Guest;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
import view.View;

/**
 * A controller responsible for managing Guest entity.
 * @author YingHao
 */
public class GuestController extends PersistenceController {
	public final static String KEY_NAME = "guest name";
	public final static String KEY_IDENTIFICATION = "identification number";
	public final static String KEY_NATIONALITY = "nationality";
	public final static String KEY_GENDER = "gender";
	public final static String KEY_CONTACT_NUMBER = "contact number";
	public final static String KEY_EMAIL_ADDRESS = "email address";
	public final static String KEY_SEARCH = "name of the guest to search for";
	public final static String KEY_ID_UPDATE = "ID of guest to update";
	private final static List<String> OPTIONS = Arrays.asList("Add new guest", "Search guest", "Update guest information");

	public GuestController(Persistence persistence) {
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
	 * Creates a new guest profile.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	public void create(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_NAME, null);
		inputMap.put(KEY_IDENTIFICATION, null);
		inputMap.put(KEY_NATIONALITY, null);
		inputMap.put(KEY_GENDER, null);
		inputMap.put(KEY_CONTACT_NUMBER, null);
		inputMap.put(KEY_EMAIL_ADDRESS, null);
		
		boolean valid = false;
		do {
			view.input(inputMap);
			
			try {
				Guest guest = new Guest(inputMap.get(KEY_IDENTIFICATION), inputMap.get(KEY_NATIONALITY));
				guest.setName(inputMap.get(KEY_NAME));
				guest.setGender(Character.toUpperCase(inputMap.get(KEY_GENDER).charAt(0)));
				guest.setContactNo(inputMap.get(KEY_CONTACT_NUMBER));
				guest.setEmailAddress(inputMap.get(KEY_EMAIL_ADDRESS));
				
				// Validate all fields
				List<String> invalids = new ArrayList<String>();
				if(guest.getName().length() == 0)
					invalids.add(KEY_NAME);
				if(guest.getIdentification().length() == 0)
					invalids.add(KEY_IDENTIFICATION);
				if(guest.getNationality().length() == 0)
					invalids.add(KEY_NATIONALITY);
				if(guest.getGender() != 'M' && guest.getGender() != 'F')
					invalids.add(KEY_GENDER);
				if(!Pattern.matches("\\+?(\\d|-|\\s|\\(|\\))+", guest.getContactNo()))
					invalids.add(KEY_CONTACT_NUMBER);
				if(!Pattern.matches("^.+@.+\\..+$", guest.getEmailAddress()))
					invalids.add(KEY_EMAIL_ADDRESS);
				
				if(invalids.size() == 0) {
					// Ensure no duplicate guest record, with same identification and nationality
					Persistence persistence = this.getPersistenceImpl();
					
					long count = persistence.getCount(new Predicate<Guest>() {

									@Override
									public boolean test(Guest item) {
										return item.getIdentification().equals(guest.getIdentification()) && 
												item.getNationality().equals(guest.getNationality());
									}
						
								}, Guest.class, false);
					if(count > 0) {
						view.message("Guest already exists, please update guest information instead");
						valid = true;
					}
					else {
						// Update Address information
						AddressValidator.update(view, guest.getAddress());
						
						persistence.create(guest, Guest.class);
						
						view.message("Guest profile successfully added!");
						valid = true;
					}
				}
				else {
					view.error(invalids);
				}
			} catch(IndexOutOfBoundsException e) {
				view.error(Arrays.asList(KEY_GENDER));
			}
		} while(!valid && !view.bailout());
	}
	
	/**
	 * Displays search for a guest by name.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	public void search(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_SEARCH, null);
		
		view.input(inputMap);
		
		Persistence persistence = this.getPersistenceImpl();
		
		List entityList = new ArrayList();
		// Provide a predicate to search for matching items
		Iterable<Guest> guests = persistence.search(new Predicate<Guest>() {

										@Override
										public boolean test(Guest item) {
											return item.getName().contains(inputMap.get(KEY_SEARCH));
										}
								
									}, Guest.class, false);
		
		// Loop through results and add it into the list
		for(Entity entity: guests)
			entityList.add(entity);
		
		// Display guests
		view.display(entityList);
	}
	
	/**
	 * Updates guest profile.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	public void update(View view) throws Exception {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ID_UPDATE, null);
		
		boolean valid = false;
		do {
			// Retrieve user input for ID to update
			view.input(inputMap);
			
			try {
				Persistence persistence = this.getPersistenceImpl();
				
				// Retrieve Guest entity based on ID
				Guest guest = persistence.retrieveByID(Long.parseLong(inputMap.get(KEY_ID_UPDATE)), Guest.class);
				if(guest == null)
					view.error(Arrays.asList(KEY_ID_UPDATE));
				else {
					do {
						// Retrieve user input for updateable fields
						inputMap.clear();
						inputMap.put(KEY_NAME, null);
						inputMap.put(KEY_GENDER, null);
						inputMap.put(KEY_CONTACT_NUMBER, null);
						inputMap.put(KEY_EMAIL_ADDRESS, null);
						
						view.input(inputMap);
						
						try {
							guest.setName(inputMap.get(KEY_NAME));
							guest.setGender(Character.toUpperCase(inputMap.get(KEY_GENDER).charAt(0)));
							guest.setContactNo(inputMap.get(KEY_CONTACT_NUMBER));
							guest.setEmailAddress(inputMap.get(KEY_EMAIL_ADDRESS));
							
							// Validate all fields
							List<String> invalids = new ArrayList<String>();
							if(guest.getName().length() == 0)
								invalids.add(KEY_NAME);
							if(guest.getGender() != 'M' && guest.getGender() != 'F')
								invalids.add(KEY_GENDER);
							if(!Pattern.matches("\\+?(\\d|-|\\s|\\(|\\))+", guest.getContactNo()))
								invalids.add(KEY_CONTACT_NUMBER);
							if(!Pattern.matches("^.+@.+\\..+$", guest.getEmailAddress()))
								invalids.add(KEY_EMAIL_ADDRESS);
							
							// Attempts to update entity
							if(invalids.size() == 0) {
								// Update address information
								AddressValidator.update(view, guest.getAddress());
								
								if(persistence.update(guest, Guest.class)) {
									valid = true;
									view.message("Guest profile successfully updated!");
								}
							}
							else {
								view.error(invalids);
							}
						} catch(IndexOutOfBoundsException e) {
							view.error(Arrays.asList(KEY_GENDER));
						}
					} while(!valid && !view.bailout());
				}
			} catch(NumberFormatException e) {
				view.error(Arrays.asList(KEY_ID_UPDATE));
			}
		} while(!valid && !view.bailout());
	}

}
