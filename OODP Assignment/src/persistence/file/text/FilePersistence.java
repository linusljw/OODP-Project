package persistence.file.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import persistence.CascadeType;
import persistence.Entity;
import persistence.PersistAnnotation;
import persistence.Persistence;
import persistence.Predicate;
import persistence.UnresolvedEntityException;

/**
 * FilePersistence is an implementation of Persistence interface providing a persistence contract
 * between the application code and the underlying file system.
 * @author YingHao
 */
public class FilePersistence implements Persistence {
	public final static String KEY_FIELD_DELIMITER = "field-delimiter";
	public final static String KEY_KV_DELIMITER = "key-value-delimiter";
	public final static String KEY_ARRAY_DELIMITER = "array-delimiter";
	public final static String KEY_DATA_DIR = "data-directory";
	public final static String KEY_DATA_EXT = "data-ext";
	public final static String KEY_TMP_DIR = "tmp-directory";
	public final static String KEY_TMP_EXT = "tmp-ext";
	public final static String KEY_AUTO_ID = "{type}.auto-id";
	public final static String AUTO_ID_TYPE_REGEX = "{type}";
	public final static Properties DEFAULT_CONFIGURATION;
	
	/**
	 * Static initializer initializes class constants during classloader loading
	 */
	static {
		DEFAULT_CONFIGURATION = new Properties();
		DEFAULT_CONFIGURATION.setProperty(KEY_DATA_DIR, "data");
		DEFAULT_CONFIGURATION.setProperty(KEY_TMP_DIR, "tmp");
		DEFAULT_CONFIGURATION.setProperty(KEY_DATA_EXT, ".data");
		DEFAULT_CONFIGURATION.setProperty(KEY_TMP_EXT, ".tmp");
		DEFAULT_CONFIGURATION.setProperty(KEY_FIELD_DELIMITER, "|");
		DEFAULT_CONFIGURATION.setProperty(KEY_KV_DELIMITER, ":");
		DEFAULT_CONFIGURATION.setProperty(KEY_ARRAY_DELIMITER, ";");
	}
	
	private final File configurationFile;
	private final Properties configuration;
	private final Map<Class<?>, Field[]> fCache;
	private final Map<Field, PersistAnnotation> pmCache;
	private final Map<Class<?>, Map<Long, SoftReference<Entity>>> entityCache;
	
	/**
	 * FilePersistence constructor.
	 * @param configurationFile - The configuration file for this FilePersistence instance.
	 * @throws Exception 
	 */
	public FilePersistence(File configurationFile) throws Exception {
		this.configurationFile = configurationFile;
		this.configuration = new Properties(DEFAULT_CONFIGURATION);
		this.fCache = new HashMap<Class<?>, Field[]>();
		this.pmCache = new HashMap<Field, PersistAnnotation>();
		this.entityCache = new HashMap<Class<?>, Map<Long, SoftReference<Entity>>>();
		
		AutoCloseable stream = null;
		try {
			if(this.configurationFile.createNewFile()) {
				FileOutputStream out = new FileOutputStream(this.configurationFile);
				stream = out;
				
				// Initializes configuration file with the defaults
				DEFAULT_CONFIGURATION.store(out, null);
			}
			else {
				FileInputStream in = new FileInputStream(this.configurationFile);
				stream = in;
				
				// Loads configurations from the configuration file
				this.configuration.load(in);
			}
		} finally {
			if(stream != null)
				stream.close();
		}
		
		File dataDir = new File(this.configuration.getProperty(KEY_DATA_DIR));
		File tmpDir = new File(this.configuration.getProperty(KEY_TMP_DIR));
		
		dataDir.mkdir();
		tmpDir.mkdir();
	}
	
	/**
	 * Gets the data file for the specified type.
	 * @param type
	 * @return Data file for the specified type.
	 */
	private <T extends Entity> File getDataFile(Class<T> type) {
		return new File(this.configuration.getProperty(KEY_DATA_DIR) + 
							File.separator + type.getName().toLowerCase().replace(".", "-") +
							this.configuration.getProperty(KEY_DATA_EXT));
	}
	
	/**
	 * Gets the temporary file for the specified type.
	 * @param type
	 * @return Temporary file for the specified type.
	 */
	private <T extends Entity> File getTemporaryFile(Class<T> type) {
		return new File(this.configuration.getProperty(KEY_TMP_DIR) +
							File.separator + type.getName().toLowerCase().replace(".", "-") +
							this.configuration.getProperty(KEY_TMP_EXT));
	}
	
	/**
	 * Gets the fields declared for the specified type.
	 * @param type
	 * @return An array of fields.
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private <T> Field[] getFieldsForType(Class<T> type) throws NoSuchMethodException, SecurityException {
		Field[] fields;
		
		if(fCache.containsKey(type)) {
			// Attempts to get the fields from cache
			fields = fCache.get(type);
		}
		else {
			// Build the cache for the fields for the specified type
			Field[] local = type.getDeclaredFields();
			
			Class<?> supertype = type.getSuperclass();
			if(supertype == null)
				fields = local;
			else {
				// Get fields from superclass
				Field[] inherited = getFieldsForType(supertype);
				
				// Store the fields in a new array
				fields = new Field[inherited.length + local.length];
				
				System.arraycopy(inherited, 0, fields, 0, inherited.length);
				System.arraycopy(local, 0, fields, inherited.length, local.length);
				
				// Sets the retrieved fields to be accessible via reflection
				for(Field field: fields)
					field.setAccessible(true);
				
				// Cache the retrieved field array
				fCache.put(type, fields);
			}
		}
		
		return fields;
	}
	
	/**
	 * Gets the persistence metadata for the specified field. 
	 * Defaults to a default PersistAnnotation instance if none is present.
	 * @param field
	 * @return
	 */
	private PersistAnnotation getFieldPersistenceMetadata(Field field) {
		PersistAnnotation meta = null;
		
		if(pmCache.containsKey(field)) {
			// Retrieves metadata from cache when it is present.
			meta = pmCache.get(field);
		}
		else {
			// Attempts to retrieve metadata from field followed by class hierarchy.
			meta = field.getAnnotation(PersistAnnotation.class);
			if(meta == null)
				meta = field.getDeclaringClass().getAnnotation(PersistAnnotation.class);
			
			// Store metadata to cache
			pmCache.put(field, meta);
		}
		
		return meta;
	}
	
	/**
	 * Gets an entity from cache.
	 * @param type - The type of entity to retrieve.
	 * @param id - The identifier for the entity.
	 * @return A managed entity residing in the cache.
	 */
	private <T extends Entity> T getEntityFromCache(Class<T> type, long id) {
		T entity = null;
		
		if(this.entityCache.containsKey(type)) {
			Map<Long, SoftReference<Entity>> idMap = this.entityCache.get(type);
			
			if(idMap.containsKey(id)) {
				// Attempts to retrieve entity from soft reference
				entity = (T) idMap.get(id).get();
				// Remove soft reference from cache if it gets invalid
				if(entity == null)
					idMap.remove(id);
			}
		}
		
		return entity;
	}
	
	/**
	 * Sets the entity to cache.
	 * @param type - The type of entity to set.
	 * @param entity - The entity to be added into cache.
	 */
	private <T extends Entity> void setEntityToCache(Class<T> type , T entity) {
		Map<Long, SoftReference<Entity>> idMap;
		
		if(this.entityCache.containsKey(type))
			idMap = this.entityCache.get(type);
		else {
			// Creates a new map for the specific type.
			idMap = new HashMap<Long, SoftReference<Entity>>();
			this.entityCache.put(type, idMap);
		}
		
		// Adds a new soft reference for the entity into cache.
		idMap.put(entity.getIdentifier(), new SoftReference<Entity>(entity));
	}
	
	/**
	 * Serializes a specific object into the given StringBuilder.
	 * @param type - The type to serialize.
	 * @param metadata - The persistence metadata.
	 * @param object - The object to serialize.
	 * @param builder - StringBuilder to serialize the field into. Must not be null.
	 * @return
	 * @throws Exception 
	 */
	private StringBuilder serialize(Class type, PersistAnnotation metadata, Object value, StringBuilder builder) throws Exception {
		if(type.isPrimitive() || type.equals(String.class) || Enum.class.isAssignableFrom(type)) {
			if(type.equals(String.class))
				value = ((String)value).trim();
			builder.append(value);
		}
		else if(Date.class.isAssignableFrom(type))
			builder.append(((Date) value).getTime());
		else if(Entity.class.isAssignableFrom(type)) {
			// Persisting an entity reference
			Entity reference = (Entity) value;
			
			if(reference.isManaged()) {
				// Cascade as update if reference is already managed and metadata contains a
				// CascadeType.Update option
				if(CascadeType.cascade(metadata.cascade(), CascadeType.Update))
					update(reference, type);
			}
			else {
				// Cascade as create if reference is not managed and metadata contains a
				// CascadeType.Create option
				if(CascadeType.cascade(metadata.cascade(), CascadeType.Create))
					create(reference, type);
				else
					// Unable to persist Entity with unmanaged references
					throw new UnresolvedEntityException();
			}
			
			// Persist identifier of Entity reference
			builder.append(reference.getIdentifier());
		}
		else if(type.isArray() || List.class.isAssignableFrom(type)) {
			String arrDelimiter = this.configuration.getProperty(KEY_ARRAY_DELIMITER);
			Class componentType = type.isArray()? type.getComponentType():
													metadata.type();
			
			// Cast the references as a List
			List items = null;
			if(type.isArray())
				items = Arrays.asList((Object[]) value);
			else
				items = (List) value;
			
			for(Object item: items)
				if(item != null)
					this.serialize(componentType, metadata, item, builder).append(arrDelimiter);
		
			// Remove trailing array delimiter
			builder.setLength(builder.length() - arrDelimiter.length());
		}
		
		return builder;
	}
	
	/**
	 * Serializes an Entity.
	 * @param entity - The entity to serialize.
	 * @return
	 * @throws Exception 
	 */
	private <T extends Entity> StringBuilder serialize(T entity) throws Exception {
		Class<?> actualType = entity.getClass();
		StringBuilder builder = new StringBuilder(actualType.getName());
		
		Field[] fields = this.getFieldsForType(actualType);
		String fieldDelimiter = this.configuration.getProperty(KEY_FIELD_DELIMITER);
		String kvDelimiter = this.configuration.getProperty(KEY_KV_DELIMITER);
		// Loop through all the fields and serialize
		for(Field field: fields) {
			PersistAnnotation metadata = this.getFieldPersistenceMetadata(field);
			Object value = field.get(entity);
			try {
				if(metadata.persist() && value != null) {
					builder.append(fieldDelimiter);
					builder.append(field.getName()).append(kvDelimiter);
					this.serialize(field.getType(), metadata, value, builder);
				}
			} catch(UnresolvedEntityException e) {
				// Add appropriate information for the exception generated
				throw new UnresolvedEntityException(field, entity);
			}
		}
		
		return builder;
	}
	
	/**
	 * Deserializes the specified string data into the field for the specified entity instance.
	 * @param type - The type to deserialize into.
	 * @param metadata - The persistence metadata.
	 * @param valueString - The string containing the serialized value.
	 * @param loadR - A flag indicating if fields containing entity references are to be loaded.
	 * @return An entity instance representing the serialized data.
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private Object deserialize(Class type, PersistAnnotation metadata, String valueString, boolean loadR) throws NumberFormatException, Exception {
		Object value = null;
		
		if(type.equals(String.class))
			value = valueString;
		else if(type.equals(Byte.TYPE) || type.equals(Byte.class))
			value = Byte.parseByte(valueString);
		else if(type.equals(Short.TYPE) || type.equals(Short.class))
			value = Short.parseShort(valueString);
		else if(type.equals(Integer.TYPE) || type.equals(Integer.class))
			value = Integer.parseInt(valueString);
		else if(type.equals(Long.TYPE) || type.equals(Long.class))
			value = Long.parseLong(valueString);
		else if(type.equals(Float.TYPE) || type.equals(Float.class))
			value = Float.parseFloat(valueString);
		else if(type.equals(Double.TYPE) || type.equals(Double.class))
			value = Double.parseDouble(valueString);
		else if(type.equals(Boolean.TYPE) || type.equals(Boolean.class))
			value = Boolean.parseBoolean(valueString);
		else if(type.equals(Character.TYPE) || type.equals(Character.class))
			value = valueString.charAt(0);
		else if(Date.class.isAssignableFrom(type))
			value = new Date(Long.parseLong(valueString));
		else if(Enum.class.isAssignableFrom(type))
			value = Enum.valueOf(type, valueString);
		else if(loadR) {
			if(Entity.class.isAssignableFrom(type)) {
				long id = Long.parseLong(valueString);
				
				// Attempts to load entity from cache and will retrieve from data file as a fallback
				synchronized(this.entityCache) {
					value = this.getEntityFromCache(type, id);
					if(value == null)
						value = this.retrieveByID(id, type);
				}
			}
			else if(type.isArray() || List.class.isAssignableFrom(type)) {
				Class componentType = type.isArray()? type.getComponentType():
														metadata.type();
				
				// Split the data string into the array elements
				String[] arrString = valueString.split(Pattern.quote(this.configuration.getProperty(KEY_ARRAY_DELIMITER)));
				if(!Entity.class.isAssignableFrom(componentType) || loadR) {
					Object[] array = (Object[]) Array.newInstance(componentType, arrString.length);
					// Loop through the array and deserialize each component
					for(int i = 0; i < array.length; i++)
						array[i] = deserialize(componentType, metadata, arrString[i], loadR);
					
					// Cast to appropriate type
					if(type.isArray())
						value = array;
					else
						value = Arrays.asList(array);
				}
			}
		}
		
		return value;
	}
	
	/**
	 * Deserializes the specified string data into an entity instance.
	 * @param entityString - The string containing the serialized entity instance.
	 * @param loadR - A flag indicating if fields containing entity references are to be loaded.
	 * @param partial - A flag indicating if the deserialization should be partial and only load null fields in the specified entity.
	 * @return An entity instance representing the serialized data.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 * @throws NumberFormatException 
	 */
	<T extends Entity> T deserialize(String entityString, boolean loadR, boolean partial) throws NumberFormatException, IllegalArgumentException, Exception {
		String[] arrString = entityString.split(Pattern.quote(this.configuration.getProperty(KEY_FIELD_DELIMITER)));
		Class type = Class.forName(arrString[0]);
		Field[] fields = this.getFieldsForType(type);
		
		// Generate a mapping of key to value
		Map<String, String> kvMap = new HashMap<String, String>();
		for(int i = 1; i < arrString.length; i++) {
			String[] kvPair = arrString[i].split(Pattern.quote(this.configuration.getProperty(KEY_KV_DELIMITER)));
			kvMap.put(kvPair[0], kvPair[1]);
		}
		
		long id = Long.parseLong(kvMap.get("_id"));
		// Search cache for entity or create a new entity in cache
		T entity = null;
		synchronized(this.entityCache) {
			entity = (T) this.getEntityFromCache(type, id);
			if(entity == null) {
				Field idField = this.getFieldsForType(Entity.class)[0];
				
				// Enable no-args constructor
				Constructor<T> constructor = type.getDeclaredConstructor();
				constructor.setAccessible(true);
				entity = (T) constructor.newInstance();
				idField.set(entity, id);
				
				this.setEntityToCache(type, entity);
			}
		}
		
		// Loop through all fields and attempt to initialize them if they are present in kvMap
		for(Field field: fields) {
			if(kvMap.containsKey(field.getName())) {
				if(!partial || field.get(entity) == null) {
					PersistAnnotation metadata = this.getFieldPersistenceMetadata(field);
					field.set(entity, this.deserialize(field.getType(), metadata, kvMap.get(field.getName()), loadR));
				}
			}
		}
			
		return entity;
	}

	@Override
	public <T extends Entity> T create(T entity, Class<T> type) throws Exception {
		// Retrieve last auto generated identifier and increment it to get identifier for new entity
		Field idField = getFieldsForType(Entity.class)[0];
		String idKey = KEY_AUTO_ID.replace(AUTO_ID_TYPE_REGEX, type.getName().toLowerCase());
		long identifier = Long.parseLong(this.configuration.getProperty(idKey, Long.toString(0))) + 1;
		idField.set(entity, identifier);
		
		// Obtain a reference to the data file and attempts to create it.
		File dataFile = this.getDataFile(type);
		dataFile.createNewFile();
		
		// Obtain a writer for the data file
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.getDataFile(type), true));
		try {
			// Writes the serialized entity into the data file
			writer.write(this.serialize(entity).toString());
			writer.newLine();
		} catch(IOException e) {
			// Unmanage entity and rethrow exception
			idField.set(entity, Long.MIN_VALUE);
			throw e;
		} finally {
			writer.close();
		}
		
		// Save auto generated identifier in configuration file
		this.configuration.setProperty(idKey, Long.toString(identifier));
		// Obtain output stream for configuration file and save configurations
		FileOutputStream out = new FileOutputStream(this.configurationFile);
		try {
			this.configuration.store(out, null);
		} finally {
			out.close();
		}
		
		return entity;
	}

	@Override
	public <T extends Entity> boolean update(T entity, Class<T> type) throws Exception {
		boolean success = false;
		
		if(entity.isManaged()) {
			// Get references to data and temporary file for the specified type
			File dataFile = this.getDataFile(type);
			File tmpFile = this.getTemporaryFile(type);
			
			// Move and rename data file to temporary file
			Files.move(dataFile.toPath(), tmpFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			
			// Create new file and write data from temporary file to new file. Replace entity matching the specified
			// entity's identifier with serialized data.
			dataFile.createNewFile();
			BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
			try {
				String entityString = null;
				while((entityString = reader.readLine()) != null) {
					long _id = Long.parseLong(entityString.split(Pattern.quote(this.configuration.getProperty(KEY_FIELD_DELIMITER)))[1]
							.split(this.configuration.getProperty(KEY_KV_DELIMITER))[1]);
					if(_id == entity.getIdentifier()) {
						writer.write(this.serialize(entity).toString());
						success = true;
					}
					else {
						writer.write(entityString);
					}
					
					writer.newLine();
				}
			} finally {
				reader.close();
				writer.close();
			}
			
			// Delete temporary file
			tmpFile.delete();
		}
		
		return success;
	}

	@Override
	public <T extends Entity> boolean delete(T entity, Class<T> type) throws Exception {
		boolean success = false;
		
		if(entity.isManaged()) {
			// Get references to data and temporary file for the specified type
			File dataFile = this.getDataFile(type);
			File tmpFile = this.getTemporaryFile(type);
			
			// Move and rename data file to temporary file
			Files.move(dataFile.toPath(), tmpFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			
			Field[] fields = this.getFieldsForType(type);
			for(Field field: fields) {
				// Delete fields with cascade delete annotations
				PersistAnnotation metadata = this.getFieldPersistenceMetadata(field);
				if(CascadeType.cascade(metadata.cascade(), CascadeType.Delete)) {
					Class fType = field.getType();
					Object value = field.get(entity);
					
					if(Entity.class.isAssignableFrom(fType))
						this.delete((Entity) value, fType);
					else if(fType.isArray() || List.class.isAssignableFrom(fType)) {
						Class componentType = type.isArray()? type.getComponentType():
							metadata.type();
						
						if(Entity.class.isAssignableFrom(componentType)) {
							// Cast the references as a List
							List items = null;
							if(type.isArray())
								items = Arrays.asList((Object[]) value);
							else
								items = (List) value;
							
							// Loop through all items and delete
							for(Object item: items)
								this.delete((Entity)item, componentType);
						}
					}
				}
			}
			
			// Create new file and write data from temporary file to new file, ignoring entity string that matches
			// the specified entity's identifier.
			dataFile.createNewFile();
			BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));
			try {
				String entityString = null;
				while((entityString = reader.readLine()) != null) {
					long _id = Long.parseLong(entityString.split(Pattern.quote(this.configuration.getProperty(KEY_FIELD_DELIMITER)))[1]
							.split(this.configuration.getProperty(KEY_KV_DELIMITER))[1]);
					if(_id == entity.getIdentifier()) {
						success = true;
						// Remove entity from cache
						synchronized(this.entityCache) {
							if(this.entityCache.containsKey(type)) {
								Map<Long, SoftReference<Entity>> idMap = this.entityCache.get(type);
								idMap.remove(entity.getIdentifier());
							}
						}
					}
					else {
						writer.write(entityString);
						writer.newLine();
					}
				}
			} finally {
				reader.close();
				writer.close();
			}
			
			// Delete temporary file
			tmpFile.delete();
		}
		
		return success;
	}

	@Override
	public <T extends Entity> EntityIterable<T> search(Predicate<T> predicate, Class<T> type, boolean loadBeforePredicate)
			throws Exception {
		return new EntityIterable(this, this.getDataFile(type), predicate, loadBeforePredicate);
	}

	@Override
	public <T extends Entity> long getCount(Predicate<T> predicate, Class<T> type, boolean loadBeforePredicate)
			throws Exception {
		int count = 0;
		
		// Read data file and deserializes each entity.
		File dataFile = this.getDataFile(type);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			try {
				String entityString = null;
				while((entityString = reader.readLine()) != null) {
					T entity = this.deserialize(entityString, loadBeforePredicate, false);
					if(predicate == null || predicate.test(entity))
						count++;
				}
			} finally {
				reader.close();
			}
		} catch(FileNotFoundException e) {
			count = 0;
		}
		
		return count;
	}

	@Override
	public <T extends Entity> T retrieveByID(long id, Class<T> type) throws Exception {
		T entity = null;
		
		// Read data file and find matching IDs to deserialize
		File dataFile = this.getDataFile(type);
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		try {
			String entityString = null;
			while((entityString = reader.readLine()) != null) {
				long _id = Long.parseLong(entityString.split(Pattern.quote(this.configuration.getProperty(KEY_FIELD_DELIMITER)))[1]
											.split(this.configuration.getProperty(KEY_KV_DELIMITER))[1]);
				if(_id == id) {
					// Deserialize data into entity for matching IDs.
					entity = this.deserialize(entityString, true, false);
					break;
				}
			}
		} finally {
			reader.close();
		}
		
		return entity;
	}
	
	@Override
	public Properties getConfiguration() {
		return new Properties(this.configuration);
	}
	
	/**
	 * Clears the cache of this FilePersistence. Call this method to guarantee receiving fresh data.
	 */
	public void clear() {
		synchronized(this.entityCache) {
			this.entityCache.clear();
		}
	}
	
}
