package processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import model.StableProjectPair;

public class ReportGenerator {

	public static void execute(Set<StableProjectPair> projectPairs) throws IOException {
		
		BufferedWriter b = new BufferedWriter(new FileWriter(new File("C:/PhD/projectPairs.txt")));
		
		for(StableProjectPair projectPair : projectPairs) {
			b.write(projectPair + "\n");
		}
		
		b.flush();
		b.close();
	}
}
