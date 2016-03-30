package model;

import java.util.Date;

/**
 * ServiceOrder is a {@link StatusEntity} class that encapsulates information about a room
 * service order.
 * @author YingHao
 */
public class ServiceOrder extends StatusEntity<OrderStatus> {
	private final MenuItem item;
	private final Date timestamp;
	private String remarks;
	
	/**
	 * RoomServiceOrder constructor.
	 * @param item - The MenuItem that this RoomServiceOrder will reference.
	 */
	public ServiceOrder(MenuItem item) {
		this.item = item;
		this.timestamp = new Date();
	}
	
	/**
	 * Gets the MenuItem this RoomServiceOrder references.
	 * @return
	 */
	public MenuItem getItem() {
		return item;
	}
	
	/**
	 * Gets the timestamp that indicates when this RoomServiceOrder was created.
	 * @return
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Gets the remarks.
	 * @return
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
