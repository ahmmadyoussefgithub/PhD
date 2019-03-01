package analysis;

import java.util.HashSet;
import java.util.Set;

import loader.CommitLoader;
import loader.ProjectLists;
import loader.filter.impl.AuthorFilter;
import loader.filter.impl.DateFilter;
import loader.filter.impl.LanguageFilter;
import model.Author;
import model.Commit;
import model.CommitSet;


public class TeamSizeAnalysis {

	/**
	 * Initialised with the path to raw revision logs
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		CommitLoader commitLoader = new CommitLoader();
		commitLoader.addFilter(new DateFilter("01-06-2012 00:00:00 +0000"));
		commitLoader.addFilter(new LanguageFilter("java"));
		commitLoader.addFilter(new AuthorFilter("no author"));
		CommitSet commitSet = commitLoader.loadCommitSet(args[0], ProjectLists.TEAM_SIZE_PROJECT_SAMPLE);

		for(String project : commitSet.projectCommitMap.keySet()) {
			try {
				System.out.println(project +"\t" + determineTeamSize(commitSet.getCommitsForProject(project)));
				
			} catch(Exception e) {}
		}
	}

	private static int determineTeamSize(Set<Commit> commitsForProject) {

		Set<Author> authors = new HashSet<Author>();
		
		for(Commit commit : commitsForProject) {
			authors.add(new Author(commit.getAuthor()));
		}
		
		return authors.size();
	}

}
