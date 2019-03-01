package processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import loader.CommitLoader;
import model.Author;
import model.Commit;
import model.CommitSet;

public class IntraProjectClassStabilityScorerTacticalStandalone {

	
	
	public static void main(String[] args) throws Exception {

		CommitLoader loader = new CommitLoader();
		CommitSet set = loader.loadCommitSet("D:/Users/ahmmadyoussef/Documents/PhD/raw_revision_logs/", new String[]{"wikipedia-map-reduce"});

		for(String projectName : set.getProjectNames()) {
			execute(set.getCommitsForProject(projectName), projectName);
		}
		
	}

	private static void execute(Set<Commit> commits, String project) {

		Map<String, List<Commit>> fileCommitMap =  generateFileCommitMap(commits);
		
		for(String file : fileCommitMap.keySet()) {
			List<Commit> commitsForFile = fileCommitMap.get(file);
			Set<Author> authors = determineAuthors(commitsForFile);
			Map<Author, Interval> authorIntervals = determineAuthorIntervals(commits, authors);
			
			Date startDate = determineStartDate(authorIntervals);
			Date endDate = determineEndDate(authorIntervals);
			
			Map<Date, List<Author>> authorDateProfile = determineAuthorTimeProfile(authorIntervals, startDate, endDate);
			double stabilityRatio = calculateStabilityRatio(authorDateProfile, startDate, endDate, authors.size());
			System.out.println(file+"\t"+stabilityRatio);
		}

	}

	private static Set<Author> determineAuthors(List<Commit> commits) {

		Set<Author> authors = new HashSet<>();
		
		for(Commit commit : commits) {
			authors.add(new Author(commit.getAuthor()));
		}

		return authors;
	}
	
	private static Map<String, List<Commit>> generateFileCommitMap(Set<Commit> commits) {
		
		Map<String, List<Commit>> fileCommitMap = new HashMap<>();

		for(Commit commit : commits) {
			if(commit.getFilenames()!=null) {
				for(String filename : commit.getFilenames()) {
					if(filename.endsWith(".java")) {
						filename = filename.substring(0, filename.length()-5);
						if(filename.contains("/")) {
							filename = filename.substring(filename.lastIndexOf("/")+1, filename.length());
						}
						if(!fileCommitMap.containsKey(filename)) {
							fileCommitMap.put(filename, new ArrayList<Commit>());
						}
						fileCommitMap.get(filename).add(commit);
						System.out.println(filename);
					}
				}
			}
		}
		
		return fileCommitMap;

	}
	private static double calculateStabilityRatio(Map<Date, List<Author>> authorDateProfile, Date startDate, Date endDate, int authorCount) {

		double intervalDays =  ((resetToMidnight(endDate).getTime() - resetToMidnight(startDate).getTime()) / (1000 * 60 * 60 * 24) ) +1;
		double cumulativeOverlapDays = determineCumulativeOverlapDays(authorDateProfile);
		double result = cumulativeOverlapDays/(intervalDays * (authorCount));

		return result;
	}

	private static long determineCumulativeOverlapDays(Map<Date, List<Author>> authorDateProfile) {

		long cumulativeDays = 0;

		for(Date date : authorDateProfile.keySet()) {
			if(!authorDateProfile.get(date).isEmpty()) {
				cumulativeDays += authorDateProfile.get(date).size();
			}
		}

		return cumulativeDays;
	}

	private static Map<Date, List<Author>> determineAuthorTimeProfile(Map<Author, Interval> authorIntervals, Date startDate, Date endDate) {

		Map<Date, List<Author>> authorTimeProfile = new HashMap<Date, List<Author>>();
		
		Calendar calendarPointer = Calendar.getInstance();
		calendarPointer.setTime(resetToMidnight(startDate));

		while(!calendarPointer.getTime().after(endDate)) {
			authorTimeProfile.put(calendarPointer.getTime(), determineActiveAuthors(authorIntervals, calendarPointer.getTime()));
			calendarPointer.add(Calendar.DATE, 1);
		}

		return authorTimeProfile;
	}

	private static Date determineStartDate(Map<Author, Interval> authorIntervals) {

		Date earliestDate = null;
		
		for(Interval interval : authorIntervals.values()) {
			if(earliestDate==null || interval.start.before(earliestDate)) {
				earliestDate = interval.start;
			}
		}
		
		return earliestDate;
	}
	
	private static Date determineEndDate(Map<Author, Interval> authorIntervals) {

		Date latestDate = null;
		
		for(Interval interval : authorIntervals.values()) {
			if(latestDate==null || interval.end.before(latestDate)) {
				latestDate = interval.end;
			}
		}
		
		return latestDate;
	}

	private static List<Author> determineActiveAuthors(Map<Author, Interval> authorIntervals, Date date) {

		List<Author> activeAuthors = new ArrayList<Author>();

		for(Author author : authorIntervals.keySet()) {
			Date authorStart = resetToMidnight(authorIntervals.get(author).start);
			Date authorEnd = resetToMidnight(authorIntervals.get(author).end);

			if(!date.before(authorStart) && !date.after(authorEnd)) {
				activeAuthors.add(author);
			}
		}

		return activeAuthors;
	}

	private static Date resetToMidnight(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		return cal.getTime();
	}

	private static Map<Author, Interval> determineAuthorIntervals(Set<Commit> commits, Set<Author> authors) {

		Map<Author, Interval> authorIntervals = new HashMap<Author, Interval>();

		for(Author author : authors) {
			Set<Commit> authorCommits = determineAuthorCommits(commits, author);
			authorIntervals.put(author, determineInterval(authorCommits));
		}

		return authorIntervals;
	}


	private static Interval determineInterval(Set<Commit> commits) {

		Date earliest = null;
		Date latest = null;

		for(Commit commit : commits) {
			if(earliest==null || commit.getDate().before(earliest)) {
				earliest = commit.getDate();
			}
			if(latest==null || commit.getDate().after(latest)) {
				latest = commit.getDate();
			}
		}
		return new Interval(earliest, latest);
	}

	private static Set<Commit> determineAuthorCommits(Set<Commit> commits, Author author) {

		Set<Commit> authorCommits = new HashSet<Commit>();

		for(Commit commit : commits) {
			Author commitAuthor = new Author(commit.getAuthor());
			if(commitAuthor.equals(author)) {
				authorCommits.add(commit);
			}
		}

		return authorCommits;
	}


	protected static class Interval {
		public Date start;
		public Date end;

		public Interval(Date start, Date end) {
			this.start = start;
			this.end = end;
		}

		public String toString() {
			return start.toString() +" - "+ end.toString();
		}
	}
}