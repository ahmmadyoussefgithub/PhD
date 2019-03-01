package analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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


public class BasicTeamStabilityAnalysis {

	private static DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

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
		CommitSet commitSet = commitLoader.loadCommitSet(args[0], ProjectLists.STABILITY_SAMPLE_FINAL);

		for(String project : commitSet.projectCommitMap.keySet()) {
			try {
				System.out.print(project +"\t" + determineUniqueAuthors(commitSet.getCommitsForProject(project)).size() +"\t" + DF.format(earliestCommit(commitSet.getCommitsForProject(project)))+"\t" + DF.format(latestCommit(commitSet.getCommitsForProject(project)))+"\t");
				for(Author author : determineUniqueAuthors(commitSet.getCommitsForProject(project))) {
					Set<Commit> commitsForAuthor = getCommitsForAuthor(author, commitSet.getCommitsForProject(project));
					Date earliestCommitForAuthor = earliestCommit(commitsForAuthor);
					Date latestCommitForAuthor = latestCommit(commitsForAuthor);
					System.out.print(author.getRawName()+"\t"+DF.format(earliestCommitForAuthor)+"\t"+DF.format(latestCommitForAuthor)+"\t");
				}
				System.out.print("\n");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Set<Author> determineUniqueAuthors(Set<Commit> commitsForProject) {

		Set<Author> authors = new HashSet<Author>();
		
		for(Commit commit : commitsForProject) {
			authors.add(new Author(commit.getAuthor()));
		}
		
		return authors;
	}

	private static Date earliestCommit(Set<Commit> commitsForProject) {

		Commit earliestCommit = null;

		for(Commit commit : commitsForProject) {
			if(earliestCommit == null) {
				earliestCommit = commit;
			} else if(earliestCommit.getDate().after(commit.getDate()))
				earliestCommit = commit;
		}
		return earliestCommit.getDate();
	}

	private static Date latestCommit(Set<Commit> commitsForProject) {

		Commit latestCommit = null;

		for(Commit commit : commitsForProject) {
			if(latestCommit == null) {
				latestCommit = commit;
			} else if(latestCommit.getDate().before(commit.getDate()))
				latestCommit = commit;
		}
		return latestCommit.getDate();
	}

	private static Set<Commit> getCommitsForAuthor(Author author, Set<Commit> commits) {
		Set<Commit> commitsForAuthor = new HashSet<Commit>();
		
		for(Commit commit : commits) {
			Author commitAuthor = new Author(commit.getAuthor());
			if(commitAuthor.equals(author)) {
				commitsForAuthor.add(commit);
			}
		}
		
		return commitsForAuthor;
	}

}
