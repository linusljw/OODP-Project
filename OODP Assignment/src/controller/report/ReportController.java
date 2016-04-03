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
		return Arrays.asList("Generate report for today", 
								"Generate report for this week",
								"Generate report for this month",
								"Generate report for this year");
	}

	@Override
	protected void safeOnOptionSelected(View view, int option) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
