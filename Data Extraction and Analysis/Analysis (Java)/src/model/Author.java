package model;

import java.io.Serializable;

public class Author implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String rawName;
	
	public Author(String rawName) {
		this.rawName = rawName;
	}
	
	public String getRawName() {
		return this.rawName;
	}

	public int hashCode() {
		return stripNonEssentials(rawName.toLowerCase()).hashCode();
	}

	public boolean equals(Object obj) {

		String otherAuthor = ((Author) obj).rawName;
		try {			
			if(rawName.equals(otherAuthor)) {
				return true;
			} else {
				otherAuthor = otherAuthor.toLowerCase();
				String basicAuthor = stripNonEssentials(rawName.toLowerCase());
				String basicOtherAuthor = stripNonEssentials(otherAuthor.toLowerCase());
				if(basicAuthor.equals(basicOtherAuthor) && basicAuthor.length()>5) {

					return true;
				}
			}
			return false;
		}
		catch(Exception e) {
			System.out.println("Cannot compare: " +rawName+ ":" +otherAuthor);
			return false;
		}
	}

	private String stripNonEssentials(String input) {
		input = input.replaceAll("<.*>", "");
		if(input.contains("@")) {
			input = input.substring(0, input.indexOf("@"));
		}
		return input.trim();
	}
	
	public String toString() {
		return this.rawName;
	}
}
