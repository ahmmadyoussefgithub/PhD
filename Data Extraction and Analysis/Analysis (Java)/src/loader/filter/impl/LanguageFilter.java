package loader.filter.impl;

import loader.filter.CommitFilter;
import model.Commit;

public class LanguageFilter implements CommitFilter {

	private String fileExtension;
	
	public LanguageFilter(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public boolean filter(Commit commit) {
		//for(String filename : commit.getFilenames()) {
			//if(filename.trim().endsWith("." +fileExtension)) {
			if(commit.getFileExtensions()!=null && commit.getFileExtensions().contains(fileExtension)) {
				return false;
			}
		//}
		return true;
	}
}
