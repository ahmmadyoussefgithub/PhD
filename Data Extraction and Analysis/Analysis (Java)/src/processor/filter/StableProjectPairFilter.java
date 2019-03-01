package processor.filter;

import model.Project;

public interface StableProjectPairFilter {

	public boolean filter(Project projectA, Project projectB);
}
