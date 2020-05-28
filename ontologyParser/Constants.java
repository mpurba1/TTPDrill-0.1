package net.divineit.apps.security.ontologyParser;

public class Constants {
	public static final String space = " ";
	
	public static final String utf8 = "UTF-8";
	public static final String TA_suffix = "_TA"; 
	public static final String G1_suffix = "_G1";
	public static final String G2_suffix = "_G2";
	public static final String blank = "";
	public static final String nlpParser_key = "NLP Parser";
	public static final String wordSpacing_key = "Word or Token Spacing";
	public static final String manualNlp_key = "NLP - manual rules";
	public static final String edgeTree_key = "Print edge tree";
	public static final String noResult = "No Results found! in the Ontology";
	public static final String suggestion = "Try changing threshold or stem filter";
	public static final String yes = "YES";
	public static final String no = "NO";
	
	
	/** Directories **/
	//Reading Ontology
	public static final String inputDir = "ontology"; // ".\\ontology";
	
	public static final String nlpPath = "NLPpathes-Ghaith_1.csv"; // ".\\NLPpathes-Ghaith_1.csv";
	
	public static final String target_dir = "target"; // ".\\target";
	
	
	// These paths are used to generate Ontology from raw directory in the project
	// We do not need these unless we want to generate fresh Ontology from Scratch
	// Refer class TTPDrill_BM25.java
	public static final String inputPath = ".\\raw";
	public static final String outputPath = ".\\generate";
}
