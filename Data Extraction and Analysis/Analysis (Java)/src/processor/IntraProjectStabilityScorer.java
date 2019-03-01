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

import model.Author;
import model.Commit;
import model.Project;
import model.StableProjectPair;

public class IntraProjectStabilityScorer {

	public static void execute(Set<StableProjectPair> stableProjects) {
		for(StableProjectPair pair : stableProjects) {
			execute(pair.getEarlierProject());
			execute(pair.getLaterProject());
		}
	}

	private static void execute(Project project) {

		//identify between developers in days using first and last commits as developer intervals
		Map<Author, Interval> authorIntervals = determineAuthorIntervals(project);
		Map<Date, List<Author>> authorDateProfile = determineAuthorTimeProfile(authorIntervals, project.getProjectStartDate(), project.getProjectEndDate());
		double stabilityRatio = calculateStabilityRatio(authorDateProfile, project);
		project.setIntraprojectStabilityRatio(stabilityRatio);
	}

	private static double calculateStabilityRatio(Map<Date, List<Author>> authorDateProfile, Project project) {

		double intervalDays =  ((resetToMidnight(project.getProjectEndDate()).getTime() - resetToMidnight(project.getProjectStartDate()).getTime()) / (1000 * 60 * 60 * 24) ) +1;
		double cumulativeOverlapDays = determineCumulativeOverlapDays(authorDateProfile);
		double authorCount = project.getProjectAuthors().size();
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

	private static Map<Author, Interval> determineAuthorIntervals(Project project) {

		Map<Author, Interval> authorIntervals = new HashMap<Author, Interval>();

		for(Author author : project.getProjectAuthors()) {
			Set<Commit> authorCommits = determineAuthorCommits(project.getAllCommits(), author);
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
