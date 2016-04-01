package viewmodel.reservation;

/**
 * A view model class that encapsulates information about a text and a count.
 * @author YingHao
 */
public class TextAndCountVM {
	private final String text;
	private long count;
	
	/**
	 * TextAndCountVM constructor.
	 * @param text - The text that is associated to this instance.
	 * @param count - The count that is associated to this instance.
	 */
	public TextAndCountVM(String text, long count) {
		this.text = text;
		this.count = count;
	}
	
	/**
	 * Gets the text associated with this instance.
	 * @return view
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets the count.
	 * @return count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 * @param count
	 */
	public void setCount(long count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return text + "(" + count + " room(s) available)";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof TextAndCountVM) && ((TextAndCountVM)obj).getText().equals(this.getText());
	}

}
