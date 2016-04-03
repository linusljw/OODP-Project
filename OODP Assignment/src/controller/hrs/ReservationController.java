package controller.hrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import controller.AddressValidator;
import controller.EntityController;
import controller.PersistenceController;
import model.BedType;
import model.BillingInformation;
import model.Guest;
import model.Reservation;
import model.ReservationStatus;
import model.Room;
import model.RoomType;
import persistence.Persistence;
import persistence.Predicate;
import persistence.file.text.EntityIterator;
import view.Options;
import view.View;
import viewmodel.reservation.BedTypeVM;
import viewmodel.reservation.RoomTypeVM;
import viewmodel.reservation.TextAndCountVM;

/**
 * ReservationController is a controller that performs reservation operations.
 * @author YingHao
 */
public class ReservationController extends PersistenceController implements ReservationInterface {
	public final static String DATE_FORMAT = "dd-MM-yyyy";
	public final static String KEY_RESERVATION_NO = "reservation number or 'Search' to search for reservation by guest";
	public final static String KEY_NUM_CHILDREN = "number of children(s)";
	public final static String KEY_NUM_ADULT = "number of adult(s)";
	public final static String KEY_START_DATE = "start date(" + DATE_FORMAT + ")";
	public final static String KEY_END_DATE = "end date(" + DATE_FORMAT + ")";
	public final static String KEY_ROOM_TYPE = "Room Type";
	public final static String KEY_BED_TYPE = "Bed Type";
	public final static String KEY_VIEW = "Room View";
	public final static String KEY_WIFI = "Wifi Status";
	public final static String KEY_SMOKING = "Smoking Room";
	public final static String KEY_CREDIT_CARD_NO = "credit card number(Omit dashes and spaces)";
	public final static String KEY_CVV_NO = "credit card cvv/cvc";
	private final EntityController<Guest> gController;
	
	/**
	 * ReservationController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 * @param gController - The Guest EntityController to allow ReservationController to interact with for information sharing.
	 */
	public ReservationController(Persistence persistence, EntityController<Guest> gController) {
		super(persistence);
		this.gController = gController;
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("Check room availability/Make a reservation",
				"Cancel a reservation", 
				"Search reservation by guest");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			makeReservation(view);
			break;
		case 1:
			cancelReservation(view);
			break;
		case 2:
			searchReservation(view);
			break;
		}
	}
	
	@Override
	public Reservation makeReservation(View view) throws Exception {
		Reservation reservation = null;
		Guest guest = gController.select(view);
		
		if(guest != null)
			reservation = makeReservation(view, guest);
		
		return reservation;
	}
	
	@Override
	public Reservation makeReservation(View view, Guest guest) throws Exception {
		Reservation reservation = new Reservation(guest);
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		
		inputMap.put(KEY_START_DATE, null);
		inputMap.put(KEY_END_DATE, null);
		
		Persistence persistence = this.getPersistenceImpl();
		boolean valid = false;
		do {
			view.input(inputMap);
			
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				
				Date todayDate = sdf.parse(sdf.format(new Date()));
				Date startDate = sdf.parse(inputMap.get(KEY_START_DATE));
				Date endDate = sdf.parse(inputMap.get(KEY_END_DATE));
				
				if(!startDate.before(todayDate)) {
					if(endDate.after(startDate)) {
						reservation.setStartDate(startDate);
						reservation.setEndDate(endDate);
						
						// Updates the criteria for the desired room
						updateRoomCriteria(view, reservation);
						
						// Display expected cost
						double cost = 0;
						if(reservation.getCriteria().getRoomType() == null) {
							// No room type selected, find the highest price
							Iterable<RoomType> roomTypes = persistence.search(null, RoomType.class, false);
							for(RoomType rType: roomTypes)
								if(rType.getPrice() > cost)
									cost = rType.getPrice();
						}
						else {
							// Retrieve the cost of the selected room type
							cost = reservation.getCriteria().getRoomType().getPrice();
						}
						cost = cost * (reservation.getEndDate().getTime() - reservation.getStartDate().getTime()) / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
						view.message("The expected cost will be: $" + String.format("%.2f", cost));
						
						// Prompts the user whether or not the reservation should be made
						view.message("Do you want to continue to make the reservation?");
						if(view.options(Arrays.asList(Options.Yes, Options.No)) == Options.Yes) {
							inputMap.clear();
							inputMap.put(KEY_NUM_CHILDREN, null);
							inputMap.put(KEY_NUM_ADULT, null);
							
							do {
								view.input(inputMap);
								
								try {
									reservation.setNumOfChildren(Integer.parseInt(inputMap.get(KEY_NUM_CHILDREN)));
									reservation.setNumOfAdult(Integer.parseInt(inputMap.get(KEY_NUM_ADULT)));
									
									// Updates the billing information
									updateBillingInformation(view, guest, reservation.getBillingInformation());
									
									// Attempts to reserve room for the reservation
									reserveRoomForReservation(reservation);
									
									persistence.create(reservation, Reservation.class);
									
									valid = true;
									if(reservation.getStatus() == ReservationStatus.Waitlist)
										view.message("The reservation has been made, but no room is currently available, your reservation has been placed in the waiting list.");
									else
										view.message("The reservation has been made, and a room has been reserved for you.");
									
									view.message("Please take note of the reservation receipt below");
									view.display(reservation);
									view.display(reservation.getCriteria());
									view.display(reservation.getBillingInformation());
								} catch(NumberFormatException e) {
									view.error(Arrays.asList(KEY_NUM_CHILDREN, KEY_NUM_ADULT));
								}
							} while(!valid && !view.bailout());
						}
						else {
							valid = true;
						}
					}
					else {
						view.message("Invalid end date, end date must be after start date.");
					}
				}
				else {
					view.message("Invalid start date, start date must be equals to or after today's date.");
				}
			} catch(ParseException e) {
				view.error(Arrays.asList(KEY_START_DATE, KEY_END_DATE));
			}
		} while(!valid && !view.bailout());
		
		return reservation;
	}
	
	@Override
	public boolean cancelReservation(View view) throws Exception {
		boolean success = false;
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_RESERVATION_NO, null);
		
		Persistence persistence = this.getPersistenceImpl();
		boolean valid = false;
		do {
			view.input(inputMap);
			
			String input = inputMap.get(KEY_RESERVATION_NO);
			if(input.toLowerCase().equals("search"))
				searchReservation(view);
			else {
				try {
					long id = Long.parseLong(inputMap.get(KEY_RESERVATION_NO));
					Reservation reservation = persistence.retrieveByID(id, Reservation.class);
					
					if(reservation == null) {
						view.error(Arrays.asList(KEY_RESERVATION_NO));
						valid = view.bailout();
					}
					else if(reservation.getStatus() != ReservationStatus.Confirmed && reservation.getStatus() != ReservationStatus.Waitlist) {
						view.message("Unable to cancel the selected reservation, this reservation is not eligible for cancellation.");
						valid = true;
					}
					else {
						Room room = reservation.getAssignedRoom();
						reservation.setStatus(ReservationStatus.Cancelled);
						
						persistence.update(reservation, Reservation.class);
						
						success = true;
						if(room != null) {
							// Attempts to find rooms that are on the waitlist that can be assigned
							// with this room
							Iterable<Reservation> waitlist = persistence.search(new Predicate<Reservation>() {

								@Override
								public boolean test(Reservation item) {
									return item.getStatus() == ReservationStatus.Waitlist && 
											new RoomReservationPredicate(item).test(room);
								}
								
							}, Reservation.class, true);
							
							List<Reservation> reservations = new ArrayList<Reservation>();
							// Stores into temporary list as we can read from file and update at the same time
							for(Reservation r: waitlist)
								reservations.add(r);
							
							// Perform last round check to ensure that the selected reservation is eligible to be assigned this room
							// This step is to ensure that assigning this room to the next reservation will not affect the
							// eligibility of the succeeding reservations
							for(Reservation r: reservations) {
								if(new RoomReservationPredicate(r).test(room)) {
									r.setAssignedRoom(room);
									persistence.update(r, Reservation.class);
								}
							}
						}
						
						view.message("Reservation cancelled!");
						valid = true;
					}
				} catch(NumberFormatException e) {
					view.error(Arrays.asList(KEY_RESERVATION_NO));
					valid = view.bailout();
				}
			}
		} while(!valid);
		
		return success;
	}
	
	@Override
	public void searchReservation(View view) throws Exception {
		Guest guest = gController.select(view);
		
		if(guest != null) {
			Date today = new Date();
			Iterable<Reservation> reservations = this.getPersistenceImpl().search(new Predicate<Reservation>() {
	
				@Override
				public boolean test(Reservation item) {
					return !item.getEndDate().before(today) && 
							item.getStatus() != ReservationStatus.Expired &&
							item.getStatus() != ReservationStatus.Cancelled && 
							guest.equals(item.getGuest());
				}
				
			}, Reservation.class, true);
			
			List<Reservation> reservationList = new ArrayList<Reservation>();
			for(Reservation reservation: reservations)
				reservationList.add(reservation);
			
			view.display(reservationList);
		}
	}
	
	@Override
	public boolean reserveRoomForReservation(Reservation reservation) throws Exception {
		boolean flag = false;
		
		if(reservation.getStatus() == ReservationStatus.Waitlist) {
			Persistence persistence = this.getPersistenceImpl();
			EntityIterator<Room> rooms = (EntityIterator<Room>) persistence.search(new RoomReservationPredicate(reservation), Room.class, true).iterator();
			
			if(rooms.hasNext()) {
				reservation.setAssignedRoom(rooms.next());
				flag = true;
			}
			
			rooms.close();
		}
		
		return flag;
	}
	
	/**
	 * Prompts the user to enter relevant information for their desired room and populates the criteria attribute of the provided
	 * reservation parameter.
	 * with the information.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - The criteria attribute of reservation will be populated with appropriate RoomDescription values.
	 */
	private void updateRoomCriteria(View view, Reservation reservation) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		boolean done = false;
		do {
			long matches = persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true);
			view.message("There are " + matches + " room(s) that are available and matches your room requirements");
			view.message("Do you want to refine your room requirements?");
			if(view.options(Arrays.asList(Options.Yes, Options.No)).equals(Options.No))
				done = true;
			else {
				view.message("Please select one of the options to update your room requirements");
				// Request the user to select an option to update the room requirement
				switch(view.options(Arrays.asList(KEY_ROOM_TYPE, KEY_BED_TYPE, KEY_VIEW, KEY_WIFI, KEY_SMOKING))) {
				case KEY_ROOM_TYPE:
					updateRoomType(view, reservation);
					break;
				case KEY_BED_TYPE:
					updateBedType(view, reservation);
					break;
				case KEY_VIEW:
					updateView(view, reservation);
					break;
				case KEY_WIFI:
					updateWifiStatus(view, reservation);
					break;
				case KEY_SMOKING:
					updateSmokingStatus(view, reservation);
					break;
				}
			}
		} while(!done);
	}
	
	/**
	 * Displays available room types and number of available rooms with that room type and prompts the user
	 * to select a RoomType to be populated into the criteria attribute of the specified reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 */
	private void updateRoomType(View view, Reservation reservation) throws Exception {
		RoomType rType = reservation.getCriteria().getRoomType();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String rTypeName = "Any";
		if(rType != null)
			rTypeName = rType.getName();
		view.message("Currently selected room type: " + rTypeName);
		
		List options = new ArrayList();
		Iterable<RoomType> roomTypes = persistence.search(null, RoomType.class, false);
		for(RoomType roomType: roomTypes) {
			reservation.getCriteria().setRoomType(roomType);
			options.add(new RoomTypeVM(roomType, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		}
		options.add(Options.Any);
		
		view.message("Please select a room type");
		Object selected = view.options(options);
		if(selected.equals(Options.Any))
			rType = null;
		else
			rType = ((RoomTypeVM) selected).getRoomType();
		
		reservation.getCriteria().setRoomType(rType);
	}
	
	/**
	 * Displays available bed types and number of available rooms with that bed type and prompts the user
	 * to select a BedType to be populated into the criteria attribute of the specified reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 */
	private void updateBedType(View view, Reservation reservation) throws Exception {
		BedType bType = reservation.getCriteria().getBedType();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String bTypeName = "Any";
		if(bType != null)
			bTypeName = bType.toString();
		view.message("Currently selected bed type: " + bTypeName);
		
		List options = new ArrayList();
		for(BedType bedType: BedType.values()) {
			reservation.getCriteria().setBedType(bedType);
			options.add(new BedTypeVM(bedType, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		}
		options.add(Options.Any);
		
		view.message("Please select a room type");
		Object selected = view.options(options);
		if(selected.equals(Options.Any))
			bType = null;
		else
			bType = ((BedTypeVM) selected).getBedType();
		
		reservation.getCriteria().setBedType(bType);
	}
	
	/**
	 * Displays available room views and number of available rooms with that view and prompts the user
	 * to select a view to be populated into the criteria attribute of the specified reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 */
	private void updateView(View view, Reservation reservation) throws Exception {
		String rView = reservation.getCriteria().getView();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String rViewName = "Any";
		if(rView != null)
			rViewName = rView;
		view.message("Currently selected room view: " + rViewName);
		
		List options = new ArrayList();
		Iterable<Room> rooms = persistence.search(null, Room.class, true);
		for(Room room: rooms) {
			TextAndCountVM viewModel = new TextAndCountVM(room.getView(), 0);
			if(!options.contains(viewModel)) {
				reservation.getCriteria().setView(room.getView());
				viewModel.setCount(persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true));
				options.add(viewModel);
			}
		}
		options.add(Options.Any);
		
		view.message("Please select the desired view for your room");
		Object selected = view.options(options);
		if(selected.equals(Options.Any))
			rView = null;
		else
			rView = ((TextAndCountVM) selected).getText();
		
		reservation.getCriteria().setView(rView);
	}
	
	/**
	 * Displays number of available wifi-enabled rooms and prompts the user to select
	 * a requirement to be populated into the criteria attribute of the specified reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 */
	private void updateWifiStatus(View view, Reservation reservation) throws Exception {
		boolean wifiStatus = reservation.getCriteria().isWifi();
		
		String required = Options.Required.toString();
		String notRequired = Options.NotRequired.toString();
		
		String wifiStatusName = notRequired;
		if(wifiStatus)
			wifiStatusName = required;
		view.message("Currently selected Wifi requirement: " + wifiStatusName);
		
		Persistence persistence = this.getPersistenceImpl();
		List<TextAndCountVM> options = new ArrayList<TextAndCountVM>();
		
		reservation.getCriteria().setIsWifi(true);
		options.add(new TextAndCountVM(required, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		reservation.getCriteria().setIsWifi(false);
		options.add(new TextAndCountVM(notRequired, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		
		view.message("Please select a Wifi requirement");
		wifiStatus = view.options(options).getText().equals(required);
		
		reservation.getCriteria().setIsWifi(wifiStatus);
	}
	
	/**
	 * Displays number of available rooms that allows smoking and prompts the user to select
	 * a requirement to be populated into the criteria attribute of the specified reservation.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 */
	private void updateSmokingStatus(View view, Reservation reservation) throws Exception {
		boolean smokingStatus = reservation.getCriteria().isSmoking();
		
		String required = Options.Required.toString();
		String notRequired = Options.NotRequired.toString();
		
		String smokingStatusName = required;
		if(smokingStatus)
			smokingStatusName = notRequired;
		view.message("Currently selected Smoking-Room requirement: " + smokingStatusName);
		
		Persistence persistence = this.getPersistenceImpl();
		List<TextAndCountVM> options = new ArrayList<TextAndCountVM>();
		
		reservation.getCriteria().setIsSmoking(true);
		options.add(new TextAndCountVM(required, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		reservation.getCriteria().setIsSmoking(false);
		options.add(new TextAndCountVM(notRequired, persistence.getCount(new RoomReservationPredicate(reservation), Room.class, true)));
		
		view.message("Please select a Smoking-Room requirement");
		smokingStatus = view.options(options).getText().equals(required);
		
		reservation.getCriteria().setIsSmoking(smokingStatus);
	}
	
	/**
	 * Prompts the user to enter relevant information for billing information and populates the BillingInformation parameter
	 * with the information.
	 * @param view - A view interface that provides input/output.
	 * @param guest - The guest that made the billing request.
	 * @param billing - BillingInformation to be populated with user entered information.
	 */
	private void updateBillingInformation(View view, Guest guest, BillingInformation billing) throws Exception {
		view.message("----- Billing Information -----");
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_CREDIT_CARD_NO, null);
		inputMap.put(KEY_CVV_NO, null);
		
		boolean valid = false;
		do {
			view.input(inputMap);
			
			billing.setCreditCardNumber(inputMap.get(KEY_CREDIT_CARD_NO));
			billing.setCVV(inputMap.get(KEY_CVV_NO));
			
			List<String> invalids = new ArrayList<String>();
			if(!Pattern.matches(
					"^(?:4[0-9]{12}(?:[0-9]{3})?" + // Visa
					"|5[1-5][0-9]{14}" + // Mastercard
					"|3[47][0-9]{13}" + // American Express
					"|3(?:0[0-5]|[68][0-9])[0-9]{11}" + // Diners club
					"|6(?:011|5[0-9]{2})[0-9]{12}" + // Discover
					"|(?:2131|1800|35\\d{3})\\d{11}" + // JCB
					")$", billing.getCreditCardNumber()))
				invalids.add(KEY_CREDIT_CARD_NO);
			if(!Pattern.matches(
					"[0-9]{3}|[0-9]{4}", billing.getCVV()))
				invalids.add(KEY_CVV_NO);
			
			if(invalids.size() > 0)
				view.error(invalids);
			else {
				view.message("Do you want to use guest's address as billing address?");
				if(view.options(Arrays.asList(Options.Yes, Options.No)).equals(Options.Yes))
					guest.getAddress().set(billing.getAddress());
				else
					AddressValidator.update(view, billing.getAddress());
				
				valid = true;
			}
		} while(!valid);
	}
}
