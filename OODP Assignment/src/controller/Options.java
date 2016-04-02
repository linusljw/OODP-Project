package controller;

/**
 * An enumeration of standard options for controller classes.
 * @author YingHao
 */
public enum Options {
	
	/**
	 * Yes option.
	 */
	Yes, 
	
	/**
	 * No option.
	 */
	No, 
	
	/**
	 * Required option.
	 */
	Required, 
	
	/**
	 * Not required option.
	 */
	NotRequired;

	@Override
	public String toString() {
		return super.toString().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
	}

}
