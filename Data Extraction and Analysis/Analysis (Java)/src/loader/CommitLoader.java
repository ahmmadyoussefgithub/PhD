package loader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import loader.filter.CommitFilter;
import model.Commit;
import model.CommitSet;



public class CommitLoader {

	private Map<String, Set<Commit>> commits = new HashMap<String, Set<Commit>>();

	private DateFormat SVN_DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
	private DateFormat HG_DF = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy Z");
	private DateFormat GIT_DF = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy Z");
	private List<CommitFilter> commitFilters = new ArrayList<CommitFilter>();

	/**
	 * Initialised with the path to raw revision logs
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CommitLoader cl = new CommitLoader();
		CommitSet set = cl.loadCommitSet(args[0]);
		for(String projectName : set.getProjectNames()) {
			System.out.println(projectName +"\t"+ set.getCommitsForProject(projectName).size());
		}
	}
	
	public CommitLoader() {
	}

	public void addFilter(CommitFilter commitFilter) {
		this.commitFilters.add(commitFilter);
	}

	public CommitSet loadCommitSet(String path, String[] projects) throws Exception {

		for (int i=0; i<projects.length; i++) {
			
			if(i%1000==0) {
				System.out.println("CommitLoader - Loaded: " +i+"/"+projects.length);	
			}
			String project = projects[i];
			try {
				readFile(path, project);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}	

		System.out.println("Retrieved " +commits.size()+ " projects");
		return new CommitSet(Collections.unmodifiableMap(commits));
	}
	
	public CommitSet loadCommitSet(String path) throws Exception {

		return loadCommitSet(path, ProjectLists.TEAM_SIZE_PROJECT_SAMPLE);

	}
	
	public CommitSet loadAllProjects(String path) throws Exception {
		
		File dir = new File(path);
		return loadCommitSet(path, dir.list());
	}

	private void readFile(String path, String project) throws Exception {

		Commit.SOURCE_CONTROL sourceControl = null;
		FileInputStream fstream = new FileInputStream(path+project);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		boolean firstLine = true;

		while ((strLine = br.readLine()) != null)   {
			try {
				if(firstLine) {
					sourceControl = determineSourceControl(sourceControl, strLine);
					firstLine = false;
				}

				if(sourceControl==Commit.SOURCE_CONTROL.SVN) {
					if(strLine.startsWith("r")) {
						StringTokenizer tokenizer = new StringTokenizer(strLine, "|");
						if(tokenizer.countTokens()==4) {
							String revision = tokenizer.nextToken().trim();
							String author = tokenizer.nextToken().trim();
							String dateString = tokenizer.nextToken().trim();
							dateString = dateString.substring(0, dateString.indexOf("(")).trim();
							Date date = SVN_DF.parse(dateString);
							List<String> filenames = new ArrayList<String>();

							strLine = br.readLine();

							while(strLine!=null && !strLine.trim().equals("")) {
								if(!strLine.equals("Changed paths:")) {
									filenames.add((strLine.substring(strLine.lastIndexOf(" "), strLine.length())).trim());
								}
								strLine = br.readLine();
							}
							String description = br.readLine();
							addCommit(project, sourceControl, revision, author, date, description, filenames);
						} 
					}
				} else if(sourceControl==Commit.SOURCE_CONTROL.HG) {

					String author=null;
					String revision=null;
					Date date = null;
					List<String> filenames = new ArrayList<String>();
					String description = null;

					while(strLine!=null && (strLine.indexOf(":")!=-1)) {
						if(strLine.startsWith("changeset:")) {
							revision = strLine.substring(strLine.indexOf(":")+1, strLine.length());
						}
						if(strLine.startsWith("user:")) {
							author = strLine.substring(strLine.indexOf(":")+1, strLine.length()).trim();
						}
						if(strLine.startsWith("date:")) {
							String dateString = strLine.substring(strLine.indexOf(":")+1, strLine.length()).trim();
							date = HG_DF.parse(dateString);	
						}				
						if(strLine.startsWith("description:")) {
							description = br.readLine();	
						}				
						if(strLine.startsWith("files:")) {
							String filesString = strLine.substring(strLine.indexOf(":")+1, strLine.length()).trim();
							StringTokenizer tokenizer = new StringTokenizer(filesString, " ");
							while(tokenizer.hasMoreElements()) {
								filenames.add(tokenizer.nextToken().trim());
							}
						}
						strLine = br.readLine();
					}
					addCommit(project, sourceControl, revision, author, date, description, filenames);
				}
				else if(sourceControl==Commit.SOURCE_CONTROL.GIT)

					if(strLine.trim().startsWith("commit")) {
						String author=null;
						String revision=null;
						Date date = null;
						boolean summaryLineRead = false;
						List<String> filenames = new ArrayList<String>();
						String description = null;

						while((strLine!=null && (!strLine.trim().equals("") || !summaryLineRead))) {

							if(strLine.startsWith("commit")) {
								revision = strLine.substring(strLine.indexOf("commit")+6, strLine.length()).trim();
							} 
							else if(strLine.startsWith("Author:")) {
								author = strLine.substring(strLine.indexOf(":")+1, strLine.length()).trim();
							} 
							else if(strLine.startsWith("Date:")) {
								String dateString = strLine.substring(strLine.indexOf(":")+1, strLine.length()).trim();
								date = GIT_DF.parse(dateString);	
								br.readLine();
								description = br.readLine();
							}
							else if(strLine.contains("|")) {
								String filename = strLine.substring(0, strLine.indexOf("|")-1).trim();
								filenames.add(filename);
							}
							else if(strLine.contains("changed,")) {
								summaryLineRead = true;
							}
							strLine = br.readLine();
						}

						addCommit(project, sourceControl, revision, author, date, description, filenames);				
					}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(project+ " " +strLine);
			}
		}
		br.close();
		in.close();
		fstream.close();
	}

	private void addCommit(String project, Commit.SOURCE_CONTROL sourceControl, String revision, String author, Date date, String description, List<String> filenames) {

		if(author==null || author.contains("no author")) {
			author="generated_" +author+ "_" +Calendar.getInstance().getTimeInMillis();
		}

		//Commit commit = new Commit(null, author, null, date, null, null, determineFileExtensions(filenames), determineModules(filenames));
		Commit commit = new Commit(null, author, null, date, null, null, determineFileExtensions(filenames), determineModules(filenames));


		if(!commitFiltersApply(commit)) {
			if(commits.get(project)==null) {
				commits.put(project, new HashSet<Commit>());
			}
			commit.setFilename(filenames);
			commits.get(project).add(commit);
		}
	}

	private Set<String> determineFileExtensions(List<String> filenames) {

		Set<String> fileExtensions = new HashSet<String>();

		for(String filename : filenames) {
			if(filename.lastIndexOf(".")!=-1) {
				fileExtensions.add(filename.substring(filename.lastIndexOf(".")+1, filename.length()));
			}
		}
		return fileExtensions;
	}

	private Set<String> determineModules(List<String> filenames) {

		Set<String> modules = new HashSet<String>();

		for(String filename : filenames) {
			if(filename.startsWith("/trunk/")) {
				String module = extractModule(filename);
				if(module==null || module.equals("src") || module.equals("test")) {
					module = "trunk";
				}
				modules.add(module);
			}
		}

		return modules;
	}

	private String extractModule(String filename) {
		if(filename.indexOf('/', 8)==-1) {
			return null;
		}
		return filename.substring(7, filename.indexOf('/', 8));
	}

	private boolean commitFiltersApply(Commit commit) {

		for(CommitFilter commitFilter : commitFilters) {
			if(commitFilter.filter(commit)) {
				return true;
			}
		}
		return false;
	}

	private Commit.SOURCE_CONTROL determineSourceControl(
			Commit.SOURCE_CONTROL sourceControl, String strLine) throws Exception {
		if(strLine.startsWith("-----------------")) {
			sourceControl = Commit.SOURCE_CONTROL.SVN;
		} else if(strLine.startsWith("changeset:")) {
			sourceControl = Commit.SOURCE_CONTROL.HG;		
		} else if(strLine.startsWith("commit")) {
			sourceControl = Commit.SOURCE_CONTROL.GIT;		
		} else {
			System.out.println(strLine);
			throw new Exception();
		}
		return sourceControl;
	}


}
