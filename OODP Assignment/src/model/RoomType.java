package model;

import persistence.Entity;

/**
 * Room Type is an {@link Entity} class that encapsulates the information of a Room Type.
 * @author Yijie
 */
public class RoomType extends Entity {
	private String name;
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
	public RoomType(String name, double price) {
		this.name = name;
		this.price = price;
	}
	
	/**
	 * Gets the name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
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
}
