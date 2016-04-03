package model;

import java.util.Date;

/**
 * ServiceOrder is a {@link StatusEntity} class that encapsulates information about a room
 * service order.
 * @author YingHao
 */
public class ServiceOrder extends StatusEntity<OrderStatus> {
	private final Reservation reservation;
	private final MenuItem item;
	private final Date timestamp;
	private Room room;
	private String remarks;
	
	/**
	 * ServiceOrder constructor. For Persistence API usage.
	 */
	protected ServiceOrder() {
		this.reservation = null;
		this.item = null;
		this.timestamp = null;
	}
	
	/**
	 * RoomServiceOrder constructor.
	 * @param reservation - The reservation to place this ServiceOrder under.
	 * @param item - The MenuItem that this RoomServiceOrder will reference.
	 */
	public ServiceOrder(Reservation reservation, MenuItem item) {
		this.reservation = reservation;
		this.item = item;
		this.timestamp = new Date();
	}
	
	/**
	 * Gets the Reservation that this ServiceOrder is under.
	 * @return reservation
	 */
	public Reservation getReservation() {
		return reservation;
	}
	
	/**
	 * Gets the MenuItem this RoomServiceOrder references.
	 * @return item
	 */
	public MenuItem getItem() {
		return item;
	}
	
	/**
	 * Gets the timestamp that indicates when this RoomServiceOrder was created.
	 * @return timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Gets the room where this order is to be served.
	 * @return room
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Sets the room where this order is to be served.
	 * @param room
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	
	/**
	 * Gets the remarks.
	 * @return remarks
	 */
	public String getRemarks() {
		return remarks;
	}
	
	/**
	 * Sets the remarks.
	 * @param remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
