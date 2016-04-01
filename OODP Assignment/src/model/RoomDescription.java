package model;

import persistence.Entity;

/**
 * RoomDescription is a class that encapsulates description about a {@link Room}.
 * @author YingHao
 */
public class RoomDescription extends Entity {
	private String view;
	private RoomType type;
	private BedType bedType;
	private boolean wifi;
	private boolean smoking;
	
	/**
	 * Gets room view.
	 * @return view
	 */
	public String getView() {
		return view;
	}
	
	/**
	 * Sets the room view.
	 * @param view
	 */
	public void setView(String view) {
		this.view = view;
	}
	
	/**
	 * Gets room type.
	 * @return roomType
	 */
	public RoomType getRoomType() {
		return type;
	}
	
	/**
	 * Sets room type.
	 * @param roomType
	 */
	public void setRoomType(RoomType roomType) {
		this.type = roomType;
	}
	
	/**
	 * Gets bed type.
	 * @return bedType
	 * @return bedType
	 */
	public BedType getBedType() {
		return bedType;
	}
	
	/**
	 * Sets bed type.
	 * @param bedType
	 */
	public void setBedType(BedType bedType) {
		this.bedType = bedType;
	}
	
	/**
	 * Gets smoking-room.
	 * @return smoking
	 */
	public boolean isSmoking() {
		return smoking;
	}
	
	/**
	 * Sets smoking-room.
	 * @param smoking
	 */
	public void setIsSmoking(boolean smoking) {
		this.smoking = smoking;
	}
	
	/**
	 * Gets is Wifi-Enabled.
	 * @return wifi
	 */
	public boolean isWifi() {
		return wifi;
	}
	
	/**
	 * Sets is Wifi-Enabled
	 * @param wifi
	 */
	public void setIsWifi(boolean wifi) {
		this.wifi = wifi;
	}

}
