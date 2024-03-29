package controller.hrs;

import java.util.List;

import model.reservation.Reservation;
import model.reservation.ReservationStatus;
import model.room.Room;
import model.room.RoomStatus;
import persistence.Predicate;

/**
 * RoomReservationPredicate is a predicate class that determines whether a Room
 * passes or fails a reservation predicate which includes conditions such as start and end date
 * as well as room criteria.
 * @author YingHao
 */
public class RoomReservationPredicate implements Predicate<Room> {
	private final Reservation reservation;
	
	/**
	 * RoomReservationPredicate constructor.
	 * @param reservation - The reservation that this predicate should base on.
	 */
	public RoomReservationPredicate(Reservation reservation) {
		this.reservation = reservation;
	}

	@Override
	public boolean test(Room item) {
		boolean flag = true;
		
		if(item.getStatus() == RoomStatus.Maintenance)
			flag = false;
		else {
			List<Reservation> reservations = item.getReservationList();
			for(int i = 0; i < reservations.size(); i++) {
				Reservation roomReservation = reservations.get(i);
				if((roomReservation.getStatus() == ReservationStatus.Confirmed || roomReservation.getStatus() == ReservationStatus.CheckedIn) &&
					roomReservation.getStartDate().before(reservation.getEndDate()) &&
					roomReservation.getEndDate().after(reservation.getStartDate())) {
					flag = false;
					break;
				}
			}
		}
		
		flag = flag && item.getDescription().fulfils(reservation.getCriteria());
		
		return flag;
	}

}
