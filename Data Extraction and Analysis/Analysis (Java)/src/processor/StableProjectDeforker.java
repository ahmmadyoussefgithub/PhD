package processor;

import java.util.HashSet;
import java.util.Set;

import model.Commit;
import model.StableProjectPair;

public class StableProjectDeforker {


	public static void execute(Set<StableProjectPair> stablePairs) {

		Set<StableProjectPair> forkedPairs = new HashSet<StableProjectPair>();

		int count=0;
		for(StableProjectPair stablePair : stablePairs) {
			count++;
			if(count%1000==0) {
				System.out.println("StableProjectDeforker processed: " +count+ "/" +stablePairs.size());
			}
			
			if(isForked(stablePair)) {
				forkedPairs.add(stablePair);
			}
		}

		stablePairs.removeAll(forkedPairs);

	}

	private static boolean isForked(StableProjectPair stableProjectPair) {
		if(determineCommonCommitCount(stableProjectPair) > 10) {
			return true;
		}
		return false;
	}

	private static int determineCommonCommitCount(StableProjectPair stableProjectPair) {

		int commonCommitCount = 0;

		for(Commit earlierProjectCommit : stableProjectPair.getEarlierProject().getAllCommits()) {
			for(Commit laterProjectCommit : stableProjectPair.getLaterProject().getAllCommits()) {
				if(earlierProjectCommit.equals(laterProjectCommit)) {
					commonCommitCount++;
				}
			}
		}

		return commonCommitCount;
	}
}
