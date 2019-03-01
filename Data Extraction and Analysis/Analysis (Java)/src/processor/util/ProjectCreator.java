package processor.util;

import java.util.ArrayList;
import java.util.List;

import model.CommitSet;
import model.Project;

public class ProjectCreator {

	public static List<Project> createProjects(CommitSet commitSet) {
		
		List<Project> projects = new ArrayList<Project>();
		
		System.out.println("ProjectCreator - starting");
		for(String projectName : commitSet.getProjectNames()) {
			projects.add(new Project(projectName, commitSet.getCommitsForProject(projectName)));
		}
		System.out.println("ProjectCreator - completed");

		return projects;
	}
	
}
