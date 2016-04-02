package view;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.Controller;

/**
 * ConsoleView is a View that provides input/output via the console and is backed by a controller implementation.
 * ConsoleView provides methods that allow Controller implementations to notify users of details.
 * @author YingHao
 */
public class ConsoleView extends View {
	public final static String KEY_OPTION = "Option";
	
	private final Controller controller;
	private final Scanner scanner;
	
	/**
	 * ConsoleView constructor.
	 * @param title - The title for this ConsoleView instance.
	 * @param scanner - The scanner object from which to read user input.
	 */
	public ConsoleView(Controller controller, String title, Scanner scanner) {
		super(title);
		this.controller = controller;
		this.scanner = scanner;
	}
	
	/**
	 * Displays this view in the console.
	 */
	public void display() {
		int option;
		
		String title = this.getTitle();
		List<String> options = controller.getOptions();
		do {
			System.out.println("==========" + title + "==========");
			
			// Loop through and display the list of options
			for(int i = 0; i < options.size(); i++)
				System.out.println((i + 1) + ") " + options.get(i));
			
			System.out.println((options.size() + 1) + ") Exit from " + title);
			System.out.println();
			
			do {
				System.out.print("Please select an option: ");
				try {
					option = Integer.parseInt(scanner.nextLine());
				} catch(NumberFormatException e) {
					option = -1;
				}
				
				System.out.println();
				
				if(option < 1 || option > options.size() + 1)
					System.out.println("Invalid selection, please enter a valid value");
				else if(option <= options.size())
					controller.onOptionSelected(this, option - 1);
			} while(option < 1 || option > options.size() + 1);
			
		} while(option != options.size() + 1);
	}

	@Override
	public void input(Map<String, String> input) {
		Iterable<String> keyset = input.keySet();
		
		// Loop through the requested fields and prompt user for input.
		for(String key: keyset) {
			System.out.print("Enter " + key + ": ");
			input.put(key, scanner.nextLine());
		}
		
		System.out.println();
	}

	@Override
	public void error(List<String> invalidFields) {
		System.out.println("One or all of the following fields are invalid: ");
		for(String field: invalidFields)
			System.out.println("\t- " + Character.toUpperCase(field.charAt(0)) + field.substring(1));
		
		System.out.println();
	}

	@Override
	public void display(Object object) {
		System.out.println(object);
	}

	@Override
	public void display(List objList) {
		if(objList.size() > 0)
			// Loop through the list of objects and display.
			for(Object obj: objList)
				display(obj);
		else
			System.out.println("There is no data to be displayed.");
	}
	
	@Override
	public void message(String message) {
		System.out.println(message);
	}
	
	@Override
	public boolean bailout() {
		// Prompt user
		message("Do you want to retry?");
		
		return options(Arrays.asList(Options.Yes, Options.No)) == Options.No;
	}

	@Override
	public <T> T options(List<T> options) {
		T selected = null;
		
		for(int i = 0; i < options.size(); i++)
			System.out.println((i + 1) + ") " + options.get(i).toString());
		
		do {
			System.out.print("Select an option: ");
			try {
				selected = options.get(Integer.parseInt(scanner.nextLine()) - 1);
			} catch(NumberFormatException | IndexOutOfBoundsException e) {
				selected = null;
				error(Arrays.asList(KEY_OPTION));
			}
		} while(selected == null);
		System.out.println();
			
		return selected;
	}
	
}
