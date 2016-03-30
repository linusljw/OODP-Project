package model;

import persistence.Entity;

/**
 * Address is an {@link Entity} class that encapsulates information about an Address.
 * @author YingHao
 */
public class Address extends Entity {
	private String country;
	private String city;
	private String state;
	private String street;
	private String unitNumber;
	private String postalCode;
	
	/**
	 * Gets the country.
	 * @return country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Sets the country.
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Gets the city.
	 * @return city
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Sets the city.
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Gets the state.
	 * @return state
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Gets the street.
	 * @return street
	 */
	public String getStreet() {
		return street;
	}
	
	/**
	 * Sets the street.
	 * @param street
	 */
	public void setStreet(String street) {
		this.street = street;
	}
	
	/**
	 * Gets the unit number.
	 * @return unit number
	 */
	public String getUnitNumber() {
		return unitNumber;
	}
	
	/**
	 * Sets the unit number.
	 * @param unitNumber
	 */
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	
	/**
	 * Gets the postal code.
	 * @return
	 */
	public String getPostalCode() {
		return postalCode;
	}
	
	/**
	 * Sets the postal code.
	 * @param postalCode
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Clones this Address instance and returns a new Address instance with similar values.
	 */
	public Address clone() {
		Address address = new Address();
		
		address.setCity(this.getCity());
		address.setCountry(this.getCountry());
		address.setPostalCode(this.getPostalCode());
		address.setState(this.getState());
		address.setStreet(this.getStreet());
		address.setUnitNumber(this.getUnitNumber());
		
		return address;
	}

	@Override
	public String toString() {
		return "---- Address Information ----\n" +
				"Country:\t" + this.getCountry() + "\n" +
				"State:\t\t" + this.getState() + "\n" +
				"City:\t\t" + this.getCity() + "\n" +
				"Street:\t\t" + this.getStreet() + "\n" + 
				"Unit number:\t" + this.getUnitNumber() + "\n" +
				"Postal code:\t" + this.getPostalCode() + "\n";
	}
	
}
