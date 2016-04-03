package controller.hss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import model.MenuItem;
import model.OrderStatus;
import model.Reservation;
import model.ReservationStatus;
import model.Room;
import model.ServiceOrder;
import persistence.Entity;
import persistence.Persistence;
import persistence.Predicate;
import persistence.file.text.EntityIterator;
import view.Options;
import view.View;

public class ServiceOrderController extends EntityController<ServiceOrder> {
	private final static String KEY_ID = "order id";
	private final static String KEY_ROOM = "room number";
	private final static String KEY_REMARKS = "remarks";
	private EntityController<MenuItem> miController = null;
	
	/**
	 * ServiceOrder Constructor
	 * @param persistence
	 */
	public ServiceOrderController(Persistence persistence, EntityController<MenuItem> miController) {
		super(persistence);
		this.miController = miController;
	}

	@Override
	protected String getEntityName() {
		return "Service Order";
	}
	
	@Override
	public List<String> getOptions() {
		return Arrays.asList(
					"Create " + this.getEntityName().toLowerCase(),
					"Retrieve " + this.getEntityName().toLowerCase(),
					"Cancel " + this.getEntityName().toLowerCase(),
					"Update " + this.getEntityName().toLowerCase() + " status"
				);
	}
	
	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			create(view);
			break;
		case 1:
			retrieve(view);
			break;
		case 2:
			delete(view);
			break;
		case 3:
			updateStatus(view);
			break;
		}
	}

	/**
	 * Prompts the user to enter relevant information required to create a Service Order.
	 */
	@Override
	protected void create(View view) throws Exception {
		Reservation reservation = null;
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ROOM, null);
		
		boolean valid = false;
		Persistence persistence = this.getPersistenceImpl();
		
		do {
			view.input(inputMap);
			
				EntityIterator<Reservation> reservations = (EntityIterator<Reservation>)persistence.search(new Predicate<Reservation>() {
					@Override
					public boolean test(Reservation item) {
						return item.getStatus() == ReservationStatus.CheckedIn && 
								item.getAssignedRoom().getNumber().equals(inputMap.get(KEY_ROOM));
					}
				}, Reservation.class, true).iterator();
				
				if (reservations.hasNext())
					reservation = reservations.next();
				else {
					valid = true;
					view.message("Room is not checked in, please try again.");
				}
				
				reservations.close();
				
				if (reservation != null) {
					Options option = Options.No;
					do {
						MenuItem item = miController.select(view);
						ServiceOrder svcOrder = new ServiceOrder(reservation, item);
						svcOrder.setRoom(reservation.getAssignedRoom());
						inputMap.clear();
						inputMap.put(KEY_REMARKS, null);
						view.input(inputMap);
						svcOrder.setStatus(OrderStatus.Confirmed);
						svcOrder.setRemarks(inputMap.get(KEY_REMARKS));
						reservation.getOrderList().add(svcOrder);
						view.message("Do you want to add more items?");
						option = view.options(Arrays.asList(Options.Yes, Options.No));
					} while(option == Options.Yes);
					
					if (!reservation.getOrderList().isEmpty()) {
						if (persistence.update(reservation, Reservation.class)) {
							view.message("Ordered has been placed, it will be delivered to your room!");
							valid = true;
						}
					}
				}
		} while(!valid && !view.bailout());
	}

	/**
	 * Retrieves and display all Service Order that isn't cancelled or delivered.
	 */
	@Override
	protected boolean retrieve(View view) throws Exception {
		Persistence persistence = this.getPersistenceImpl();
		
		List entityList = new ArrayList();
		
		Iterable<Reservation> reservations = persistence.search(new Predicate<Reservation>() {
			@Override
			public boolean test(Reservation item) {
				return item.getStatus() == ReservationStatus.CheckedIn;
			}
		}, Reservation.class, true);
		
		for (Reservation entity: reservations)
			for (ServiceOrder item: entity.getOrderList())
				if (item.getStatus() != OrderStatus.Cancelled && item.getStatus() != OrderStatus.Delivered)
					entityList.add(item);
		
		view.display(entityList);
		
		return entityList.size() > 0;
	}

	@Override
	protected void update(View view) throws Exception {
		// TODO Auto-generated method stub - Won't be using this method.
	}

	/**
	 * Prompts the user to enter relevant information required and cancels service order.
	 */
	@Override
	protected void delete(View view) throws Exception {
		ServiceOrder so = select(view);
		so.setStatus(OrderStatus.Cancelled);
	}
	
	/**
	 * Prompts the user to enter relevant information to update service order status.
	 */
	protected void updateStatus(View view) throws Exception {
		ServiceOrder so = select(view);
		
		if (so != null) {
			Persistence persistence = this.getPersistenceImpl();
			
			so.setStatus(view.options(Arrays.asList(OrderStatus.values())));
			
			if(persistence.update(so, ServiceOrder.class))
				view.message("Service order status has been updated successfully!");
		}
	}

	/**
	 * Prompts the user to select a service order.
	 */
	@Override
	public ServiceOrder select(View view) throws Exception {
		ServiceOrder so = null;
		
		retrieve(view);
		
		Map<String, String> inputMap = new LinkedHashMap<String, String>();
		inputMap.put(KEY_ID, null);

		Persistence persistence = this.getPersistenceImpl();
		do {
			view.input(inputMap);
			
			EntityIterator<ServiceOrder> serviceorders = (EntityIterator<ServiceOrder>) persistence.search(new Predicate<ServiceOrder>() {
				@Override
				public boolean test(ServiceOrder item) {
					return item.getIdentifier() == Long.parseLong(inputMap.get(KEY_ID));
				}
			}, ServiceOrder.class, false).iterator();
			if(serviceorders.hasNext())
				so = serviceorders.next();
			else
				view.message("Service order does not exist. Please try again.\n");
			serviceorders.close();
		} while(so == null && !view.bailout());
		
		return so;
	}

}
