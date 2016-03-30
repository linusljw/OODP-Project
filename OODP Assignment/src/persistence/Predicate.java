package persistence;

/**
 * Represents a predicate condition.
 * @author YingHao
 * @param <T>
 */
public interface Predicate<T> {
	
	/**
	 * Test if a specified item passes the predicate condition.
	 * @param item
	 * @return Success status
	 */
	public boolean test(T item);

}
