package loader.filter.impl;

import loader.filter.CommitFilter;
import model.Commit;

public class AuthorFilter implements CommitFilter {

	private String searchString;

	public AuthorFilter(String searchString) {
		this.searchString = searchString;
	}

	public boolean filter(Commit commit) {
		if(!commit.getAuthor().contains(searchString)) {
			return false;
		}
		return true;
	}
}
