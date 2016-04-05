package model.service.fnb;

/**
 * OrderStatus is an enumeration that specifies the possible status of an {@link ServiceOrder}
 * @author YingHao
 */
public enum OrderStatus {
	/**
	 * Order has been confirmed.
	 */
	Confirmed, 
	/**
	 * Order has been processed and preparation has started.
	 */
	Preparing, 
	/**
	 * Order has been delivered.
	 */
	Delivered,
	/**
	 * Order has been cancelled.
	 */
	Cancelled
}
