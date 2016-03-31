package model;

/**
 * Room is a {@link StatusEntity} class that encapsulates information about a Room.
 * @author Yijie
 */
public class Room extends StatusEntity<RoomStatus> {
	private final String number;
	private String view;
	private boolean wifi;
	private boolean smoking;
	private RoomType type;
	private BedType bedType;
	
	/**
	 * Room Constructor. For Persistence API Usage
	 */
	protected Room() {
		this.number = null;
		this.view = null;
		this.wifi = false;
		this.smoking = false;
		this.type = null;
		this.bedType = null;
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
		this.type = new RoomType();
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
		return view;
	}
	
	/**
	 * Sets Room View.
	 * @param view
	 */
	public void setView(String view) {
		this.view = view;
	}

	/**
	 * Gets is Wifi-Enabled.
	 * @return wifi
	 */
	public boolean isWifi() {
		return wifi;
	}

	/**
	 * Sets Wifi-Enabled.
	 * @param wifi
	 */
	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}

	/**
	 * Gets is Smoking-Room.
	 * @return
	 */
	public boolean isSmoking() {
		return smoking;
	}

	/**
	 * Sets is Smoking-Room.
	 * @param smoking
	 */
	public void setSmoking(boolean smoking) {
		this.smoking = smoking;
	}

	/**
	 * Gets Room Type.
	 * @return type
	 */
	public RoomType getType() {
		return type;
	}

	/**
	 * Sets Room Type.
	 * @param type
	 */
	public void setType(RoomType type) {
		this.type = type;
	}
	
	/**
	 * Gets Bed Type.
	 * @return
	 */
	public BedType getBedType() {
		return bedType;
	}

	/**
	 * Sets Bed Type.
	 * @param bedType
	 */
	public void setBedType(BedType bedType) {
		this.bedType = bedType;
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
