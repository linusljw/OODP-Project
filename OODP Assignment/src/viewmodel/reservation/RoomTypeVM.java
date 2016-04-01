package viewmodel.reservation;

import model.RoomType;
import model.Room;

/**
 * A view model class that encapsulates information about a {@link RoomType} and the number of {@link Room} instances with the
 * specified {@link RoomType}.
 * @author YingHao
 */
public class RoomTypeVM {
	private final RoomType roomType;
	private final long count;
	
	/**
	 * RoomTypeVM constructor.
	 * @param roomType - RoomType that is associated to this instance.
	 * @param count - The number of rooms that are available for the associated room type.
	 */
	public RoomTypeVM(RoomType roomType, long count) {
		this.roomType = roomType;
		this.count = count;
	}
	
	/**
	 * Gets the {@link RoomType} associated with this instance.
	 * @return roomType
	 */
	public RoomType getRoomType() {
		return roomType;
	}
	
	/**
	 * Gets the number of {@link Room} instances that are available with the specified {@link RoomType}.
	 * @return count
	 */
	public long getCount() {
		return count;
	}

	@Override
	public String toString() {
		return roomType.getName() + "(" + count + " room(s) available)";
	}

}
