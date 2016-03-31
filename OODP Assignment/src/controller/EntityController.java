package controller;

import java.util.Arrays;
import java.util.List;
import persistence.Persistence;
import view.View;

/**
 * An abstract base class for all controller classes that manages the lifetime of an Entity class. 
 * @author YingHao
 *
 * @param <T>
 */
public abstract class EntityController<T> extends PersistenceController {

	public EntityController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList(
					"Create " + this.getEntityName().toLowerCase(),
					"Retrieve/Search " + this.getEntityName().toLowerCase(),
					"Update " + this.getEntityName().toLowerCase(),
					"Delete " + this.getEntityName().toLowerCase()
				);
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			create(view);
			break;
		case 1:
			retrieve(view);
			break;
		case 2:
			update(view);
			break;
		case 3:
			delete(view);
			break;
		}
	}
	
	/**
	 * Gets the entity name of the entity managed by this Controller instance.
	 * This entity name will be used to display the available options. <br />
	 * For example, if entity name is "Guest Profile" <br />
	 * The following options will be displayed for user to select: <br />
	 * <ol>
	 * 	<li>Create guest profile</li>
	 * 	<li>Retrieve/Search guest profile</li>
	 * 	<li>Update guest profile</li>
	 * 	<li>Delete guest profile</li>
	 * </ol>
	 * @return
	 */
	protected abstract String getEntityName();
	
	/**
	 * Prompts the user to enter relevant information required and creates a new
	 * Entity instance.
	 * @param view - A view interface that provides input/output.
	 */
	protected abstract void create(View view) throws Exception;
	
	/**
	 * Prompts the user to enter relevant information required to retrieve and display
	 * entity instances.
	 * @param view - A view interface that provides input/output.
	 */
	protected abstract void retrieve(View view) throws Exception;
	
	/**
	 * Prompts the user to enter relevant information required and updates an
	 * Entity instance.
	 * @param view - A view interface that provides input/output.
	 */
	protected abstract void update(View view) throws Exception;
	
	/**
	 * Prompts the user to enter relevant information required and deletes an
	 * Entity instance.
	 * @param view - A view interface that provides input/output.
	 */
	protected abstract void delete(View view) throws Exception;
	
	/**
	 * Prompts the user to select an Entity instance managed by this EntityController.
	 * @param view - A view interface that provides input/output.
	 * @return Entity selected by the user.
	 */
	public abstract T select(View view) throws Exception;

}
