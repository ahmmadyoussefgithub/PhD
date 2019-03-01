package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Set<Commit> commits;
	private double intraprojectStabilityRatio;
	private double intraprojectStabilityRatioModularised;
	Map<String, Pair<Integer, Double>> classLevelStabilityRatios = new HashMap<>();
	
	public Project(String name, Set<Commit> commits) {
		this.name = name;
		this.commits = commits;
	}
	
	public void setIntraprojectStabilityRatio(double intraprojectStabilityRatio) {
		this.intraprojectStabilityRatio = intraprojectStabilityRatio;
	}
	
	public void setIntraprojectStabilityRatioModularised(double intraprojectStabilityRatioModularised) {
		this.intraprojectStabilityRatioModularised = intraprojectStabilityRatioModularised;
	}
	
	public void addClassLevelStabilityRatios(String classname, double stabilityRatio, int revisions) {
		this.classLevelStabilityRatios.put(classname, new Pair<Integer, Double>(revisions, stabilityRatio));
	}

	public String getName() {
		return name;
	}
	
	public Date getProjectStartDate() {
		return getEarliestCommitDate(getAllCommits());
	}
	
	public Date getProjectEndDate() {
		return getLastCommitDate(getAllCommits());
	}

	public Set<Commit> getAllCommits() {
		return commits;
	}
	
	public Set<String> getModules() {
		
		Set<String> modules = new HashSet<String>();
		
		for(Commit commit : getAllCommits()) {
			modules.addAll(commit.getModules());
		}
		return modules;
	}

	public Set<Author> getProjectAuthors() {

		Set<Author> authors = new HashSet<Author>();
		
		for(Commit commit : getAllCommits()) {
			authors.add(new Author(commit.getAuthor()));
		}
		return authors;
	}
	
	public Set<Author> getModuleAuthors(String module) {

		Set<Author> authors = new HashSet<Author>();
		
		for(Commit commit : getModuleCommits(module)) {
			authors.add(new Author(commit.getAuthor()));
		}
		return authors;
	}
	
	public Set<Commit> getModuleCommits(String module) {
		Set<Commit> commitsForModule = new HashSet<Commit>();
		
		for(Commit commit : commits) {
			if(commit.getModules().contains(module)) {
				commitsForModule.add(commit);
			}
		}
		
		return commitsForModule;
	}

	public Date getModuleStartDate(String module) {
		return getEarliestCommitDate(getModuleCommits(module));
	}
	
	public Date getModuleEndDate(String module) {
		return getLastCommitDate(getModuleCommits(module));
	}
	
	public double getIntraprojectStabilityRatio() {
		return intraprojectStabilityRatio;
	}
	
	public double getIntraprojectStabilityRatioModularised() {
		return intraprojectStabilityRatioModularised;
	}
	
	public Map<String, Pair<Integer, Double>> getClassLevelStabilityRatios() {
		return classLevelStabilityRatios;
	}
	
	public String toString() {
		return name;
	}

	private static Date getEarliestCommitDate(Set<Commit> commits) {

		Date earliestDate = null;

		for(Commit commit : commits) {
			if(commit.getDate()!=null && 
					(earliestDate == null || earliestDate.after(commit.getDate()))) {
				earliestDate = commit.getDate();
			}
		}
		return earliestDate;
	}

	private static Date getLastCommitDate(Set<Commit> commits) {
		Date latestDate = null;

		for(Commit commit : commits) {
			if(commit.getDate()!=null && 
					(latestDate == null || latestDate.before(commit.getDate()))) {
				latestDate = commit.getDate();
			}
		}
		return latestDate;
	}
	
}
