package controller;

import java.util.ArrayList;
import java.util.List;

import view.View;

/**
 * NavigationController implements Controller to control the navigation flow of views.
 * @author YingHao
 */
public class NavigationController implements Controller {
	private final List<View> views;
	
	public NavigationController() {
		this.views = new ArrayList<View>();
	}
	
	/**
	 * Adds a view.
	 * @param view
	 */
	public void addView(View view) {
		this.views.add(view);
	}
	
	/**
	 * Removes a view.
	 * @param view
	 */
	public void removeView(View view) {
		this.views.remove(view);
	}

	@Override
	public List<String> getOptions() {
		List<String> options = new ArrayList<String>();
		for(View view: this.views)
			options.add("Navigate to " + view.getTitle());
		
		return options;
	}

	@Override
	public void onOptionSelected(View view, int option) {
		this.views.get(option).display();
	}

}
