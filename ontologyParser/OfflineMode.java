package net.divineit.apps.security.ontologyParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class OfflineMode {
	
	public static void runOnDirectory(String target_dir) {
		File dir = new File(target_dir);
        File[] files = dir.listFiles();

        for (File f : files) {
            if(f.isFile()) {

                try {
                	String inputReport = new Scanner(new File(f.getAbsolutePath())).useDelimiter("\\Z").next();
                	// OntologyParser.use_manual_nlp_rules_extract(text, onlyPrintEdgesFlag, writeToFile);
                	OntologyParser op = new OntologyParser();
                	op.use_manual_nlp_rules_extract(inputReport, false, true );
                	ArrayList<String> resultList = op.getResultList();
                	File outputfile = new File(f.getParentFile()+ "\\" + f.getName().split(".txt")[0]+ "_op.csv");
            		FileWriter fw = new FileWriter(outputfile);
            		BufferedWriter bw = new BufferedWriter(fw);
                	for (String x: resultList) {
                		bw.write(x);
                	}
                	bw.close();
                	fw.close();
                	
                }
                catch(Exception ex) {
                	ex.printStackTrace();
                }
                finally {
                    }
                }
            }
        }
}

