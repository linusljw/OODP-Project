package controller;

import persistence.Persistence;
import view.View;

/**
 * An abstract base class for all Controller classes that requires access to Persistence API.
 * @author YingHao
 */
public abstract class PersistenceController implements Controller {
	private final Persistence persistence;
	
	/**
	 * PersistenceController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 */
	public PersistenceController(Persistence persistence) {
		this.persistence = persistence;
	}
	
	/**
	 * Gets the Persistence API implementation that was associated with this instance.
	 * @return A Persistence implementation.
	 */
	protected Persistence getPersistenceImpl() {
		return this.persistence;
	}
	
	/**
	 * Subclasses of PersistenceController should implement this method instead of 
	 * {@link PersistenceController#onOptionSelected(View, int)}. For more information of
	 * what is to be implemented, please refer to {@link Controller#onOptionSelected(View, int)}
	 * @param view - A view interface that provides input/output.
	 * @param option - The option that is selected, the option value starts from 0.
	 */
	protected abstract void safeOnOptionSelected(View view, int option) throws Exception;

	/**
	 * Subclasses of PersistenceController should implement {@link PersistenceController#safeOnOptionSelected} instead.
	 * This method has been implemented as a wrapper to catch unhandled exceptions and displaying file operation error
	 * to the user.
	 */
	@Override
	public void onOptionSelected(View view, int option) {
		try {
			this.safeOnOptionSelected(view, option);
		} catch(Exception e) {
			view.message("An error occurred while performing file operations, please try again later.");
		}
	}

}
