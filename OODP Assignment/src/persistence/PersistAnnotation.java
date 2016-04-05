package persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Annotation referenced by the Persistence API to retrieve user defined meta-data.
 * This annotation can be annotated on class-level or field-level with the latter being
 * prioritised by the Persistence API during serialization.<br />
 * The user defined meta-data possible are:
 * <ul>
 * 	<li>persist - Click on {@link PersistAnnotation#persist} for more details.</li>
 * 	<li>cascade - Click on {@link PersistAnnotation#cascade} for more details.</li>
 * 	<li>type - Click on {@link PersistAnnotation#type} for more details.</li>
 * </ul>
 * Example of annotation usage:
 * <code>
 * <pre>
 * 	&#064;PersistAnnotation(cascade = {CascadeType.Create})
 * 	public class ExampleModel extends Entity {
 * 		
 * 		// This field will not be persisted.
 * 		&#064;PersistAnnotation(persist = false)
 * 		private boolean nopersist;
 * 
 * 		// This field will default to no cascade as the field annotation will override
 * 		// the class-level annotation.
 * 		&#064;PersistAnnotation
 * 		private ExampleModel nocascade;
 * 
 * 		// This field will inherit its parent's cascade policy.
 * 		private ExampleModel cascadecreate;
 * 
 * 		// This field will be persisted as ExampleModel and any operations performed on its parent will be cascaded.
 * 		&#064;PersistAnnotation(
 * 			type = ExampleModel.class,
 * 			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
 * 		)
 * 		private List&lt;ExampleModel&gt; cascadeall;
 * 
 * 	}
 * </pre>
 * </code>
 * @author YingHao
 *
 */
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistAnnotation {
	
	/**
	 * Indicates if a specific field can be persisted.
	 * This declaration can only be used in field declaration.
	 * This declaration takes precedence over cascade declaration, that is if persist is false any cascade operations
	 * specified will not be considered.
	 * Defaults to true.
	 */
	boolean persist() default true;
	
	/**
	 * Indicates the possible cascade operations of the fields belonging to a class (class-level) or a specific field (field-level).
	 * Defaults to no cascade operations.
	 */
	CascadeType[] cascade() default {};
	
	/**
	 * Indicates the type of a specific field. 
	 * This declaration is required for fields containing a {@link Collection}.
	 * This has no effect for class-level declaration and fields that are not of Collection types.
	 */
	Class<? extends Entity> type() default Entity.class;
}
