package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import model.BillingInformation;
import view.View;

/**
 * BillInformation is a auxiliary class for classes that wish to manipulate a {@link BillingInformation}
 * instance. It provides a single method {@link BillingInformationValidator#update(View, BillingInformation)} to update the provided {@link BillingInformation} instance. 
 * @author YingHao
 */
public class BillingInformationValidator {
	public final static String KEY_CREDIT_CARD_NO = "credit card number(Omit dashes and spaces)";
	public final static String KEY_CVV_NO = "credit card cvv/cvc";
	
	/**
	 * Updates the given {@link BillingInformation} instance with user input from the view.
	 * @param view - A view interface that provides input/output.
	 * @param billing - The {@link BillingInformation} instance to be updated.
	 */
	public static void update(View view, BillingInformation billing) {
		view.message("----- Billing Information -----");
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_CREDIT_CARD_NO, null);
		inputMap.put(KEY_CVV_NO, null);
		
		boolean valid = false;
		do {
			view.input(inputMap);
			
			billing.setCreditCardNumber(inputMap.get(KEY_CREDIT_CARD_NO));
			billing.setCVV(inputMap.get(KEY_CVV_NO));
			
			List<String> invalids = new ArrayList<String>();
			if(!Pattern.matches(
					"^(?:4[0-9]{12}(?:[0-9]{3})?" + // Visa
					"|5[1-5][0-9]{14}" + // Mastercard
					"|3[47][0-9]{13}" + // American Express
					"|3(?:0[0-5]|[68][0-9])[0-9]{11}" + // Diners club
					"|6(?:011|5[0-9]{2})[0-9]{12}" + // Discover
					"|(?:2131|1800|35\\d{3})\\d{11}" + // JCB
					")$", billing.getCreditCardNumber()))
				invalids.add(KEY_CREDIT_CARD_NO);
			if(!Pattern.matches(
					"[0-9]{3}|[0-9]{4}", billing.getCVV()))
				invalids.add(KEY_CVV_NO);
			
			if(invalids.size() > 0)
				view.error(invalids);
			else {
				view.message("--- Billing Address ---");
				AddressValidator.update(view, billing.getAddress());
				valid = true;
			}
		} while(!valid);
	}

}
