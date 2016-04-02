package model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import persistence.CascadeType;
import persistence.PersistAnnotation;

/**
 * Reservation is a {@link StatusEntity} that encapsulates information about a Reservation.
 * @author YingHao
 */
public class Reservation extends StatusEntity<ReservationStatus> {
	private final Guest guest;
	@PersistAnnotation(
			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final RoomDescription criteria;
	@PersistAnnotation(
			cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final BillingInformation billingInformation;
	private int numOfChildren;
	private int numOfAdult;
	private Date startDate;
	private Date endDate;
	@PersistAnnotation(
			cascade = {CascadeType.Update}
	)
	private Room assignedRoom;
	
	/**
	 * Reservation constructor. For Persistence API usage.
	 */
	protected Reservation() {
		this.guest = null;
		this.criteria = null;
		this.billingInformation = null;
	}
	
	/**
	 * Reservation constructor. This assigns a WaitList status for this reservation.
	 * @param guest - The guest that made this reservation.
	 */
	public Reservation(Guest guest) {
		this.guest = guest;
		this.criteria = new RoomDescription();
		this.billingInformation = new BillingInformation();
		this.setStatus(ReservationStatus.Waitlist);
	}
	
	/**
	 * Gets the guest that made this reservation.
	 * @return
	 */
	public Guest getGuest() {
		return guest;
	}
	
	/**
	 * Gets the criteria for the room for this reservation.
	 * @return
	 */
	public RoomDescription getCriteria() {
		return criteria;
	}
	
	/**
	 * Gets the billing information registered with this reservation.
	 * @return billingInformation
	 */
	public BillingInformation getBillingInformation() {
		return billingInformation;
	}
	
	/**
	 * Gets the number of children.
	 * @return numOfChildren
	 */
	public int getNumOfChildren() {
		return numOfChildren;
	}
	
	/**
	 * Sets the number of children.
	 * @param numOfChildren - Number of children.
	 */
	public void setNumOfChildren(int numOfChildren) {
		this.numOfChildren = numOfChildren;
	}
	
	/**
	 * Gets the number of adult.
	 * @return numOfAdult
	 */
	public int getNumOfAdult() {
		return numOfAdult;
	}
	
	/**
	 * Sets the number of adult.
	 * @param numOfAdult - Number of adult.
	 */
	public void setNumOfAdult(int numOfAdult) {
		this.numOfAdult = numOfAdult;
	}
	
	/**
	 * Gets the start date of this reservation.
	 * @return startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date of this reservation.
	 * @param startDate - Start date of reservation.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the end date of this reservation.
	 * @return endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the end date of this reservation.
	 * @param endDate - End date of reservation.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Gets the room assigned to this reservation.
	 * @return
	 */
	public Room getAssignedRoom() {
		return assignedRoom;
	}
	
	/**
	 * Sets the room assigned to this reservation and assigns this reservation to a Confirmed status.
	 * @param room - Room assigned to this reservation.
	 */
	public void setAssignedRoom(Room room) {
		this.assignedRoom.getReservationList().remove(this);
		this.assignedRoom = room;
		if(room == null) {
			this.setStatus(ReservationStatus.Waitlist);
		}
		else {
			this.setStatus(ReservationStatus.Confirmed);
			this.assignedRoom.getReservationList().add(this);
		}
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		RoomDescription criteria = this.getCriteria();
		String status = this.getStatus().toString();
		if(this.getStatus() == ReservationStatus.CheckedIn)
			status = "Checked in";
		
		String roomType = "Any";
		String bedType = "Any";
		String view = "Any";
		String wifi = "Not required";
		String smoking = "Not required";
		
		if(criteria.getRoomType() != null)
			roomType = criteria.getRoomType().getName();
		if(criteria.getBedType() != null)
			bedType = criteria.getBedType().toString();
		if(criteria.getView() != null)
			view = criteria.getView();
		if(criteria.isWifi())
			wifi = "Required";
		if(criteria.isSmoking())
			smoking = "Required";
		
		BillingInformation billing = this.getBillingInformation();
		String ccNumberMask = billing.getCreditCardNumber();
		
		// Prepare obfuscation mask
		char[] mask = new char[ccNumberMask.length() - 4];
		Arrays.fill(mask, '*');
		ccNumberMask = new String(mask) + ccNumberMask.substring(ccNumberMask.length() - 4, ccNumberMask.length());
		
		return "Reservation number: " + this.getIdentifier() + "\n" +
				"Reserved by: " + this.getGuest().getName() + "(ID: " + this.getIdentifier() + ")\n" +
				"Start date: " + sdf.format(this.getStartDate()) + "\n" +
				"End date: " + sdf.format(this.getEndDate()) + "\n" + 
				"Status: " + status + "\n" +
				"----- Room Requirements -----\n" + 
				"Room type: " + roomType + "\n" +
				"Bed type: " + bedType + "\n" +
				"View: " + view + "\n" +
				"Wifi-Enabled: " + wifi + "\n" +
				"Smoking-Room: " + smoking + "\n" + 
				"----- Billing Information -----\n" +
				"Credit card number: " + ccNumberMask + "\n" +
				billing.getAddress().toString();
	}

}
