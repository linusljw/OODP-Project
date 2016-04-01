package controller.hrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import controller.PersistenceController;
import model.BedType;
import model.BillingInformation;
import model.Guest;
import model.Reservation;
import model.ReservationStatus;
import model.Room;
import model.RoomDescription;
import model.RoomType;
import persistence.Persistence;
import persistence.Predicate;
import view.View;
import viewmodel.reservation.BedTypeVM;
import viewmodel.reservation.RoomTypeVM;
import viewmodel.reservation.TextAndCountVM;

/**
 * ReservationController is a controller that performs reservation operations.
 * @author YingHao
 */
public class ReservationController extends PersistenceController {
	public final static String DATE_FORMAT = "dd-MM-yyyy";
	public final static String KEY_NUM_CHILDREN = "number of children(s)";
	public final static String KEY_NUM_ADULT = "number of adult(s)";
	public final static String KEY_START_DATE = "start date(" + DATE_FORMAT + ")";
	public final static String KEY_END_DATE = "end date(" + DATE_FORMAT + ")";
	public final static String KEY_ROOM_TYPE = "Room Type";
	public final static String KEY_BED_TYPE = "Bed Type";
	public final static String KEY_VIEW = "Room View";
	public final static String KEY_WIFI = "Wifi Status";
	public final static String KEY_SMOKING = "Smoking Room";
	public final static String KEY_YES = "Yes";
	public final static String KEY_NO = "No";
	public final static String KEY_ANY = "Any";
	public final static String KEY_REQUIRED = "Required";
	public final static String KEY_NOT_REQUIRED = "Not required";
	private EntityController<Guest> gController;
	
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
		return Arrays.asList("Make a reservation");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			makeReservation(view);
			break;
		}
	}
	
	/**
	 * Prompts the user to enter relevant information to make a reservation.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	private void makeReservation(View view) throws Exception {
		Guest guest = gController.select(view);
		
		if(guest != null) {
			Map<String, String> inputMap = new LinkedHashMap<String, String>();
			Reservation reservation = new Reservation(guest);
			
			inputMap.put(KEY_NUM_CHILDREN, null);
			inputMap.put(KEY_NUM_ADULT, null);
			inputMap.put(KEY_START_DATE, null);
			inputMap.put(KEY_END_DATE, null);
			
			Persistence persistence = this.getPersistenceImpl();
			boolean valid = false;
			do {
				view.input(inputMap);
				
				try {
					reservation.setNumOfChildren(Integer.parseInt(inputMap.get(KEY_NUM_CHILDREN)));
					reservation.setNumOfAdult(Integer.parseInt(inputMap.get(KEY_NUM_ADULT)));
					try {
						SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
						
						Date todayDate = new Date();
						Date startDate = sdf.parse(inputMap.get(KEY_START_DATE));
						Date endDate = sdf.parse(inputMap.get(KEY_END_DATE));
						
						if(startDate.after(todayDate)) {
							if(endDate.after(startDate)) {
								reservation.setStartDate(startDate);
								reservation.setEndDate(endDate);
								
								// Updates the criteria for the desired room
								updateRoomCriteria(view, reservation);
								
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
							}
							else {
								view.message("Invalid end date, end date must be after start date.");
							}
						}
						else {
							view.message("Invalid start date, start date must be after today's date.");
						}
					} catch(ParseException e) {
						view.error(Arrays.asList(KEY_START_DATE, KEY_END_DATE));
					}
				} catch(NumberFormatException e) {
					view.error(Arrays.asList(KEY_NUM_CHILDREN, KEY_NUM_ADULT));
				}
			} while(!valid && !view.bailout());
		}
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
		RoomDescription criteria = reservation.getCriteria();
		do {
			long matches = persistence.getCount(new Predicate<Room>() {
	
				@Override
				public boolean test(Room item) {
					return item.getDescription().fulfils(criteria);
				}
				
			}, Room.class, true);
			view.message("There are " + matches + " room(s) that are available and matches your room requirements");
			view.message("Do you want to refine your room requirements?");
			if(view.options(Arrays.asList(KEY_YES, KEY_NO)).equals(KEY_NO))
				done = true;
			else {
				view.message("Please select one of the options to update your room requirements");
				// Request the user to select an option to update the room requirement
				switch(view.options(Arrays.asList(KEY_ROOM_TYPE, KEY_BED_TYPE, KEY_VIEW, KEY_WIFI, KEY_SMOKING))) {
				case KEY_ROOM_TYPE:
					criteria.setRoomType(getRoomType(view, reservation));
					break;
				case KEY_BED_TYPE:
					criteria.setBedType(getBedType(view, reservation));
					break;
				case KEY_VIEW:
					criteria.setView(getView(view, reservation));
					break;
				case KEY_WIFI:
					criteria.setIsWifi(getWifiStatus(view, reservation));
					break;
				case KEY_SMOKING:
					criteria.setIsSmoking(getSmokingStatus(view, reservation));
					break;
				}
			}
		} while(!done);
	}
	
	/**
	 * Displays available room types and number of available rooms with that room type.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 * @return RoomType instance that the user has selected.
	 */
	private RoomType getRoomType(View view, Reservation reservation) throws Exception {
		RoomType rType = reservation.getCriteria().getRoomType();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String rTypeName = "Any";
		if(rType != null)
			rTypeName = rType.getName();
		view.message("Currently selected room type: " + rTypeName);
		
		List options = new ArrayList();
		Iterable<RoomType> roomTypes = persistence.search(null, RoomType.class, false);
		for(RoomType roomType: roomTypes) {
			options.add(new RoomTypeVM(roomType, persistence.getCount(new Predicate<Room>() {

				@Override
				public boolean test(Room item) {
					return roomType.equals(item.getType());
				}
				
			}, Room.class, true)));
		}
		options.add(KEY_ANY);
		
		view.message("Please select a room type");
		Object selected = view.options(options);
		if(selected.equals(KEY_ANY))
			rType = null;
		else
			rType = ((RoomTypeVM) selected).getRoomType();
		
		return rType;
	}
	
	/**
	 * Displays available bed types and number of available rooms with that bed type.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 * @return BedType enum that user has selected.
	 */
	private BedType getBedType(View view, Reservation reservation) throws Exception {
		BedType bType = reservation.getCriteria().getBedType();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String bTypeName = "Any";
		if(bType != null)
			bTypeName = bType.toString();
		view.message("Currently selected bed type: " + bTypeName);
		
		List options = new ArrayList();
		for(BedType bedType: BedType.values()) {
			options.add(new BedTypeVM(bedType, persistence.getCount(new Predicate<Room>() {

				@Override
				public boolean test(Room item) {
					return item.getBedType() == bedType;
				}
				
			}, Room.class, true)));
		}
		options.add(KEY_ANY);
		
		view.message("Please select a room type");
		Object selected = view.options(options);
		if(selected.equals(KEY_ANY))
			bType = null;
		else
			bType = ((BedTypeVM) selected).getBedType();
		
		return bType;
	}
	
	/**
	 * Displays available room views and number of available rooms with that view.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 * @return String representing the view that user has selected.
	 */
	private String getView(View view, Reservation reservation) throws Exception {
		String rView = reservation.getCriteria().getView();
		
		Persistence persistence = this.getPersistenceImpl();
		
		String rViewName = "Any";
		if(rView != null)
			rViewName = rView;
		view.message("Currently selected room view: " + rViewName);
		
		List options = new ArrayList();
		Iterable<Room> rooms = persistence.search(null, Room.class, true);
		for(Room room: rooms) {
			TextAndCountVM viewModel = new TextAndCountVM(room.getView(), 1);
			if(options.contains(viewModel)) {
				viewModel = (TextAndCountVM) options.get(options.indexOf(viewModel));
				viewModel.setCount(viewModel.getCount() + 1);
			}
			else {
				options.add(viewModel);
			}
		}
		options.add(KEY_ANY);
		
		view.message("Please select the desired view for your room");
		Object selected = view.options(options);
		if(selected.equals(KEY_ANY))
			rView = null;
		else
			rView = ((TextAndCountVM) selected).getText();
		
		return rView;
	}
	
	/**
	 * Displays number of available wifi-enabled rooms.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 * @return A flag indicating the requirement of Wifi.
	 */
	private boolean getWifiStatus(View view, Reservation reservation) throws Exception {
		boolean wifiStatus = reservation.getCriteria().isWifi();
		
		String wifiStatusName = KEY_NOT_REQUIRED;
		if(wifiStatus)
			wifiStatusName = KEY_REQUIRED;
		view.message("Currently selected Wifi requirement: " + wifiStatusName);
		
		Persistence persistence = this.getPersistenceImpl();
		List<TextAndCountVM> options = new ArrayList<TextAndCountVM>();
		options.add(new TextAndCountVM(KEY_REQUIRED, persistence.getCount(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				return item.isWifi();
			}
			
		}, Room.class, true)));
		options.add(new TextAndCountVM(KEY_NOT_REQUIRED, persistence.getCount(null, Room.class, false)));
		
		view.message("Please select a Wifi requirement");
		wifiStatus = view.options(options).getText().equals(KEY_REQUIRED);
		
		return wifiStatus;
	}
	
	/**
	 * Displays number of available rooms that allows smoking.
	 * @param view - A view interface that provides input/output.
	 * @param reservation - Reservation instance that this method will base on for the start and end date.
	 * @return A flag indicating the requirement of Smoking-Room.
	 */
	private boolean getSmokingStatus(View view, Reservation reservation) throws Exception {
		boolean wifiStatus = reservation.getCriteria().isSmoking();
		
		String wifiStatusName = KEY_NOT_REQUIRED;
		if(wifiStatus)
			wifiStatusName = KEY_REQUIRED;
		view.message("Currently selected Smoking-Room requirement: " + wifiStatusName);
		
		Persistence persistence = this.getPersistenceImpl();
		List<TextAndCountVM> options = new ArrayList<TextAndCountVM>();
		options.add(new TextAndCountVM(KEY_REQUIRED, persistence.getCount(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				return item.isSmoking();
			}
			
		}, Room.class, true)));
		options.add(new TextAndCountVM(KEY_NOT_REQUIRED, persistence.getCount(null, Room.class, false)));
		
		view.message("Please select a Smoking-Room requirement");
		wifiStatus = view.options(options).getText().equals(KEY_REQUIRED);
		
		return wifiStatus;
	}
	
	/**
	 * Prompts the user to enter relevant information for billing information and populates the BillingInformation parameter
	 * with the information.
	 * @param view - A view interface that provides input/output.
	 * @param guest - The guest that made the billing request.
	 * @param billing - BillingInformation to be populated with user entered information.
	 */
	private void updateBillingInformation(View view, Guest guest, BillingInformation billing) {
		view.message("----- Billing Information -----");
		// TODO billing
	}
	
	/**
	 * Reserves a room for the specified Reservation.
	 * @param reservation - The reservation used to reserve a room.
	 */
	private void reserveRoomForReservation(Reservation reservation) {
		// TODO
	}
}
