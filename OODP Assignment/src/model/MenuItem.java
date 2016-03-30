package model;

import persistence.Entity;

/**
 * MenuItem is an {@link Entity} class that encapsulates information about a menu item.
 * @author YingHao
 */
public class MenuItem extends Entity {
	private final String name;
	private double price;
	private String description;
	
	/**
	 * MenuItem constructor. For Persistence API usage.
	 */
	protected MenuItem() {
		this.name = null;
	}
	
	/**
	 * MenuItem constructor.
	 * @param name
	 */
	public MenuItem(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the price.
	 * @return
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Sets the price.
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * Gets the description.
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return super.toString() + 
				"Name: " + this.name + "\n" +
				"Price: $" + String.format("%.2f", this.price) + "\n" +
				"Description: " + this.description + "\n";
	}

}
