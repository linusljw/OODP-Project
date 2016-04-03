package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	@PersistAnnotation(
		      cascade = {CascadeType.Create, CascadeType.Update, CascadeType.Delete}
	)
	private final List<ServiceOrder> orders;
	private int numOfChildren;
	private int numOfAdult;
	private Date startDate;
	private Date endDate;
	@PersistAnnotation(
			cascade = {CascadeType.Update}
	)
	private Room assignedRoom;
	private Payment payment;
	
	/**
	 * Reservation constructor. For Persistence API usage.
	 */
	protected Reservation() {
		this.guest = null;
		this.criteria = null;
		this.billingInformation = null;
		this.orders = null;
	}
	
	/**
	 * Reservation constructor. This assigns a WaitList status for this reservation.
	 * @param guest - The guest that made this reservation.
	 */
	public Reservation(Guest guest) {
		this.guest = guest;
		this.criteria = new RoomDescription();
		this.billingInformation = new BillingInformation();
		this.orders = new ArrayList<ServiceOrder>();
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
	 * Gets the service orders that have been made using this reservation.
	 * @return orders
	 */
	public List<ServiceOrder> getOrderList() {
		return orders;
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
		if(this.assignedRoom != null)
			this.assignedRoom.getReservationList().remove(this);
		
		if(room == null) {
			this.setStatus(ReservationStatus.Waitlist);
		}
		else {
			this.assignedRoom = room;
			this.setStatus(ReservationStatus.Confirmed);
			this.assignedRoom.getReservationList().add(this);
		}
	}
	
	/**
	 * Gets the payment instance that was made for this reservation.
	 * @return payment
	 */
	public Payment getPayment() {
		return payment;
	}
	
	/**
	 * Sets the payment instance that was made for this reservation.
	 * @param payment 
	 */
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	@Override
	public void setStatus(ReservationStatus status) {
		if(status == ReservationStatus.Cancelled || status == ReservationStatus.Expired || status == ReservationStatus.CheckedOut) {
			if(status == ReservationStatus.CheckedOut)
				assignedRoom.setStatus(RoomStatus.Vacant);
			setAssignedRoom(null);
		}
		else if(status == ReservationStatus.CheckedIn) {
			assignedRoom.setStatus(RoomStatus.Occupied);
		}
		
		super.setStatus(status);
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String status = this.getStatus().toString();
		if(this.getStatus() == ReservationStatus.CheckedIn)
			status = "Checked in";
		
		return "----- Reservation Information -----\n" +
				"Reservation number: " + this.getIdentifier() + "\n" +
				"Reserved by: " + this.getGuest().getName() + "(ID: " + this.getGuest().getIdentifier() + ")\n" +
				"Start date: " + sdf.format(this.getStartDate()) + "\n" +
				"End date: " + sdf.format(this.getEndDate()) + "\n" + 
				"Status: " + status + "\n";
	}

}
