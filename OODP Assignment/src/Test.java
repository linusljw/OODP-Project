import java.io.File;
import java.util.Scanner;

import controller.GuestController;
import controller.MenuItemController;
import controller.NavigationController;
import controller.RoomTypeController;
import persistence.file.text.FilePersistence;
import view.ConsoleView;

public class Test {
	
	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		NavigationController controller = new NavigationController();
		FilePersistence persistence;
		try {
			persistence = new FilePersistence(new File("persistence.cfg"));
			
			controller.addView(new ConsoleView(new GuestController(persistence), "Manage Guest", sc));
			controller.addView(new ConsoleView(new MenuItemController(persistence), "Manage Menu Item", sc));
			controller.addView(new ConsoleView(new RoomTypeController(persistence), "Manage Room Type", sc));
			
			ConsoleView mainView = new ConsoleView(controller, "Main View", sc);
			
			mainView.display();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
