package analysis;


import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import jsc.independentsamples.MannWhitneyTest;
import loader.ProjectLists;
import loader.ReportReader;

public class ProjectPairMetricAnalysis {

	private enum RESULT {GT, LT, EQ};

	/**
	 * Initialised with the path to raw revision logs
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String[][] projectPairs = ProjectLists.STABILITY_REVISED;

		for (int i=0; i<projectPairs.length; i++) {
			ReportReader.ProjectMetrics earlierMetrics = ReportReader.readProjectMetrics(args[0]+projectPairs[i][0]+".txt");
			ReportReader.ProjectMetrics laterMetrics = ReportReader.readProjectMetrics(args[0]+projectPairs[i][1]+".txt");
			compareMetrics(projectPairs[i][0], projectPairs[i][1], earlierMetrics, laterMetrics);
		}
	}	

	private static void compareMetrics(String earlierProject, String laterProject, ReportReader.ProjectMetrics earlierMetrics, ReportReader.ProjectMetrics laterMetrics) {

		String metrics[] = {"CBO", "DIT", "LCOM", "NOC", "RFC", "WMC"};

		for(String metric : metrics) {			
			double[] earlierMetricDoubles = convert(earlierMetrics.classMetrics, metric);
			double[] laterMetricDoubles = convert(laterMetrics.classMetrics, metric);

			Arrays.sort(earlierMetricDoubles);
			Arrays.sort(laterMetricDoubles);

			if(earlierMetricDoubles.length>1 && laterMetricDoubles.length>1) {
				MannWhitneyTest test = new MannWhitneyTest(earlierMetricDoubles, laterMetricDoubles);
				boolean sigificant = test.getSP()<0.05d;
				RESULT mean = calcMean(earlierMetricDoubles, laterMetricDoubles);

				System.out.println(earlierProject+"\t"+
						laterProject+"\t"+
						metric+"\t"+
						sigificant+"\t"+
						mean+"\t"+
						earlierMetrics.LOC+"\t"+
						laterMetrics.LOC);
			}
		}
	}

	public static RESULT calcMean(double[] valueSet1, double[] valueSet2) {

		Mean mean1 = new Mean();
		double mean1Val = mean1.evaluate(valueSet1);	

		Mean mean2 = new Mean();
		double mean2Val = mean2.evaluate(valueSet2);	

		if(mean1Val>mean2Val) {
			//MEAN >>
			return RESULT.GT;
		} else if (mean1Val<mean2Val) {
			//MEAN <<
			return RESULT.LT;
		} else {
			//MEAN ==
			return RESULT.EQ;
		}
	}


	private static double[] convert(List<ReportReader.ClassMetric> classMetrics, String metric) {

		double[] doubles = new double[classMetrics.size()];

		for(int i=0; i<classMetrics.size(); i++) {
			if(metric.equals("CBO")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).CBO.trim());
			}
			if(metric.equals("DIT")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).DIT.trim());
			}
			if(metric.equals("LCOM")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).LCOM.trim());
			}
			if(metric.equals("NOC")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).NOC.trim());
			}
			if(metric.equals("RFC")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).RFC.trim());
			}
			if(metric.equals("WMC")) {
				doubles[i] = Double.parseDouble(classMetrics.get(i).WMC.trim());
			}
		}

		return doubles;
	}

}
