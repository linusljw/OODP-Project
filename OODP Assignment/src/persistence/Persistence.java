package persistence;

/**
 * Persistence is an interface representing a generic contract for persisting an Entity.
 * @author YingHao
 */
public interface Persistence {
	
	/**
	 * Creates an entity.
	 * @param entity - Entity to be persisted.
	 * @param type - Type of the entity to be persisted.
	 * @return A managed entity representing the persisted version of the entity argument passed into this method.
	 */
	public <T extends Entity> T create(T entity, Class<T> type) throws Exception;
	
	/**
	 * Updates an entity.
	 * @param entity - Entity to be updated. Must be a managed entity.
	 * @param type - Type of the entity to be updated.
	 * @return Success status of the update operation.
	 */
	public <T extends Entity> boolean update(T entity, Class<T> type) throws Exception;
	
	/**
	 * Deletes an entity.
	 * @param entity - Entity to be deleted. Must be a managed entity.
	 * @param type - Type of the entity to be deleted.
	 * @return Success status of the delete operation.
	 */
	public <T extends Entity> boolean delete(T entity, Class<T> type) throws Exception;
	
	/**
	 * Searches through the given type and applying {@link Predicate#test(Object)} and returning matching entities.
	 * @param predicate - Predicate indicating which entities to be accepted or rejected. Can be null, of which the entire list belonging to the specified type is retrieved.
	 * @param type - Type of entity to be searched.
	 * @param loadBeforePredicate - Indicates if all entity references should be loaded during predicate evaluation or after predicate evaluation.
	 * @return An {@link Iterable} that represents the managed entities that matches the given predicate.
	 */
	public <T extends Entity> Iterable<T> search(Predicate<T> predicate, Class<T> type, boolean loadBeforePredicate) throws Exception;
	
	/**
	 * Searches through the given type and applying {@link Predicate#test(Object)} and returning the number of matching entities.
	 * @param predicate - Predicate indicating which entities to be accepted or rejected. Can be null, of which the entire list belonging to the specified type is counted.
	 * @param type - Type of entity to be searched.
	 * @param loadBeforePredicate - Indicates if all entity references should be loaded during predicate evaluation or after predicate evaluation.
	 * @return long value indicating the number of entities that matches the predicate.
	 */
	public <T extends Entity> long getCount(Predicate<T> predicate, Class<T> type, boolean loadBeforePredicate) throws Exception;
	
	/**
	 * Retrieves a given entity via its unique identifier.
	 * @param id - Unique identifier
	 * @param type - Type of the entity to be retrieved.
	 * @return A managed entity with the unique identifier or null if it does not exists.
	 */
	public <T extends Entity> T retrieveByID(long id, Class<T> type) throws Exception;

}
