package controller.report;

import java.util.Date;
import java.util.List;

import model.reservation.Reservation;
import model.reservation.ReservationStatus;
import persistence.Predicate;

/**
 * ReservationPredicate for retrieving relevant information for report generation.
 * @author YingHao
 */
public class ReservationPredicate implements Predicate<Reservation> {
	private final List<ReservationStatus> statuses;
	private final Date startDate;
	private final Date endDate;
	
	/**
	 * ReservationPredicate constructor.
	 * @param statuses - The list of statuses to check for the reservations. Reservations will be identified as positive on a OR basis.
	 * @param start - The start date of the date range to check for overlaps.
	 * @param end - The end date of the date range to check for overlaps.
	 */
	public ReservationPredicate(List<ReservationStatus> statuses, Date start, Date end) {
		this.statuses = statuses;
		this.startDate = start;
		this.endDate = end;
	}

	@Override
	public boolean test(Reservation item) {
		boolean flag = false;
		
		// Loop through statuses to find a matching status
		for(ReservationStatus status: statuses) {
			if(item.getStatus() == status) {
				flag = true;
				break;
			}
		}
		
		// Additional condition to check
		flag = flag && !startDate.after(item.getEndDate()) && !endDate.before(item.getStartDate());
		
		return flag;
	}

}
