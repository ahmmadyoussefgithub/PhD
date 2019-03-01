package processor.filter.impl;

import model.Project;
import processor.filter.StableProjectPairFilter;

public class NonOverlapSequenceFilter implements StableProjectPairFilter {

	@Override
	public boolean filter(Project projectA, Project projectB) {

		if(projectA.getProjectEndDate().before(projectB.getProjectEndDate())) {
			return false;
		}
		
		return true;
	}
}
