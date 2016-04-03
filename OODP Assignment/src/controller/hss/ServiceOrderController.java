package controller.hss;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controller.EntityController;
import model.MenuItem;
import model.Reservation;
import model.ReservationStatus;
import model.ServiceOrder;
import persistence.Persistence;
import persistence.Predicate;
import persistence.file.text.EntityIterator;
import view.Options;
import view.View;

public class ServiceOrderController extends EntityController<ServiceOrder> {
	private final static String KEY_ROOM = "room number";
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
						return item.getAssignedRoom().getNumber().equals(inputMap.get(KEY_ROOM));
					}
				}, Reservation.class, true).iterator();
				
				if (reservations.hasNext())
					reservation = reservations.next();
				else {
					valid = true;
					view.message("Room is vacant, please try again.");
				}
				
				if (reservation != null) {
					List<ServiceOrder> orderList = reservation.getOrderList();
					Options option = Options.No;
					if (reservation.getStatus() == ReservationStatus.CheckedIn) {
						do {
							MenuItem item = miController.select(view);
							ServiceOrder svcOrder = new ServiceOrder(reservation, item);
							svcOrder.setRoom(reservation.getAssignedRoom());
							orderList.add(svcOrder);
							view.message("Do you want to add more items?");
							option = view.options(Arrays.asList(Options.Yes, Options.No));
						} while(option == Options.Yes);
					} else {
						valid = true;
						view.message("Room is not checked in, please try again.");
					}
				}
		} while(!valid && !view.bailout());
	}

	@Override
	protected boolean retrieve(View view) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void update(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void delete(View view) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceOrder select(View view) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
