package controller.report;

import java.util.Arrays;
import java.util.List;

import controller.PersistenceController;
import persistence.Persistence;
import view.View;

/**
 * A controller reponsible for generating reports.
 * @author YingHao
 *
 */
public class ReportController extends PersistenceController {

	/**
	 * ReportController constructor.
	 * @param persistence - The Persistence API implementation class to interact with for entity persistency.
	 */
	public ReportController(Persistence persistence) {
		super(persistence);
	}

	@Override
	public List<String> getOptions() {
		return Arrays.asList("View report for today", 
								"View report for this week",
								"View report for this month",
								"View report for this year");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		switch(option) {
		case 0:
			viewReportForToday(view);
			break;
		default:
			viewReportForRange(view, option);
			break;
		}
	}
	
	/**
	 * Displays room occupancy report for today.
	 * @param view - A view interface that provides input/output.
	 */
	private void viewReportForToday(View view) {
		
	}
	
	/**
	 * Displays room occupancy report for the specified date range.
	 * @param view - A view interface that provides input/output.
	 * @param option - The option of the date range selected. This depends on {@link #getOptions()}.
	 */
	private void viewReportForRange(View view, int option) {
		
	}
}
