package processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Author;
import model.Project;
import model.StableProjectPair;
import processor.filter.StableProjectPairFilter;

public class StableProjectSearch {

	private List<StableProjectPairFilter> filters;
	
	public StableProjectSearch(List<StableProjectPairFilter> filters) {
		this.filters = filters;
	}
	
	public Set<StableProjectPair> execute(List<Project> projects) {
		
		Map<Author, List<Project>> authorProjectMap = createAuthorProjectMap(projects);
		Set<StableProjectPair> projectPairs = new HashSet<StableProjectPair>();
		int count = 0;
		for(Project project : projects) {
			System.out.println("StableProjectSearch processing: " +project);
			Set<Project> candidateProjects = determineCandidateProjects(project.getProjectAuthors(), authorProjectMap);
			projectPairs.addAll(determineProjectPairs(project, candidateProjects));
			count++;
			if(count%100==0) {
				System.out.println("StableProjectSearch processed: " +count+ "/" +projects.size());
			}
		}

		return projectPairs;
	}

	private Map<Author, List<Project>> createAuthorProjectMap(List<Project> projects) {

		Map<Author, List<Project>> authorProjectMap = new HashMap<Author, List<Project>>();
		
		for(Project project : projects) {
			for(Author author : project.getProjectAuthors()) {
				if(!authorProjectMap.containsKey(author)) {
					authorProjectMap.put(author, new ArrayList<Project>());
				}
				authorProjectMap.get(author).add(project);
			}
		}

		return authorProjectMap;
	}

	private Set<Project> determineCandidateProjects(Set<Author> authors, Map<Author, List<Project>> authorProjectMap) {

		Set<Project> candidateProjects = new HashSet<Project>();
		
		for(Author author : authors) {
			candidateProjects.addAll(authorProjectMap.get(author));
		}
		
		return candidateProjects;
	}

	private Set<StableProjectPair> determineProjectPairs(Project project, Set<Project> projects) {
		
		Set<StableProjectPair> projectPairs = new HashSet<StableProjectPair>();
		
		for(Project otherProject : projects) {
			if(project!=otherProject) {
				if(isStableProjectPair(project, otherProject)) {
					projectPairs.add(new StableProjectPair(project, otherProject));
				}
			}
		}
		return projectPairs;
	}

	private boolean isStableProjectPair(Project project, Project otherProject) {

		for(StableProjectPairFilter filter : filters) {
			if(filter.filter(project, otherProject)) {
				return false;
			}
		}
		
		return true;
	}


}
