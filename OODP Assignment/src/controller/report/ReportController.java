package controller.report;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import controller.PersistenceController;
import model.Reservation;
import model.ReservationStatus;
import model.Room;
import model.RoomStatus;
import model.RoomType;
import persistence.Persistence;
import persistence.Predicate;
import view.View;

/**
 * A controller reponsible for generating reports.
 * @author YingHao
 *
 */
public class ReportController extends PersistenceController {

	/**
	 * ReportController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 */
	public ReportController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("View report for today", 
								"View report for this week",
								"View report for this month",
								"View report for this year");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		if(option == 0)
			viewReportForToday(view);
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date end = sdf.parse(sdf.format(new Date()));
			Date start = end;
			
			if(option == 1)
				start = new Date(end.getTime() - TimeUnit.DAYS.toMillis(7));
			else if(option == 2)
				start = new Date(end.getTime() - TimeUnit.DAYS.toMillis(30));
			else
				start = new Date(end.getTime() - TimeUnit.DAYS.toMillis(365));
			
			viewReportForRange(view, start, end);
		}
	}
	
	/**
	 * Displays room occupancy report for today.
	 * @param view - A view interface that provides input/output.
	 * @throws Exception 
	 */
	private void viewReportForToday(View view) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		view.message("----- Room Occupancy Report For " + sdf.format(new Date()) + " -----");
		
		int occupiedRoomCount = printRoomsWithStatus(view, RoomStatus.Occupied);
		int vacantRoomCount = printRoomsWithStatus(view, RoomStatus.Vacant);
		int maintenanceRoomCount = printRoomsWithStatus(view, RoomStatus.Maintenance);
		
		double occupancyRate = ((double)occupiedRoomCount / (occupiedRoomCount + vacantRoomCount + maintenanceRoomCount)) * 100;
		view.message("\n----- Summary -----");
		view.message("Number of occupied room(s): " + occupiedRoomCount);
		view.message("Number of vacant room(s): " + vacantRoomCount);
		view.message("Number of room(s) under maintenance: " + maintenanceRoomCount);
		view.message("Percentage of room occupancy: " + 
					String.format("%.2f", occupancyRate) +
					"%\n");
	}
	
	/**
	 * Displays room occupancy report for the specified date range.
	 * @param view - A view interface that provides input/output.
	 * @param startDate - The start date to generate the report.
	 * @param endDate - The end date to generate the report.
	 */
	private void viewReportForRange(View view, Date startDate, Date endDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		view.message("----- Room Occupancy Report(" + sdf.format(startDate) + " to " + sdf.format(endDate) + ") -----");
	
		Persistence persistence = this.getPersistenceImpl();
		long totalRoomCount = persistence.getCount(null, Room.class, false);
		Iterable<Reservation> fulfilledReservations = persistence.search(new ReservationPredicate(Arrays.asList(ReservationStatus.CheckedIn, ReservationStatus.CheckedOut), startDate, endDate), 
				Reservation.class, false);
		
		int millis = 0;
		long startMillis = startDate.getTime();
		long endMillis = endDate.getTime();
		for(Reservation reservation: fulfilledReservations)
			millis += Math.min(endMillis, reservation.getEndDate().getTime()) - Math.max(startMillis, reservation.getStartDate().getTime());
	
		long interval = endDate.getTime() - startDate.getTime();
		long totalTime = totalRoomCount * interval;
		view.message("Percentage of room occupancy: " + String.format("%.2f", ((double) millis / totalTime))+ "%");
		view.message("Number of no show or expired reservations: " + 
				persistence.getCount(new ReservationPredicate(Arrays.asList(ReservationStatus.Expired), startDate, endDate),
				Reservation.class, false));
		view.message("Number of cancelled reservations: " +
				persistence.getCount(new ReservationPredicate(Arrays.asList(ReservationStatus.Cancelled), startDate, endDate),
				Reservation.class, false));
		
		RoomType popular = getMostPopularRoomType(startDate, endDate);
		if(popular != null)
			view.message("Most popular room type: " + popular.getName());
		view.message("");
	}
	
	/**
	 * Prints the rooms with the following status and returns the number of rooms printed.
	 * @param view - A view interface that provides input/output.
	 * @param status - The rooms with this status will be printed out.
	 * @return The number of rooms with the specified status.
	 */
	private int printRoomsWithStatus(View view, RoomStatus status) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		String message = "";
		int count = 0;
		Iterable<Room> vacantRooms = persistence.search(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				return item.getStatus() == status;
			}
			
		}, Room.class, false);
		message = status + " Room(s): ";
		for(Room room: vacantRooms) {
			message += room.getNumber() + " ";
			count++;
		}
		if(count == 0)
			message += "None";
		view.message(message);
		
		return count;
	}
	
	/**
	 * Gets the most popular room type for the specified date range.
	 * @param startDate - Start date to search for most popular room type.
	 * @param endDate - End date to search for most popular room type.
	 * @return A RoomType instance that is the most popular in the specified date range.
	 * @throws Exception
	 */
	private RoomType getMostPopularRoomType(Date startDate, Date endDate) throws Exception {
		RoomType popular = null;
		
		Map<RoomType, Integer> map = new HashMap<RoomType, Integer>();
		Persistence persistence = this.getPersistenceImpl();
		Iterable<Reservation> reservations = persistence.search(new ReservationPredicate(Arrays.asList(ReservationStatus.values()), startDate, endDate), 
				Reservation.class, false);
		
		for(Reservation reservation: reservations) {
			RoomType roomType = reservation.getCriteria().getRoomType();
			if(roomType != null) {
				int count = 1;
				if(map.containsKey(roomType))
					count = count + map.get(roomType);
				
				map.put(roomType, count);
			}
		}
		
		int max = 0;
		for(RoomType roomType: map.keySet()) {
			int count = map.get(roomType);
			if(count > max) {
				max = count;
				popular = roomType;
			}
		}
		
		return popular;
	}
}
