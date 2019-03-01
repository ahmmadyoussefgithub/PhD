package processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Author;
import model.Commit;
import model.Project;
import model.InterprojectStabilityScore;
import model.StableProjectPair;

public class InterProjectStabilityScorer {

	public static void execute(Set<StableProjectPair> stableProjects) {

		int count=0;;
		for(StableProjectPair projectPair : stableProjects) {
			count++;
			if(count%1000==0) {
				System.out.println("StabilityScorer processing: " +count+ "/" +stableProjects.size());
			}
			
			Set<Author> commonAuthors = determineCommonAuthors(projectPair);
			if(!commonAuthors.isEmpty()) {
				List<Commit> stableProjectCommits = determineCommitsByAuthors(commonAuthors, projectPair.getLaterProject());
				projectPair.setStabilityScore(new InterprojectStabilityScore(stableProjectCommits.size(), projectPair.getLaterProject().getAllCommits().size(), commonAuthors.size()));
			}
		}
	}

	private static Set<Author> determineCommonAuthors(StableProjectPair projectPair) {

		Set<Author> earlierProjectAuthors = projectPair.getEarlierProject().getProjectAuthors();
		Set<Author> laterProjectAuthors = projectPair.getLaterProject().getProjectAuthors();
		Set<Author> commonAuthors = new HashSet<Author>();

		for(Author earlierAuthor : earlierProjectAuthors) {
			if(laterProjectAuthors.contains(earlierAuthor)) {
				commonAuthors.add(earlierAuthor);
			}
		}
		return commonAuthors;
	}

	private static List<Commit> determineCommitsByAuthors(Set<Author> authors, Project project) {

		List<Commit> commitsByCommonAuthors = new ArrayList<Commit>();

		for(Commit commit : project.getAllCommits()) {
			if(authors.contains(new Author(commit.getAuthor()))) {
				commitsByCommonAuthors.add(commit);
			}
		}

		return commitsByCommonAuthors;
	}
}
