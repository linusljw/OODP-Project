package controller.hrs;

import java.util.Date;

import model.Guest;
import model.reservation.Reservation;
import model.reservation.ReservationStatus;
import persistence.Predicate;

/**
 * CheckInPredicate is a predicate class that determines whether a Reservation
 * passes or fails a check-in predicate which includes conditions such as start and end date and
 * guest it belongs to.
 * @author YingHao
 */
public class CheckInPredicate implements Predicate<Reservation> {
	private final Guest guest;
	
	/**
	 * CheckInPredicate constructor.
	 * @param guest - The guest to perform this check in predicate on.
	 */
	public CheckInPredicate(Guest guest) {
		this.guest = guest;
	}

	@Override
	public boolean test(Reservation item) {
		Date today = new Date();
		
		return (item.getStatus() == ReservationStatus.Confirmed || item.getStatus() == ReservationStatus.Waitlist) &&
				item.getGuest().equals(guest) &&
				!today.before(item.getStartDate()) &&
				!today.after(item.getEndDate());
	}

}
