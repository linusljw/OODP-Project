package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Address;
import view.View;

/**
 * AddressValidator is a auxiliary class for classes that wish to manipulate an Address
 * instance. It provides a single method {@link AddressValidator#update(View)} to update the provided Address instance. 
 * @author YingHao
 */
public class AddressValidator {
	public final static String KEY_COUNTRY = "country";
	public final static String KEY_CITY = "city";
	public final static String KEY_STATE = "state";
	public final static String KEY_STREET = "street";
	public final static String KEY_UNIT_NO = "unit number";
	public final static String KEY_POSTAL_CODE = "postal code";

	/**
	 * Updates the given address instance with user input from the view.
	 * @param controller - The controller that requested for this method to be called.
	 * @param view - A view interface that provides input/output.
	 * @param address - The address instance to be updated.
	 * @return A list of fields that are not valid.
	 */
	public static void update(View view, Address address) {
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_COUNTRY, null);
		inputMap.put(KEY_CITY, null);
		inputMap.put(KEY_STATE, null);
		inputMap.put(KEY_STREET, null);
		inputMap.put(KEY_UNIT_NO, null);
		inputMap.put(KEY_POSTAL_CODE, null);
		
		view.message("---- Address Information ----");
		
		boolean valid = false;
		do {
			view.input(inputMap);
			
			address.setCountry(inputMap.get(KEY_COUNTRY));
			address.setCity(inputMap.get(KEY_CITY));
			address.setState(inputMap.get(KEY_STATE));
			address.setStreet(inputMap.get(KEY_STREET));
			address.setUnitNumber(inputMap.get(KEY_UNIT_NO));
			address.setPostalCode(inputMap.get(KEY_POSTAL_CODE));
			
			// Validate fields
			List<String> invalids = new ArrayList<String>();
			if(address.getCountry().length() == 0)
				invalids.add(KEY_COUNTRY);
			if(address.getCity().length() == 0)
				invalids.add(KEY_CITY);
			if(address.getState().length() == 0)
				invalids.add(KEY_STATE);
			if(address.getStreet().length() == 0)
				invalids.add(KEY_STREET);
			if(address.getUnitNumber().length() == 0)
				invalids.add(KEY_UNIT_NO);
			if(address.getPostalCode().length() == 0)
				invalids.add(KEY_POSTAL_CODE);
			
			if(invalids.size() > 0)
				view.error(invalids);
			else 
				valid = true;
			
		} while(!valid && !view.bailout());
	}

}
