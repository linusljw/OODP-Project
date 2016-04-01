package model;

import persistence.Entity;

/**
 * RoomDescription is a class that encapsulates description about a {@link Room}.
 * @author YingHao
 */
public class RoomDescription extends Entity {
	private final String view;
	private final RoomType type;
	private final BedType bedType;
	private final boolean wifi;
	private final boolean smoking;
	
	public RoomDescription(String view, RoomType type, BedType bedType, boolean wifi, boolean smoking) {
		this.view = view;
		this.type = type;
		this.bedType = bedType;
		this.wifi = wifi;
		this.smoking = smoking;
	}
	
	/**
	 * Gets room view.
	 * @return view
	 */
	public String getView() {
		return view;
	}
	
	/**
	 * Gets room type.
	 * @return roomType
	 */
	public RoomType getRoomType() {
		return type;
	}
	
	/**
	 * Gets bed type.
	 * @return bedType
	 * @return
	 */
	public BedType getBedType() {
		return bedType;
	}
	
	/**
	 * Gets smoking-room.
	 * @return
	 */
	public boolean isSmoking() {
		return smoking;
	}
	
	/**
	 * Gets is Wifi-Enabled.
	 * @return
	 */
	public boolean isWifi() {
		return wifi;
	}

}
