package persistence.file.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import persistence.Entity;
import persistence.EntityIterator;
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
public class FileEntityIterator<T extends Entity> implements EntityIterator<T> {
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
	public FileEntityIterator(FilePersistence persistence, File file, Predicate predicate, boolean loadR) throws Exception {
		this.persistence = persistence;
		try {
			this.reader = new BufferedReader(new FileReader(file));
		} catch(FileNotFoundException e) {
			this.reader = null;
		}
		this.predicate = predicate;
		this.loadR = loadR;
	}

	@Override
	public boolean hasNext() {
		this.entity = null;
		
		if(this.entity == null && reader != null) {
			try {
				String entityString = null;
				while(this.entity == null && ((entityString = reader.readLine()) != null)) {
					T next = persistence.deserialize(entityString, loadR, false);
					if(predicate == null || predicate.test(next)) {
						if(!loadR)
							this.entity = persistence.deserialize(entityString, true, false);
						else
							this.entity = next;
					}
				}
				
				// Reached the end of the file, close iterator.
				if(this.entity == null && entityString == null)
					this.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return this.entity != null;
	}

	@Override
	public T next() {
		T entity = this.entity;
		this.entity = null;
		
		return entity;
	}

	@Override
	public void close() throws Exception {
		reader.close();
		entity = null;
	}

}
