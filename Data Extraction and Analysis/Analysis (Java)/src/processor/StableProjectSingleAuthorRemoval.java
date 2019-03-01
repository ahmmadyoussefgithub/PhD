package processor;

import java.util.HashSet;
import java.util.Set;

import model.Project;
import model.StableProjectPair;

public class StableProjectSingleAuthorRemoval {

	public static void execute(Set<StableProjectPair> stablePairs) {

		Set<StableProjectPair> singleAuthorPairs = new HashSet<StableProjectPair>();

		int count=0;
		for(StableProjectPair stablePair : stablePairs) {
			count++;
			if(count%1000==0) {
				System.out.println("StableProjectSingleAuthorRemoval processed: " +count+ "/" +stablePairs.size());
			}
			
			if(isSingleAuthor(stablePair.getEarlierProject()) || isSingleAuthor(stablePair.getLaterProject())) {
				singleAuthorPairs.add(stablePair);
			}
		}

		stablePairs.removeAll(singleAuthorPairs);
	}

	private static boolean isSingleAuthor(Project project) {
				
		for(String module : project.getModules()) {
			if(project.getModuleAuthors(module).size()>1) {
				return false;
			}
		}
		
		return true;
	}

}
