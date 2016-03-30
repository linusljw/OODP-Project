package controller;

import java.util.List;
import view.View;

/**
 * Controller is an abstract base class for all controller classes.
 * @author YingHao
 */
public abstract class Controller {
	
	/**
	 * Callback method to notify the {@link View} of the available options for this controller.
	 * @return A list of possible options.
	 */
	public abstract List<String> getOptions();
	
	/**
	 * Callback method for {@link View} to inform Controller about a selection.
	 * @param view - The view instance that triggered this callback.
	 * @param option - The option that is selected, the option value starts from 0.
	 */
	public abstract void onOptionSelected(View view, int option);

}
