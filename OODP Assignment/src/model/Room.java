package model;

import persistence.CascadeType;
import persistence.PersistAnnotation;

/**
 * Room is a {@link StatusEntity} class that encapsulates information about a Room.
 * @author Yijie
 */
public class Room extends StatusEntity<RoomStatus> {
	private final String number;
	@PersistAnnotation(
			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final RoomDescription description;
	
	/**
	 * Room Constructor. For Persistence API Usage
	 */
	protected Room() {
		this.number = null;
		this.description = null;
	}
	
	/**
	 * Room Constructor
	 * @param number
	 * @param view
	 * @param wifi
	 * @param smoking
	 * @param type
	 * @param bedType
	 */
	public Room(String number) {
		this.number = number;
		this.description = new RoomDescription();
		this.setStatus(RoomStatus.Vacant);
	}

	/**
	 * Gets Room Number.
	 * @return number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Gets Room View.
	 * @return view
	 */
	public String getView() {
		return description.getView();
	}
	
	/**
	 * Sets Room View.
	 * @param view
	 */
	public void setView(String view) {
		this.description.setView(view);
	}

	/**
	 * Gets is Wifi-Enabled.
	 * @return wifi
	 */
	public boolean isWifi() {
		return description.isWifi();
	}

	/**
	 * Sets Wifi-Enabled.
	 * @param wifi
	 */
	public void setWifi(boolean wifi) {
		this.description.setIsWifi(wifi);
	}

	/**
	 * Gets is Smoking-Room.
	 * @return
	 */
	public boolean isSmoking() {
		return description.isSmoking();
	}

	/**
	 * Sets is Smoking-Room.
	 * @param smoking
	 */
	public void setSmoking(boolean smoking) {
		this.description.setIsSmoking(smoking);
	}

	/**
	 * Gets Room Type.
	 * @return type
	 */
	public RoomType getType() {
		return description.getRoomType();
	}

	/**
	 * Sets Room Type.
	 * @param type
	 */
	public void setType(RoomType type) {
		this.description.setRoomType(type);
	}
	
	/**
	 * Gets Bed Type.
	 * @return
	 */
	public BedType getBedType() {
		return description.getBedType();
	}

	/**
	 * Sets Bed Type.
	 * @param bedType
	 */
	public void setBedType(BedType bedType) {
		this.description.setBedType(bedType);
	}
	
	/**
	 * Gets the {@link RoomDescription} of this Room instance.
	 * @return description
	 */
	public RoomDescription getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return super.toString() +
				"Room Number: " + this.getNumber() + "\n" +
				"Room View: " + this.getView() + "\n" +
				"Room Type: " + this.getType().getName() + "\n" +
				"Bed Type: " + this.getBedType() + "\n" +
				"Wifi-Enabled: " + this.isWifi() + "\n" +
				"Smoking-Room: " + this.isSmoking() + "\n";
	}
}
