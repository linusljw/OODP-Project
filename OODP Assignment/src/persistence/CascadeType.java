package persistence;

/**
 * An enum specifying the possible cascade types for an entity.
 * @author YingHao
 */
public enum CascadeType {
	Create, Update, Delete;
	
	/**
	 * Test if an array of cascade type can perform the given cascade operation.
	 * @param operations - The array of CascadeType to test for a cascade operation.
	 * @param cascade - The cascade operation to test.
	 * @return A flag indicating whether a specified cascade operation can be performed.
	 */
	public static boolean cascade(CascadeType[] operations, CascadeType cascade) {
		boolean found = false;
		
		// Searches through the available cascade operations and attempts to find
		// a matching CascadeType.
		for(int i = 0; !found && i < operations.length; i++)
			if(operations[i] == cascade)
				found = true;
		
		return found;
	}
}
