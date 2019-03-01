package model;

public class StableProjectPair {

	private Project earlierProject;
	private Project laterProject;
	private InterprojectStabilityScore stabilityScore;

	public StableProjectPair(Project earlierProject, Project laterProject) {
		this.earlierProject = earlierProject;
		this.laterProject = laterProject;
	}

	public Project getEarlierProject() {
		return earlierProject;
	}

	public Project getLaterProject() {
		return laterProject;
	}

	public InterprojectStabilityScore getStabilityScore() {
		return stabilityScore;
	}

	public void setStabilityScore(InterprojectStabilityScore stabilityScore) {
		this.stabilityScore = stabilityScore;
	}

	public String toString() {
		return earlierProject.getName() + "\t" +
				earlierProject.getIntraprojectStabilityRatio() + "\t" +
				earlierProject.getIntraprojectStabilityRatioModularised() + "\t" +
				earlierProject.getProjectAuthors().size() + "\t" +
				earlierProject.getModules().size() + "\t" +
				laterProject.getName() + "\t" +
				laterProject.getIntraprojectStabilityRatio() + "\t" +
				laterProject.getIntraprojectStabilityRatioModularised() + "\t" +
				laterProject.getProjectAuthors().size() + "\t" +
				laterProject.getModules().size() + "\t" +
				stabilityScore.getScore() + "\t" +
				stabilityScore.getNumberOfCommonAuthors();
	}
}
