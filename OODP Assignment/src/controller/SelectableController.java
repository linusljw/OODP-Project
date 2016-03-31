package controller;

import persistence.Entity;
import view.View;

/**
 * SelectableController is an interface representing a generic contract for all Controller classes, that provide a method
 * for external classes to select an {@link Entity} using the Controller.
 * @author YingHao
 *
 * @param <T>
 */
public interface SelectableController<T extends Entity> extends Controller {
	
	/**
	 * Call this method to prompt the user to select a specific entity managed by this Controller class.
	 * @param view - The view instance that triggered this callback.
	 * @return Entity selected by the user.
	 */
	public T select(View view) throws Exception;

}
