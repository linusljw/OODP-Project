package model.room;

import persistence.Entity;

/**
 * RoomDescription is an {@link Entity} that encapsulates description about a {@link Room}.
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
	
	/**
	 * Gets a flag indicating if this RoomDescription instance fulfils the criteria set by the specified RoomDescription instance.
	 * @return flag
	 */
	public boolean fulfils(RoomDescription desc) {
		boolean flag = true;
		
		if(desc == null)
			flag = false;
		else {
			if(desc.getView() != null && !desc.getView().toLowerCase().equals(this.getView().toLowerCase()))
				flag = false;
			else if(desc.getRoomType() != null && !desc.getRoomType().equals(this.getRoomType()))
				flag = false;
			else if(desc.getBedType() != null && !desc.getBedType().equals(this.getBedType()))
				flag = false;
			else if(desc.isSmoking() == true && this.isSmoking() == false)
				flag = false;
			else if(desc.isWifi() == true && this.isWifi() == false)
				flag = false;
		}
		
		return flag;
	}
	
	/**
	 * Sets the fields of the specified {@link RoomDescription} instance to be similar to the fields of this RoomDescription instance.
	 * @param desc
	 */
	public void set(RoomDescription desc) {
		desc.setBedType(this.bedType);
		desc.setRoomType(this.type);
		desc.setView(this.view);
		desc.setIsWifi(this.wifi);
		desc.setIsSmoking(this.smoking);
	}

	/**
	 * Clones this {@link RoomDescription} instance and returns a new instance with similar values.
	 */
	public RoomDescription clone() {
		RoomDescription desc = new RoomDescription();
		set(desc);
		
		return desc;
	}

	@Override
	public String toString() {
		String roomType = "Any";
		String bedType = "Any";
		String view = "Any";
		String wifi = "Not required";
		String smoking = "Not required";
		
		if(getRoomType() != null)
			roomType = getRoomType().getName();
		if(getBedType() != null)
			bedType = getBedType().toString();
		if(getView() != null)
			view = getView();
		if(isWifi())
			wifi = "Required";
		if(isSmoking())
			smoking = "Required";
		
		return "Room type: " + roomType + "\n" +
				"Bed type: " + bedType + "\n" +
				"View: " + view + "\n" +
				"Wifi-Enabled: " + wifi + "\n" +
				"Smoking-Room: " + smoking + "\n";
	}
}
