package model;

import java.util.Arrays;

import persistence.CascadeType;
import persistence.Entity;
import persistence.PersistAnnotation;

/**
 * BillingInformation is an {@link Entity} that encapsulates informaton about billing.
 * @author YingHao
 */
public class BillingInformation extends Entity {
	@PersistAnnotation(
			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final Address address;
	private String creditCardNo;
	private String cvv;
	
	/**
	 * BillingInformation constructor.
	 */
	public BillingInformation() {
		this.address = new Address();
	}
	
	/**
	 * Gets the billing address.
	 * @return address
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * Gets the credit card number.
	 * @return creditCardNo
	 */
	public String getCreditCardNumber() {
		return creditCardNo;
	}
	
	/**
	 * Sets the credit card number.
	 * @param creditCardNo - Credit card number.
	 */
	public void setCreditCardNumber(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}
	
	/**
	 * Gets the CVV of the credit card.
	 * @return cvv
	 */
	public String getCVV() {
		return cvv;
	}
	
	/**
	 * Sets the CVV of the credit card.
	 * @param cvv - Card security code.
	 */
	public void setCVV(String cvv) {
		this.cvv = cvv;
	}

	@Override
	public String toString() {
		String ccNumberMask = getCreditCardNumber();
		
		// Prepare obfuscation mask
		char[] mask = new char[ccNumberMask.length() - 4];
		Arrays.fill(mask, '*');
		ccNumberMask = new String(mask) + ccNumberMask.substring(ccNumberMask.length() - 4, ccNumberMask.length());
		
		return "----- Billing Information -----\n" +
				"Credit card number: " + ccNumberMask + "\n" +
				getAddress().toString();
	}
}
