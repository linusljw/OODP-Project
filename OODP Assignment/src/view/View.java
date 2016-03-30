package view;

import java.util.List;
import java.util.Map;

/**
 * View is an abstract base class of all View instances.
 * @author YingHao
 */
public abstract class View {
	private final String title;
	
	public View(String title) {
		this.title = title;
	}
	
	/**
	 * Displays this view.
	 */
	public abstract void display();
	
	/**
	 * Request user to input data for the keys present in this map. Use a LinkedHashMap or any map implementation
	 * that preserves order if order of requesting input is relevant.
	 * @param input - The map to be populated with user input after this method returns.
	 */
	public abstract void input(Map<String, String> input);
	
	/**
	 * Warns user of the invalid fields.
	 * @param invalidFields - A list of invalid fields that requires user's attention.
	 */
	public abstract void error(List<String> invalidFields);
	
	/**
	 * Displays the specified entity.
	 * @param object - The object to be displayed.
	 */
	public abstract void display(Object object);
	
	/**
	 * Displays a list of entity.
	 * @param objList - The list of objects to be displayed.
	 */
	public abstract void display(List<Object> objList);
	
	/**
	 * Display a message.
	 * @param message - The message to be displayed
	 */
	public abstract void message(String message);
	
	/**
	 * Constructs a user bailout message and returns user's choice.
	 * @return A flag indicating if user wants to bailout.
	 */
	public abstract boolean bailout();
	
	/**
	 * Gets the title of the view.
	 * @return A string title for the view.
	 */
	public String getTitle() {
		return title;
	}

}
