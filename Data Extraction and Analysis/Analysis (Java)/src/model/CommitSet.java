package model;

import java.util.Map;
import java.util.Set;

public class CommitSet {

	public Map<String, Set<Commit>> projectCommitMap;
	
	public CommitSet(Map<String, Set<Commit>> projectCommitMap) {
		this.projectCommitMap = projectCommitMap;
	}
	
	public Set<Commit> getCommitsForProject(String projectName) {
		return this.projectCommitMap.get(projectName);
	}
	
	public Set<String> getProjectNames() {
		return this.projectCommitMap.keySet();
	}
	
}
