/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.divineit.apps.security.ontologyParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import net.divineit.apps.security.modals.BM25;
import net.divineit.apps.security.modals.Bagofwords;
import net.divineit.apps.security.modals.Document_Terms;

/**
 *
 * @author ghusari
 */
public class TTPDrill_BM25 {

	/**
	 * Only change input path and output path in the main method below
	 * And then hit run
	 */
	public static void main(String[] args) {
		
		
		// creating documents from the lableld set
		// this uses the generate and raw directory in the project
		String inputPath = Constants.inputPath;
		String outputPath = Constants.outputPath;
		
		TTPDrill_BM25 bm25 = new TTPDrill_BM25();
		bm25.method1_writeFiles(inputPath, outputPath);
		
		// loading the documents of labeled set to serve as the ontology 
		OntologyParser op = new OntologyParser();
		op.loading_method(outputPath, Constants.nlpParser_key);
		
	}
	
	
	/**
	 * method1_writeFiles
	 * @param inputP
	 * @param outputP
	 */
	public void method1_writeFiles(String inputP, String outputP) { 
		// #############################################
		///////////// parsing ontology
		BufferedReader br = null;
		System.out.println("#####parsing ontology");
		try {

			File dir = new File(inputP);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.isDirectory())
						continue;
					// Do something with child
					br = new BufferedReader(new FileReader(child));
					String inputFilename = child.getName().split(".txt")[0];
					System.out.println("Processing file " + inputFilename);
					int exampleCount = 0;
					String st;
					while ((st = br.readLine()) != null) {

						// cleaning sentence
						st = st.replaceAll("--Example not given--", "");
						st = st.trim();
						if (st.equals(""))
							continue; // ignoring this sentence

						++exampleCount;
						System.out.println(inputFilename + "--" + exampleCount + "--" + st);
						TTPExtractor ex = new TTPExtractor(st);
						// extract 3 files, goal1, goal2 and verb_action

						ArrayList<String> vo = ex.getVoDoc();
						ArrayList<String> g1Doc = ex.getG1Doc();
						ArrayList<String> g2Doc = ex.getG2Doc();
						// create 3 files, goal1, goal2 and verb_action
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(outputP + inputFilename + "_ex" + exampleCount + "_TA.txt"));
						for (String each : vo)
							writer.write(each + " ");
						writer.close();
						writer = new BufferedWriter(
								new FileWriter(outputP + inputFilename + "_ex" + exampleCount + "_G1.txt"));
						for (String each : g1Doc)
							writer.write(each + " ");
						writer.close();
						writer = new BufferedWriter(
								new FileWriter(outputP + inputFilename + "_ex" + exampleCount + "_G2.txt"));
						for (String each : g2Doc)
							writer.write(each + " ");
						writer.close();
					}
				}
			} else {
				// Handle the case where dir is not really a directory.
				// Checking dir.isDirectory() above would not be sufficient
				// to avoid race conditions with another process that deletes
				// directories.
				System.out.println("-----------> Not really a directory");
			}
		} catch (Exception ex) {
			System.out.println("sth us wrong");
			ex.printStackTrace();
		}
	}
	
	

	public static double Calculate_BM25_Similarity_bagofwords_ankit(Document_Terms q, Document_Terms d,
			ArrayList<Bagofwords> bagswords, boolean normtf, int number_of_documents, double AvgDocLeng){ // sim between query and document d
		//System.out.println("arrived to-- Calculate_BM25_Similarity_bagofwords-- method");
		BM25 bmsim = new BM25();
		double bm25score_btw_q_doc = 0;

		for (String t : q.term_freq_hmap.keySet()) // for every term in query document
		{
			double bm25_score;

			// find t bagofwords
			ArrayList<String> synons = null;
			int pop = -1;// popularity
			//System.out.println("trying to get the popularity if the term "+ t+" in bag of	words lists -- method -- Calculate_BM25_Similarity_bagofwords");
			for (Bagofwords bow : bagswords) // for every array of synoyms in bagofwords array [list of lists where each
												// list is a set of sysnoyms]
			{
				if (bow.Contains(t)) {

					synons = bow.synonyms;
					pop = bow.popularity;
//					System.out.println("bag of words found of this term" + t +"and it is:" );
//					System.out.println(bow.synonyms.toArray().toString());
//					System.out.println(bow.synonyms.get(0));

					// testig
//					 System.out.println("Calculate_BM25_Similarity_bagofwords--bag of words for 	 term \""+t+"\" is found and it is :");
					 for(String x:synons)
					 {
//					 System.out.print(x+" ");
					 }
//					 System.out.println("");
//					 System.out.println(" its poularity is "+pop);
					// testig

					break;
				}
			}

			if (d.Contains_BagTerm(synons)) {
				///// popularity is affected by words by word or bag of words approach

				// System.out.println("this document contains one of the synonyms "+d.ID);
				if (pop == -1) {
					System.out.println("########### warning this popularity is -1 , sth is wrong, term: " + t);
				}
				// the following func returns the bm25 score for a query term and a document d
				// t is a term that comes from query

				if (!normtf) {
					bm25_score = bmsim.score(d.Get__Term_Frequency_inDoc_bagofwords(synons),
							number_of_documents, d.Get_Document_length(),AvgDocLeng, 
							q.Get__Term_Frequency_inDoc(t), pop);
				} else {
					bm25_score = bmsim.score(d.Get__Term_Frequency_inDoc_bagofwords(synons),
							number_of_documents, d.Get_Document_length(), AvgDocLeng,
							q.Get_Normalized_Term_Frequency_inDoc(t), pop);
				}

			} else {// if this term is not in doc then its bm25 similarity value is 0
				bm25_score = 0;
			}
			// aggregating the bm25 similarity values of all terms
			// now we use simple addition
			bm25score_btw_q_doc += bm25_score;
		}
		// System.out.println("bm25 score is "+bm25score_btw_q_doc);
		return bm25score_btw_q_doc;
	}

	
}
