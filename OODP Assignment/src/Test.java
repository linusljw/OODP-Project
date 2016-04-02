import java.io.File;
import java.util.Scanner;

import controller.NavigationController;
import controller.hrs.CheckInCheckOutController;
import controller.hrs.ReservationController;
import controller.management.*;
import persistence.file.text.FilePersistence;
import view.ConsoleView;

public class Test {
	
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		FilePersistence persistence;
		try {
			persistence = new FilePersistence(new File("persistence.cfg"));
			
			RoomTypeController rtController = new RoomTypeController(persistence);
			GuestController gController = new GuestController(persistence);
			ReservationController rController = new ReservationController(persistence, gController);
			
			NavigationController managementController = new NavigationController();
			managementController.addView(new ConsoleView(new GuestController(persistence), "Manage Guest", sc));
			managementController.addView(new ConsoleView(new MenuItemController(persistence), "Manage Menu Item", sc));
			managementController.addView(new ConsoleView(new RoomController(persistence, rtController), "Manage Room", sc));
			managementController.addView(new ConsoleView(rtController, "Manage Room Type", sc));
			
			ConsoleView managementView = new ConsoleView(managementController, "Management View", sc);
			
			NavigationController hrsController = new NavigationController();
			hrsController.addView(new ConsoleView(rController, "Reservation System", sc));
			hrsController.addView(new ConsoleView(new CheckInCheckOutController(persistence, rController), "Check-in/Check-out", sc));
			
			ConsoleView hrsView = new ConsoleView(hrsController, "Hotel Reservation System", sc);
			
			NavigationController mainNav = new NavigationController();
			mainNav.addView(managementView);
			mainNav.addView(hrsView);
			
			ConsoleView mainView = new ConsoleView(mainNav, "Main View", sc);
			mainView.display();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
