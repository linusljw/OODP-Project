package persistence.file.text;

import java.io.File;
import persistence.Entity;
import persistence.Predicate;

/**
 * EntityIterable fulfils the contract of an Iterable by providing an EntityIterator as 
 * a pointer to an open file. The iterator obtained from this instance is represents a file connection, do close it
 * when not in use to prevent a resource leak.
 * @author YingHao
 *
 * @param <T>
 * @see EntityIterator
 */
public class EntityIterable<T extends Entity> implements Iterable<T> {
	private final FilePersistence persistence;
	private final File file;
	private final Predicate<T> predicate;
	private final boolean loadR;
	
	/**
	 * EntityIterable constructor.
	 * @param persistence - The persistence instance to be used for deserializing entity instances.
	 * @param file - The file to be deserialized.
	 * @param predicate - The predicate for entity to pass through to determine whether they are accepted or rejected.
	 * @param loadR - Indicates if all entity references should be loaded during predicate evaluation or after predicate evaluation.
	 */
	public EntityIterable(FilePersistence persistence, File file, Predicate predicate, boolean loadR) {
		this.persistence = persistence;
		this.file = file;
		this.predicate = predicate;
		this.loadR = loadR;
	}

	@Override
	public EntityIterator<T> iterator() {
		try {
			return new EntityIterator<T>(persistence, file, predicate, loadR);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
