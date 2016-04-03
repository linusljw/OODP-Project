package controller.report;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import controller.PersistenceController;
import model.Room;
import model.RoomStatus;
import persistence.Persistence;
import persistence.Predicate;
import view.Options;
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
		switch(option) {
		case 0:
			viewReportForToday(view);
			break;
		default:
			viewReportForRange(view, option);
			break;
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
		
		String message;
		int occupiedRoomCount = 0;
		int vacantRoomCount = 0;
		int maintenanceRoomCount = 0;
		
		Persistence persistence = this.getPersistenceImpl();
		Iterable<Room> occupiedRooms = persistence.search(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				return item.getStatus() == RoomStatus.Occupied;
			}
			
		}, Room.class, false);
		message = "Occupied Room(s): ";
		for(Room room: occupiedRooms) {
			message += room.getNumber() + " ";
			occupiedRoomCount++;
		}
		view.message(message);
		
		Iterable<Room> vacantRooms = persistence.search(new Predicate<Room>() {

			@Override
			public boolean test(Room item) {
				return item.getStatus() == RoomStatus.Vacant;
			}
			
		}, Room.class, false);
		message = "Vacant Room(s): ";
		for(Room room: vacantRooms) {
			message += room.getNumber() + " ";
			vacantRoomCount++;
		}
		view.message(message);
		
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
	 * @param option - The option of the date range selected. This depends on {@link #getOptions()}.
	 */
	private void viewReportForRange(View view, int option) {
		
	}
}
