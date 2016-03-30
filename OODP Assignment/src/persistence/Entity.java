package persistence;

/**
 * Entity is an abstract base class for all java objects that can be persisted by the
 * Persistence API.
 * @author YingHao
 * @see Persistence
 */
public abstract class Entity {
	private final long _id;
	
	/**
	 * Default constructor.
	 */
	public Entity() {
		this._id = Long.MIN_VALUE;
	}
	
	/**
	 * Gets the unique identifier.
	 * @return identifier
	 */
	public long getIdentifier() {
		return this._id;
	}
	
	/**
	 * Gets whether this entity is a managed entity.
	 * @return Managed status
	 */
	public boolean isManaged() {
		return this._id != Long.MIN_VALUE;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		
		if(obj != null && obj instanceof Entity) {
			Entity entity = (Entity) obj;
			
			// Equals condition is met if and only if they meet the 3 requirements:
			// 1) They are of the same class/type
			// 2) They are either both managed or unmanaged
			// 3) They must have the same identifier if managed or must refer to the same instance if unmanaged
			if(entity.getClass().equals(this.getClass()) && 
					entity.isManaged() == this.isManaged()) {
				if(entity.isManaged())
					equals = entity.getIdentifier() == this.getIdentifier();
				else
					equals = entity == this;
			}
		}
			
		return equals;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		
		// Generate a unique hash function for Entity classes to support the Java Collections Framework.
		int seed = 31;
		hash = (hash * seed + this.getClass().hashCode()) % Integer.MAX_VALUE;
		hash = (hash * seed + (int)(this._id ^ (this._id >>> 32))) % Integer.MAX_VALUE;
		
		return hash;
	}

	@Override
	public String toString() {
		return "------------ ID " + this._id + " ------------\n";
	}
	
}
