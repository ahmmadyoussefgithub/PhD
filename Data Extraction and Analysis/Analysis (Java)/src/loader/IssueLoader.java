package loader;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class IssueLoader {

	private static final String ISSUE_URL= "https://storage.googleapis.com/google-code-archive/v2/code.google.com/$1/issues-page-$2.json";

	public static void main(String[] args) throws Exception {

		for(String project : ProjectLists.PROJECT_FULL_LIST) {
			try {
			System.out.println(project +"\t"+ getDefectCount(project, 1));
			} catch(Exception e) {
				System.out.println(project +"\t"+ "XX");
				
			}
		}
	}

	public static int getDefectCount(String project, int pageNumber) throws Exception {

		int defects=0;
		JsonReader reader = Json.createReader(readUrl(project, pageNumber));
		JsonObject json = (JsonObject) reader.read();
		JsonNumber totalPages = (JsonNumber) json.get("totalPages");
		JsonArray issues = (JsonArray) json.get("issues");

		for(JsonValue issue : issues) {
			JsonObject castedIssue = (JsonObject) issue;
			JsonArray labels = (JsonArray) castedIssue.get("labels");
			String status = ((JsonString) castedIssue.get("status")).getString();
			String type = labels.get(0).toString();

			if(type.contains("Type-Defect") && !status.contains("Duplicate") && !status.contains("Invalid")) {
				defects++;
			}
		}

		if(pageNumber<totalPages.intValue()) {
			defects += getDefectCount(project, pageNumber+1);
		}

		return defects;
	}

	private static Reader readUrl(String project, int page) throws Exception {

		String url = ISSUE_URL;
		url = url.replace("$1", project);
		url = url.replace("$2", new Integer(page).toString());

		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));

	}

}
