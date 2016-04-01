import java.io.File;
import java.util.Scanner;

import controller.NavigationController;
import controller.management.GuestController;
import controller.management.MenuItemController;
import controller.management.RoomTypeController;
import persistence.file.text.FilePersistence;
import view.ConsoleView;

public class Test {
	
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		FilePersistence persistence;
		try {
			persistence = new FilePersistence(new File("persistence.cfg"));
			
			NavigationController managementController = new NavigationController();
			managementController.addView(new ConsoleView(new GuestController(persistence), "Manage Guest", sc));
			managementController.addView(new ConsoleView(new MenuItemController(persistence), "Manage Menu Item", sc));
			managementController.addView(new ConsoleView(new RoomTypeController(persistence), "Manage Room Type", sc));
			
			ConsoleView managementView = new ConsoleView(managementController, "Management View", sc);
			
			NavigationController hrsController = new NavigationController();
			
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
