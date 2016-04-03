package controller.hrs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import controller.PersistenceController;
import model.DiscountType;
import model.Guest;
import model.Payment;
import model.PaymentType;
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
	public final static String KEY_DISCOUNT_VALUE = "discount value";
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
		case 1:
			performCheckOut(view);
			break;
		}
	}
	
	/**
	 * Prompts user to enter relevant information to perform a check-in.
	 * @param view - A view interface that provides input/output.
	 */
	private void performCheckIn(View view) throws Exception {
		Guest guest = gController.select(view);
		
		if(guest != null) {
			Persistence persistence = this.getPersistenceImpl();
			CheckInPredicate predicate = new CheckInPredicate(guest);
			
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
						view.message("Do you wish to check-in the above reservation?");
						if(view.options(ynOptionList) == Options.Yes)
							reservations.add(reservation);
					}
				}
			}
			
			// Proceed with check-in
			if(reservations.size() > 0) {
				view.message("You have selected " + reservations.size() + " reservation(s), do you wish to proceed to check-in?");
				if(view.options(ynOptionList) == Options.Yes)
					checkin(view, reservations);
			}
			else {
				view.message("You have no reservations selected for check-in");
			}
		}
	}
	
	/**
	 * Prompts user to enter relevant information to perform a check-out.
	 * @param view - A view interface that provides input/output.
	 */
	private void performCheckOut(View view) throws Exception {
		Guest guest = gController.select(view);
		
		if(guest != null) {
			Persistence persistence = this.getPersistenceImpl();
			CheckOutPredicate predicate = new CheckOutPredicate(guest);
			
			long count = persistence.getCount(predicate, Reservation.class, true);
			if(count == 0) {
				view.message("No checked-in room that is available for check-out.");
			}
			else {
				view.message("You have " + count + " rooms eligible for check-out");
				view.message("Do you wish to add check-out all your rooms (Select no to inspect each reservation to decide which ones to check-out)?");
				
				List<Reservation> reservations = new ArrayList<Reservation>();
				List<Options> ynOptionList = Arrays.asList(Options.Yes, Options.No);
				Options selectedOption = view.options(ynOptionList);
				
				// Display reservations for user to select
				Iterable<Reservation> rIterable = persistence.search(predicate, Reservation.class, true);
				for(Reservation reservation: rIterable) {
					if(selectedOption == Options.Yes) {
						reservations.add(reservation);
					}
					else {
						view.message("Room number: " + reservation.getAssignedRoom().getNumber());
						view.display(reservation);
						view.message("Do you wish to check-out of the above room?");
						if(view.options(ynOptionList) == Options.Yes)
							reservations.add(reservation);
					}
				}
				
				if(reservations.size() > 0) {
					view.message("You have selected " + reservations.size() + " room(s) to check out, do you wish to proceed?");
					if(view.options(ynOptionList) == Options.Yes)
						checkout(view, reservations);
				}
				else {
					view.message("You have no rooms selected for check-out");
				}
			}
		}
	}
	
	/**
	 * Performs check in for all the reservations present in the specified list and prints out
	 * the respective room numbers.
	 * @param view - A view interface that provides input/output.
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
	
	/**
	 * Performs check out for all the reservations present in the specified list and prints out
	 * the bill.
	 * @param view - A view interface that provides input/output.
	 * @param reservations - The reservations to perform check in.
	 */
	private void checkout(View view, List<Reservation> reservations) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date today = sdf.parse(sdf.format(new Date()));
		for(Reservation reservation: reservations) {
			// Update the end date of the reservations to today's date;
			reservation.setEndDate(today);
		}
		
		Payment payment = new Payment(reservations);
		view.display(payment);
		
		view.message("Do you wish to specify a discount?");
		if(view.options(Arrays.asList(Options.Yes, Options.No)) == Options.Yes) {
			Map<String, String> inputMap = new LinkedHashMap<String, String>();
			inputMap.put(KEY_DISCOUNT_VALUE, null);
			
			view.message("Select a discount type");
			DiscountType dType = view.options(Arrays.asList(DiscountType.values()));
			double value = 0;
			boolean valid;
			do {
				view.input(inputMap);
				try {
					value = Double.parseDouble(inputMap.get(KEY_DISCOUNT_VALUE));
					valid = true;
				} catch(NumberFormatException e) {
					view.error(Arrays.asList(KEY_DISCOUNT_VALUE));
					valid = false;
				}
			} while(!valid);
			
			payment.setDiscount(dType, value);
			view.display(payment);
		}
		
		view.message("Which method of payment to use?");
		payment.setPaymentType(view.options(Arrays.asList(PaymentType.values())));
		
		// Create payment
		persistence.create(payment, Payment.class);
		view.message("Your payment is successful, thank you for staying with us, we hope to see you again!");
		for(Reservation reservation: reservations) {
			// Update status and save changes to file
			reservation.setStatus(ReservationStatus.CheckedOut);
			reservation.setPayment(payment);
			persistence.update(reservation, Reservation.class);
			
			view.message("Successfully checked out from room " + reservation.getAssignedRoom().getNumber() + ".");
		}
	}
	
	/**
	 * Finds a vacant and available room that fits the criteria of the specified reservation.
	 * @param reservation - The reservation to find a room for.
	 * @return A room instance that fits the criteria of the specified reservation and is vacant and available.
	 */
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
