package controller.hrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import controller.EntityController;
import controller.PersistenceController;
import model.Guest;
import model.Reservation;
import model.ReservationStatus;
import model.Room;
import model.RoomStatus;
import persistence.Persistence;
import persistence.Predicate;
import persistence.file.text.EntityIterator;
import view.Options;
import view.View;

/**
 * CheckInCheckOutController is a controller that performs check-in check-out operations.
 * @author YingHao
 */
public class CheckInCheckOutController extends PersistenceController {
	public final static String KEY_GRACE_PERIOD = "grace-period";
	public final static String KEY_RESERVATION_NO = "reservation number or 'Search' to search for reservation by guest";
	private final EntityController<Guest> gController;
	private final ReservationInterface rInterface;

	/**
	 * CheckInCheckOutController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 * @param rInterface - The ReservationInterface to allow CheckInCheckOutController to interact with for handling reservations.
	 */
	public CheckInCheckOutController(Persistence persistence, EntityController<Guest> gController,ReservationInterface rInterface) {
		super(persistence);
		this.gController = gController;
		this.rInterface = rInterface;
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("Check in", "Check out");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			performCheckIn(view);
			break;
		}
	}
	
	/**
	 * Prompts user to enter relevant information to perform a check-in.
	 * @param view - A view interface that provides input/output.
	 */
	private void performCheckIn(View view) throws Exception {
		Guest guest = gController.select(view);
		
		Persistence persistence = this.getPersistenceImpl();
		CheckInPredicate predicate = new CheckInPredicate(guest);
		if(guest != null) {
			long count = persistence.getCount(predicate, Reservation.class, true);
			
			List<Reservation> reservations = new ArrayList<Reservation>();
			List<Options> ynOptionList = Arrays.asList(Options.Yes, Options.No);
			if(count == 0) {
				// There are no reservations, ask user to make reservation
				view.message("You have no reservations for today, do you want to make one?");
				if(view.options(ynOptionList) == Options.Yes) {
					Reservation reservation = rInterface.makeReservation(view, guest);
					
					// Add reservation to list if it passes check-in predicate
					if(reservation != null) {
						if(predicate.test(reservation))
							reservations.add(reservation);
						else
							view.message("Thank you for making a reservation with our hotel, but the reservations is not made for today, hence you can't be checked in now.");
					}
				}
			}
			else {
				view.message("You have " + count + " reservations eligible for check-in");
				view.message("Do you wish to add check-in all your reservations (Select no to inspect each reservation to decide which ones to check-in)?");
				
				Options selectedOption = view.options(ynOptionList);
				
				// Display reservations for user to select
				Iterable<Reservation> rIterable = persistence.search(predicate, Reservation.class, true);
				for(Reservation reservation: rIterable) {
					if(selectedOption == Options.Yes) {
						reservations.add(reservation);
					}
					else {
						view.display(reservation);
						view.display(reservation.getCriteria());
						view.message("Do you wish to check-in this reservation?");
						if(view.options(ynOptionList) == Options.Yes)
							reservations.add(reservation);
					}
				}
			}
			
			// Proceed with check-in
			if(reservations.size() > 0) {
				view.message("You have selected " + reservations.size() + " reservation(s), do you wish to proceed to check-in?");
				if(view.options(Arrays.asList(Options.Yes, Options.No)) == Options.Yes)
					checkin(view, reservations);
			}
			else {
				view.message("You have no reservations selected for check-in");
			}
		}
	}
	
	/**
	 * Performs check in for all the reservations present in the specified list and prints out
	 * the respective room numbers.
	 * @param reservations - The reservations to perform check in.
	 */
	private void checkin(View view, List<Reservation> reservations) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		for(Reservation reservation: reservations) {
			view.display(reservation);
			
			if(reservation.getStatus() == ReservationStatus.Waitlist)
				reservation.setAssignedRoom(findVacantAndAvailableRoom(reservation));
			
			if(reservation.getStatus() == ReservationStatus.Confirmed) {
				reservation.setStatus(ReservationStatus.CheckedIn);
				persistence.update(reservation, Reservation.class);
				view.message("The above reservation has been checked-in successfully, the room number assigned is " + reservation.getAssignedRoom().getNumber());
			}
			else {
				view.message("We are unable to check-in for the reservation above as it is still in the wait list and there are no available rooms for the specified room requirements.");
			}
		}
	}
	
	private Room findVacantAndAvailableRoom(Reservation reservation) throws Exception {
		Room room = null;
		Persistence persistence = this.getPersistenceImpl();
		
		long now = new Date().getTime();
		long grace = Long.parseLong(persistence.getConfiguration().getProperty(KEY_GRACE_PERIOD, Integer.toString(10))) * 60 * 60 * 1000;
		
		List<Reservation> expiredList = new ArrayList<Reservation>();
		// Attempts to get a room if reservation is in wait list
		EntityIterator<Room> roomIterator = (EntityIterator<Room>) persistence.search(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				boolean flag = false;
				
				// Only care about vacant rooms
				if(item.getStatus() == RoomStatus.Vacant && item.getDescription().fulfils(reservation.getCriteria())) {
					// Loop through the list to find out which reservation has expired (Grace period is over).
					List<Reservation> reservations = item.getReservationList();
					for(int i = 0; i < reservations.size(); i++) {
						Reservation roomReservation = reservations.get(i);
						// Find overlapping reservations
						if(roomReservation.getStartDate().before(reservation.getEndDate()) &&
								roomReservation.getEndDate().after(reservation.getStartDate())) {
							long overtime = now - roomReservation.getStartDate().getTime();
							if(overtime > grace) {
								// Find expired reservations
								flag = true;
								expiredList.add(roomReservation);
							}
							else {
								// Sets flag back to false if we find reservations that are
								// overlapping and yet not expiring
								flag = false;
							}
						}
					}
				}
				
				return flag;
			}
			
		}, Room.class, true).iterator();
		
		if(roomIterator.hasNext())
			room = roomIterator.next();
		
		roomIterator.close();
		
		// Update expired reservations with their correct status
		for(Reservation expired: expiredList) {
			expired.setStatus(ReservationStatus.Expired);
			persistence.update(expired, Reservation.class);
		}
		
		return room;
	}

}
