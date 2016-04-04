package model;

import persistence.CascadeType;
import persistence.Entity;
import persistence.PersistAnnotation;

/**
 * Guest is an {@link Entity} class that encapsulates information about a Guest.
 * @author YingHao
 */
public class Guest extends Entity {
	@PersistAnnotation(
			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final BillingInformation billing;
	private final String identification;
	private final String nationality;
	private String name;
	private String contactNo;
	private String emailAddress;
	private char gender;
	
	/**
	 * Guest constructor. For Persistence API usage.
	 */
	protected Guest() {
		this.identification = null;
		this.nationality = null;
		this.billing = null;
	}
	
	/**
	 * Guest constructor.
	 * @param identification - The unique identification number of this Guest.
	 * @param nationality - The nationality of this Guest.
	 */
	public Guest(String identification, String nationality) {
		this.identification = identification;
		this.nationality = nationality;
		this.billing = new BillingInformation();
	}
	
	/**
	 * Gets the name.
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the contact number.
	 * @return contactNo
	 */
	public String getContactNo() {
		return contactNo;
	}

	/**
	 * Sets the contact number.
	 * @param contactNo
	 */
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	/**
	 * Gets the email address.
	 * @return emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address.
	 * @param emailAddress
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the gender.
	 * @return gender
	 */
	public char getGender() {
		return gender;
	}
	
	/**
	 * Sets the gender.
	 * @param gender
	 */
	public void setGender(char gender) {
		this.gender = gender;
	}
	
	/**
	 * Gets the billing information. Note that the lifetime of the returned {@link BillingInformation} is tied to the
	 * Guest. Clone this instance for a seperate {@link BillingInformation} instance with an independent lifetime.
	 * @return address
	 */
	public BillingInformation getBillingInformation() {
		return billing;
	}
	
	/**
	 * Gets the identification.
	 * @return identification
	 */
	public String getIdentification() {
		return identification;
	}
	
	/**
	 * Gets the nationality.
	 * @return nationality
	 */
	public String getNationality() {
		return nationality;
	}

	@Override
	public String toString() {
		return super.toString() +
				"Name: " + this.getName() + "\n" +
				"Identification number: " + this.getIdentification() + "\n" +
				"Nationality: " + this.getNationality() + "\n" +
				"Gender: " + this.getGender() + "\n" +
				"Contact number: " + this.getContactNo() + "\n" +
				"Email address: " + this.getEmailAddress() + "\n\n" +
				this.getBillingInformation().toString();
	}
	
}
