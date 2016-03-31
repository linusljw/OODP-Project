package model;

import persistence.Entity;

/**
 * Room Type is an {@link Entity} class that encapsulates the information of a Room Type.
 * @author Yijie
 */
public class RoomType extends Entity {
	private final String name;
	private double price;
	
	/**
	 * RoomType Constructor. For Persistence API Usage.
	 */
	protected RoomType() {
		this.name = null;
		this.price = 0;
	}
	
	/**
	 * RoomType Constructor
	 * @param name
	 * @param price
	 */
	public RoomType(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the price
	 * @return price
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Sets the price
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return super.toString() +
				"Type: " + this.name + "\n" +
				"Price: " + String.format("%.2f", this.price) + "\n";
	}
}
