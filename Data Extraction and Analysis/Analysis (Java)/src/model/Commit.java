package model;



import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;



public class Commit implements Serializable {

	private static final long serialVersionUID = 1L;
	public static enum SOURCE_CONTROL{SVN, HG, GIT};

	private static DateFormat OUTPUT_DF = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss Z");

	private String project;
	private String author;
	private String revision;
	private Date date;
	private String description;
	private List<String> filenames;
	private Set<String> fileExtensions;
	private Set<String> modules;
	private SOURCE_CONTROL sourceControl;


	public Commit(String project, String author, String revision, Date date, SOURCE_CONTROL sourceControl) {
		this(project, author, revision, date, null, sourceControl, null);
	}
	
	public Commit(String project, String author, String revision, Date date, String description, SOURCE_CONTROL sourceControl, List<String> filenames) {
		this.project = project;
		this.author = author;
		this.date = date;
		this.revision = revision;
		this.sourceControl = sourceControl;
		this.description = description;
		this.filenames = filenames;
	}

	public Commit(String project, String author, String revision, Date date, String description, SOURCE_CONTROL sourceControl, Set<String> fileExtensions, Set<String> modules) {
		this.project = project;
		this.author = author;
		this.date = date;
		this.revision = revision;
		this.sourceControl = sourceControl;
		this.description = description;
		this.fileExtensions = fileExtensions;
		this.modules = modules;
	}

	public static DateFormat getOUTPUT_DF() {
		return OUTPUT_DF;
	}

	public String getProject() {
		return project;
	}

	public String getAuthor() {
		return author;
	}

	public String getRevision() {
		return revision;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}
	
	public List<String> getFilenames() {
		return filenames;
	}
	
	public void setFilename(List<String> filenames) {
		this.filenames = filenames;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public SOURCE_CONTROL getSourceControl() {
		return sourceControl;
	}

	public void setModules(Set<String> modules) {
		this.modules = modules;
	}
	
	public Set<String> getModules() {
		return modules;
	}
	
	public Set<String> getFileExtensions() {
		return fileExtensions;
	}

	@Override
	public String toString() {
		return project + " | " + author + " | " + revision + " | " + OUTPUT_DF.format(date)+ " | " +description+ " | " +listToString(filenames)+ " | " +sourceControl;
	}

	private String listToString(List<String> strings) {
		
		StringBuffer buffer = new StringBuffer();
		
		for(String string : strings) {
			buffer.append(string+" ");			
		}
		
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((project == null) ? 0 : project.hashCode());
		result = prime * result
				+ ((revision == null) ? 0 : revision.hashCode());
		result = prime * result
				+ ((sourceControl == null) ? 0 : sourceControl.hashCode());
		result = prime * result
				+ ((filenames == null) ? 0 : filenames.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Commit other = (Commit) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;

		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision)) {
			return false;
		}
		
		if (filenames == null) {
			if (other.filenames != null) {
				return false;
			}
		} else if (!filenames.containsAll(other.filenames) || !other.filenames.containsAll(filenames)) {
			return false;
		}

		if (modules == null) {
			if (other.modules != null) {
				return false;
			}
		} else if (!modules.containsAll(other.modules) || !other.modules.containsAll(modules)) {
			return false;
		}

		return true;
	}

}
