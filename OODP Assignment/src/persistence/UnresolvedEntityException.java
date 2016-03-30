package persistence;

import java.lang.reflect.Field;

/**
 * UnresolvedEntityException is an exception class generated during persist operations when 
 * a field is not of type CascadeType.Create for create operations and is unmanaged.
 * @author YingHao
 */
public class UnresolvedEntityException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3118453366537792554L;
	public final Field field;
	public final Entity entity;
	
	/**
	 * UnresolvedEntityException constructor.
	 */
	public UnresolvedEntityException() {
		this.field = null;
		this.entity = null;
	}
	
	/**
	 * UnresolvedEntityException constructor.
	 * @param field - The field that generated contains an unmanaged entity.
	 * @param entity - The unmanaged entity.
	 */
	public UnresolvedEntityException(Field field, Entity entity) {
		super("The field " + field.getName() + " of the " + entity.getClass().getSimpleName() + " instance is not managed.");
		this.field = field;
		this.entity = entity;
	}

}
