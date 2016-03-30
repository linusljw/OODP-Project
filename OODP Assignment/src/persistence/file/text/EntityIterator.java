package persistence.file.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import persistence.Entity;
import persistence.Predicate;

/**
 * EntityIterator represents a pointer to an open file with a collection of entity.
 * EntityIterator lazily loads Entity matching the given predicate, ensuring that unused
 * entity will not be loaded in the process. Close this EntityIterator when not in use to prevent
 * a resource leak.
 * @author YingHao
 *
 * @param <T>
 */
public class EntityIterator<T extends Entity> implements Iterator<T>, AutoCloseable {
	private final FilePersistence persistence;
	private final Predicate<T> predicate;
	private final boolean loadR;
	private BufferedReader reader;
	private T entity;
	
	/**
	 * EntityIterator constructor.
	 * @param persistence - The persistence instance to be used for deserializing entity instances.
	 * @param file - The file to be deserialized.
	 * @param predicate - The predicate for entity to pass through to determine whether they are accepted or rejected.
	 * @param loadR - Indicates if all entity references should be loaded during predicate evaluation or after predicate evaluation.
	 * @throws IOException 
	 */
	public EntityIterator(FilePersistence persistence, File file, Predicate predicate, boolean loadR) throws Exception {
		this.persistence = persistence;
		try {
			this.reader = new BufferedReader(new FileReader(file));
		} catch(FileNotFoundException e) {
			this.reader = null;
		}
		this.predicate = predicate;
		this.loadR = loadR;
		
		load();
	}
	
	/**
	 * Make an attempt to load the next entity into our buffer.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 * @throws NumberFormatException 
	 */
	private void load() throws NumberFormatException, IllegalArgumentException, Exception {
		this.entity = null;
		
		String entityString = null;
		while(this.entity == null && ((entityString = reader.readLine()) != null)) {
			T next = persistence.deserialize(entityString, loadR, false);
			if(predicate == null || predicate.test(next)) {
				if(!loadR)
					this.entity = persistence.deserialize(entityString, true, true);
				else
					this.entity = next;
			}
		}
		
		// Reached the end of the file, close iterator.
		if(this.entity == null && entityString == null)
			this.close();
	}

	@Override
	public boolean hasNext() {
		return reader != null && entity != null;
	}

	@Override
	public T next() {
		T next = this.entity;
		try {
			if(next != null)
				load();
		} catch (Exception e) {
			throw new RuntimeException("Error loading next entity into buffer", e);
		}
		
		return next;
	}

	@Override
	public void close() throws Exception {
		reader.close();
		entity = null;
	}

}
