package model;

import persistence.Entity;

/**
 * StatusEntity is an abstract class for all {@link Entity} classes that requires a status.
 * @author YingHao
 */
public class StatusEntity<T extends Enum<T>> extends Entity {
	private T status;
	
	/**
	 * Gets the status.
	 * @return The status of this Entity.
	 */
	public T getStatus() {
		return this.status;
	}
	
	/**
	 * Sets the status.
	 * @param status - The status to set for this entity.
	 */
	public void setStatus(T status) {
		this.status = status;
	}
}
