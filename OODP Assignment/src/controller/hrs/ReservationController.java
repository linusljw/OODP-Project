package controller.hrs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import controller.PersistenceController;
import model.BillingInformation;
import model.Guest;
import model.Reservation;
import model.ReservationStatus;
import model.RoomDescription;
import persistence.Persistence;
import view.View;

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
								updateRoomCriteria(view, reservation.getCriteria());
								
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
	 * Prompts the user to enter relevant information for their desired room and populates the RoomDescription parameter
	 * with the information.
	 * @param view - A view interface that provides input/output.
	 * @param description - RoomDescription to be populated with user entered information.
	 */
	private void updateRoomCriteria(View view, RoomDescription description) {
		// TODO
	}
	
	/**
	 * Prompts the user to enter relevant information for billing information and populates the BillingInformation parameter
	 * with the information.
	 * @param view - A view interface that provides input/output.
	 * @param guest - The guest that made the billing request.
	 * @param billing - BillingInformation to be populated with user entered information.
	 */
	private void updateBillingInformation(View view, Guest guest, BillingInformation billing) {
		// TODO
	}
	
	/**
	 * Reserves a room for the specified Reservation.
	 * @param reservation - The reservation used to reserve a room.
	 */
	private void reserveRoomForReservation(Reservation reservation) {
		// TODO
	}
}
