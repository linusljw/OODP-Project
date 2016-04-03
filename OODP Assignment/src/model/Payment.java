package model;

import java.util.List;
import java.util.concurrent.TimeUnit;
import persistence.Entity;
import persistence.PersistAnnotation;

/**
 * Address is an {@link Entity} class that encapsulates information about an Payment.
 * @author YingHao
 */
public class Payment extends Entity {
	@PersistAnnotation(type = Reservation.class)
	private final List<Reservation> reservations;
	private DiscountType discountType;
	private double discountValue;
	private PaymentType paymentType;
	
	/**
	 * Payment constructor. For Persistence API usage.
	 */
	protected Payment() {
		this.reservations = null;
	}
	
	/**
	 * Payment constructor.
	 * @param reservations - The list of reservations that this payment instance handles.
	 */
	public Payment(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	/**
	 * Sets the discount.
	 * @param discountType - The discount type for the specified value.
	 * @param value - The discount value.
	 */
	public void setDiscount(DiscountType discountType, double value) {
		this.discountType = discountType;
		this.discountValue = value;
	}
	
	/**
	 * Gets the payment type.
	 * @return paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}
	
	/**
	 * Sets the payment type.
	 * @param paymentType
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	
	/**
	 * Gets the total sum for this payment instance.
	 * @return totalSum
	 */
	public double getTotalSum() {
		double totalSum = 0;
		
		for(Reservation reservation: reservations) {
			// Loop through all reservations and add up all the room prices
			long numOfDays = (reservation.getEndDate().getTime() - reservation.getStartDate().getTime()) / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
			totalSum += reservation.getAssignedRoom().getType().getPrice() * numOfDays;
			
			for(ServiceOrder order: reservation.getOrderList()) {
				// Loop through all the service orders and add up all the service orders.
				if(order.getStatus() == OrderStatus.Delivered)
					totalSum += order.getItem().getPrice();
			}
		}
		
		// Perform discount operation
		if(discountType != null) {
			if(discountType == DiscountType.FIXED)
				totalSum -= discountValue;
			else
				totalSum *= (100 - discountValue) / 100;
		}
		
		return totalSum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("--------- Payment ---------\n");
		if(this.isManaged())
			builder.append("Receipt number: " + this.getIdentifier() + "\n");
		
		builder.append("Total number of rooms: " + reservations.size() + "\n");
		for(Reservation reservation: reservations) {
			builder.append("\n");
			double roomSubTotal = 0;
			Room room = reservation.getAssignedRoom();
			
			long days = (reservation.getEndDate().getTime() - reservation.getStartDate().getTime()) / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
			builder.append("----- Room " + room.getNumber() + " -----\n");
			builder.append("Cost per night: " + room.getType().getPrice() + "\n");
			builder.append("Number of nights stayed: " + days + "\n");
			roomSubTotal = days * room.getType().getPrice();
			
			int count = 0;
			for(ServiceOrder order: reservation.getOrderList()) {
				builder.append("--- Service Orders ---\n");
				// Loop through all the service orders and count the number of delivered service order.
				if(order.getStatus() == OrderStatus.Delivered) {
					count++;
					builder.append("Service Order " + count + ": " + order.getItem().getName() + "($" + order.getItem().getPrice() + ")\n");
					roomSubTotal += order.getItem().getPrice();
				}
			}
			builder.append("Total number of room service orders placed: " + count + "\n");
			builder.append("The subtotal for this room is $" + String.format("%.2f", roomSubTotal) + "\n");
		}
		
		builder.append("\n");
		builder.append("Discount: ");
		if(discountType == null) {
			builder.append("None");
		}
		else {
			if(discountType == DiscountType.FIXED)
				builder.append("$");
			builder.append(discountValue);
			if(discountType == DiscountType.PERCENTAGE)
				builder.append("%");
		}
		builder.append("\nThe subtotal of all rooms checked-out is $" + String.format("%.2f", getTotalSum()) + "\n");
		
		return builder.toString();
	}

}
