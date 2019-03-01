package processor.filter.impl;

import model.Project;
import processor.filter.StableProjectPairFilter;

public class ConsecutiveSequenceFilter implements StableProjectPairFilter {

	@Override
	public boolean filter(Project projectA, Project projectB) {

		if(projectA.getProjectStartDate().before(projectB.getProjectStartDate())) {
			return false;
		}
		
		return true;
	}

}
