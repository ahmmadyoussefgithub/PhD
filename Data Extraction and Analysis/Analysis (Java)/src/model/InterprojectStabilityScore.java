package model;

public class InterprojectStabilityScore {

	private double stableCommitCount;
	private double totalCommitCount;
	private int numberOfCommonAuthors;

	public InterprojectStabilityScore(double stableCommitCount, double totalCommitCount, int numberOfCommonAuthors) {
		this.stableCommitCount = stableCommitCount;
		this.totalCommitCount = totalCommitCount;
		this.numberOfCommonAuthors = numberOfCommonAuthors;
	}

	public double getStableCommitCount() {
		return this.stableCommitCount;
	}

	public double getTotalCommitCount() {
		return this.totalCommitCount;
	}

	public int getNumberOfCommonAuthors() {
		return this.numberOfCommonAuthors;
	}

	public double getScore() {
		Double score = (stableCommitCount/totalCommitCount)*100d;
		score = Math.round(score*100d)/100d;
		return score;
	}
	
	public String toString() {
		return "[" + stableCommitCount + "]" +
				"[" + totalCommitCount + "]" +
				"[" + numberOfCommonAuthors + "]" +
				"[" + getScore() + "]";
	}

}
