package loader;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class CommitHistoryJsonLoader {

	private static final String COMMITS_URL= "https://storage.googleapis.com/google-code-archive/v2/code.google.com/$1/commits-page-$2.json";
	private static Date lastCommitDate = null;
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static void main(String[] args) throws Exception {

		for(String project : ProjectLists.PROJECT_FULL_LIST) {
			try {
			System.out.println(project +"\t"+ getCommitCount(project, 1) +"\t" +df.format(lastCommitDate));
			lastCommitDate = null;
			} catch(Exception e) {
			}
		}
	}

	public static int getCommitCount(String project, int pageNumber) throws Exception {

		int commitCount=0;
		JsonReader reader = Json.createReader(readUrl(project, pageNumber));
		JsonObject json = (JsonObject) reader.read();
		JsonNumber totalPages = (JsonNumber) json.get("TotalPages");
		JsonArray commits = (JsonArray) json.get("commits");

		for(JsonValue commit : commits) {
			JsonObject castedCommit = (JsonObject) commit;
			String commitDateString = castedCommit.getString("date");
			Date commitDate = df.parse(commitDateString);
			if(lastCommitDate==null || lastCommitDate.before(commitDate)) {
				lastCommitDate = commitDate;
			}
			commitCount++;
		}

		if(pageNumber<totalPages.intValue()) {
			commitCount += getCommitCount(project, pageNumber+1);
			
		}

		return commitCount;
	}

	private static Reader readUrl(String project, int page) throws Exception {

		String url = COMMITS_URL;
		url = url.replace("$1", project);
		url = url.replace("$2", new Integer(page).toString());

		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));

	}

}
