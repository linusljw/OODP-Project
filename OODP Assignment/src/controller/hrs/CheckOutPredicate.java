package controller.hrs;

import model.Guest;
import model.reservation.Reservation;
import model.reservation.ReservationStatus;
import persistence.Predicate;

/**
 * CheckOutPredicate is a predicate class that determines whether a Reservation
 * passes or fails a check-out predicate which includes conditions such as start and end date and
 * guest it belongs to.
 * @author YingHao
 */
public class CheckOutPredicate implements Predicate<Reservation> {
	private final Guest guest;
	
	/**
	 * CheckOutPredicate constructor.
	 * @param guest - The guest to perform this check out predicate on.
	 */
	public CheckOutPredicate(Guest guest) {
		this.guest = guest;
	}

	@Override
	public boolean test(Reservation item) {
		return item.getStatus() == ReservationStatus.CheckedIn &&
				item.getGuest().equals(guest);
	}

}
