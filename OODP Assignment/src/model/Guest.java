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
	private final Address address;
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
		this.address = null;
	}
	
	/**
	 * Guest constructor.
	 * @param identification - The unique identification number of this Guest.
	 * @param country - The nationality of this Guest.
	 */
	public Guest(String identification, String nationality) {
		this.identification = identification;
		this.nationality = nationality;
		this.address = new Address();
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
	 * Gets the address. Note that the lifetime of the returned address is tied to the
	 * Guest. Clone the address for a seperate address instance with an independent lifetime.
	 * @return address
	 */
	public Address getAddress() {
		return address;
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
				"Email address: " + this.getEmailAddress() + "\n" +
				this.getAddress().toString();
	}
	
}
