package persistence;

import java.util.Iterator;

/**
 * EntityIterator represents a pointer to an open resource with a collection of entity.
 * Close this EntityIterator when not in use to prevent a resource leak.
 * @author YingHao
 *
 * @param <T>
 */
public interface EntityIterator<T> extends Iterator<T>, AutoCloseable {

}
