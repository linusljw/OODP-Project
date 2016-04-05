package viewmodel.reservation;

import model.room.BedType;
import model.room.Room;

/**
 * A view model class that encapsulates information about a {@link BedType} and the number of {@link Room} instances with the
 * specified {@link BedType}.
 * @author YingHao
 */
public class BedTypeVM {
	private final BedType bedType;
	private final long count;
	
	/**
	 * BedTypeVM constructor.
	 * @param bedType - BedType that is associated to this instance.
	 * @param count - The number of rooms that are available for the associated bed type.
	 */
	public BedTypeVM(BedType bedType, long count) {
		this.bedType = bedType;
		this.count = count;
	}

	/**
	 * Gets the {@link BedType} associated with this instance.
	 * @return bedType
	 */
	public BedType getBedType() {
		return bedType;
	}
	
	/**
	 * Gets the number of {@link Room} instances that are available with the specified {@link BedType}.
	 * @return count
	 */
	public long getCount() {
		return count;
	}

	@Override
	public String toString() {
		return bedType.toString() + "(" + count + " room(s) available)";
	}
	
}
