package processor.filter.impl;

import model.Author;
import model.Project;
import processor.filter.StableProjectPairFilter;

public class CommonAuthorFilter implements StableProjectPairFilter {

	public boolean filter(Project projectA, Project projectB) {

		for(Author author : projectA.getProjectAuthors()) {
			for(Author otherAuthor : projectB.getProjectAuthors()) {
				if(author.equals(otherAuthor)) {
					return false;
				}
			}
		}
		return true;
	}
}
