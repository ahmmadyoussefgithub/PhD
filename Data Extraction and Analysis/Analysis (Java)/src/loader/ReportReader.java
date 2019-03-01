package loader;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportReader {

	/**
	 * Initialised with the path to stability reports
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String[] projects = ProjectLists.PROJECT_FULL_LIST;

		for (int i=0; i<projects.length; i++) {
			ProjectMetrics metrics = readProjectMetrics(args[0]+projects[i]+".txt");

			List<String> cboMetrics = convert(metrics.classMetrics, "CBO");
			List<String> ditMetrics = convert(metrics.classMetrics, "DIT");
			List<String> lcomMetrics = convert(metrics.classMetrics, "LCOM");
			List<String> nocMetrics = convert(metrics.classMetrics, "NOC");
			List<String> rfcMetrics = convert(metrics.classMetrics, "RFC");
			List<String> wmcMetrics = convert(metrics.classMetrics, "WMC");

			for(int j=0; j<cboMetrics.size(); j++) {

				System.out.println(projects[i] +"\t"+ metrics.classMetrics.get(i).className +"\t"+
						metrics.LOC +"\t"+ 
						metrics.LOCOM +"\t"+ 
						cboMetrics.get(j) +"\t"+
						ditMetrics.get(j) +"\t"+
						lcomMetrics.get(j) +"\t"+
						nocMetrics.get(j) +"\t"+
						rfcMetrics.get(j) +"\t"+
						wmcMetrics.get(j));

			}

		}
	}

	private static List<String> convert(List<ClassMetric> classMetrics, String metric) {

		List<String> metrics = new ArrayList<String>();

		for(int i=0; i<classMetrics.size(); i++) {
			if(metric.equals("CBO")) {
				metrics.add(classMetrics.get(i).CBO.trim());
			}
			if(metric.equals("DIT")) {
				metrics.add(classMetrics.get(i).DIT.trim());
			}
			if(metric.equals("LCOM")) {
				metrics.add(classMetrics.get(i).LCOM.trim());
			}
			if(metric.equals("NOC")) {
				metrics.add(classMetrics.get(i).NOC.trim());
			}
			if(metric.equals("RFC")) {
				metrics.add(classMetrics.get(i).RFC.trim());
			}
			if(metric.equals("WMC")) {
				metrics.add(classMetrics.get(i).WMC.trim());
			}
		}

		return metrics;
	}

	public static ProjectMetrics readProjectMetrics(String filename) throws Exception {

		ProjectMetrics projectMetric = new ProjectMetrics();
		readProjectLevelMetrics(filename, projectMetric);
		readClassLevelMetrics(filename, projectMetric);
		readClassSizes(filename, projectMetric);
		return projectMetric;
	}

	private static void readProjectLevelMetrics(String filename, ProjectMetrics projectMetric) throws Exception {
		boolean processMetrics = false;

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = reader.readLine();
		while(line!=null) {
			if(!processMetrics) {
				processMetrics = isProjectLevelMetricsHeader(line);
			} else if(isLOC(line)) {
				projectMetric.LOC = Long.parseLong(lastSnippet(line).trim());
			} else if(isLOCOM(line)) {
				projectMetric.LOCOM = Long.parseLong(lastSnippet(line).trim());
			} else if(isClassCount(line)) {
				projectMetric.classCount = Integer.parseInt(lastSnippet(line).trim());
			} 
			line = reader.readLine();
		}
		reader.close();
	}

	public static void readClassLevelMetrics(String filename, ProjectMetrics projectMetric) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		List<ClassMetric> classMetrics = new ArrayList<ClassMetric>();

		boolean processMetrics = false;
		boolean nextReportEncountered = false;

		String line = reader.readLine();
		while(line!=null && !nextReportEncountered) {
			if(!processMetrics) {
				processMetrics = isMetricsReportHeader(line);
			} else if (isPostMetricsReportHeader(line)) {
				nextReportEncountered = true;
			} else if (isClassName(line)){
				classMetrics.add(new ClassMetric(removeLastCharactor(line), lastSnippet(reader.readLine()), lastSnippet(reader.readLine()), lastSnippet(reader.readLine()), lastSnippet(reader.readLine()),
						lastSnippet(reader.readLine()), lastSnippet(reader.readLine()), lastSnippet(reader.readLine()), lastSnippet(reader.readLine()), lastSnippet(reader.readLine())));
			}
			line = reader.readLine();
		}
		reader.close();

		projectMetric.classMetrics = classMetrics;
	}

	public static void readClassSizes(String filename, ProjectMetrics projectMetric) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		boolean processMetrics = false;
		boolean nextReportEncountered = false;

		String line = reader.readLine();
		while(line!=null && !nextReportEncountered) {
			if(!processMetrics) {
				processMetrics = isFileAverageMetricsHeaders(line);
			} else if (isPostMetricsReportHeader(line)) {
				nextReportEncountered = true;
			} else if (isJavaFile(line)){
				String className = line.substring(0, line.indexOf(".java"));
				skip(4, reader);
				String locLine = reader.readLine();
				String LOC = locLine.substring(locLine.lastIndexOf(" ", locLine.length()));
				ClassMetric classMetric = findClassName(projectMetric, className);
				if(classMetric!=null) {
					classMetric.LOC = LOC;					
				}
			}
			line = reader.readLine();
		}
		reader.close();

	}

	private static ClassMetric findClassName(ProjectMetrics projectMetric, String className) {

		for(ClassMetric classMetric : projectMetric.classMetrics) {
			if(classMetric.className.endsWith(className)) {
				return classMetric;
			}
		}

		return null;
	}

	private static void skip(int skipCount, BufferedReader reader) throws IOException {

		for(int i=0; i<skipCount; i++) {
			reader.readLine();
		}

	}

	private static String lastSnippet(String line) {
		String trimmedString = line.trim();
		int lastIndexOfSpace = trimmedString.lastIndexOf(" ");
		return trimmedString.substring(lastIndexOfSpace, trimmedString.length());
	}

	private static String removeLastCharactor(String line) {
		String trimmedString = line.trim();
		return trimmedString.substring(0, trimmedString.length()-1);
	}

	private static boolean isPostMetricsReportHeader(String line) {
		return line.trim().equals("Uninitialized Items Report");
	}

	private static boolean isProjectLevelMetricsHeader(String line) {
		return line.trim().equals("Project Metrics Summary Report");
	}

	private static boolean isMetricsReportHeader(String line) {
		return line.trim().equals("Class OO Metrics Report");
	}

	private static boolean isFileAverageMetricsHeaders(String line) {
		return line.trim().equals("File Average Metrics Report");
	}

	private static boolean isLOC(String line) {
		return line.trim().startsWith("Lines Code: ");
	}

	private static boolean isLOCOM(String line) {
		return line.trim().startsWith("Lines Comment: ");
	}	

	private static boolean isClassCount(String line) {
		return line.trim().startsWith("Classes: ");
	}

	private static boolean isClassName(String line) {
		String trimmedLine = line.trim();
		return !trimmedLine.startsWith("LCOM ") && !trimmedLine.startsWith("DIT ") && !trimmedLine.startsWith("IFANIN ") && !trimmedLine.startsWith("CBO ") && !trimmedLine.startsWith("NOC ") 
				&& !trimmedLine.startsWith("RFC ") && !trimmedLine.startsWith("NIM ") && !trimmedLine.startsWith("NIV ") && !trimmedLine.startsWith("WMC ") && !trimmedLine.equals("Class OO Metrics Report")
				&& !trimmedLine.startsWith("===")  && !trimmedLine.startsWith("Page ")  && !trimmedLine.isEmpty();
	}

	private static boolean isJavaFile(String line) {
		return line.endsWith(".java");
	}

	public static class ProjectMetrics {
		public long LOC;
		public long LOCOM;
		public int classCount;
		public List<ClassMetric> classMetrics;		
	}
	public static class ClassMetric {

		public String className;
		public String LCOM;
		public String DIT;
		public String IFANIN;
		public String CBO;
		public String NOC;
		public String RFC;
		public String NIM;
		public String NIV;
		public String WMC;
		public String LOC;

		public ClassMetric(String className, String LCOM, String DIT, String IFANIN, String CBO,
				String NOC, String RFC, String NIM, String NIV, String WMC) {
			this.className = className;
			this.LCOM = LCOM;
			this.DIT = DIT;
			this.IFANIN = IFANIN;
			this.CBO = CBO;
			this.NOC = NOC;
			this.RFC = RFC;
			this.NIM = NIM;
			this.NIV = NIV;
			this.WMC = WMC;
		}

		@Override
		public String toString() {
			return className + "\n" +LCOM + "\t" + DIT + "\t" + IFANIN + "\t" + CBO + "\t" + NOC + "\t" + RFC
					+ "\t" + NIM + "\t" + NIV + "\t" + WMC + "\t" + LOC;
		}

	}

}
