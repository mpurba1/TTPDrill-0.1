package net.divineit.apps.security.ontologyParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class TTPExtractor{
	
	CoreSentence sentence;

/**	
public static void main(String[] args) throws IOException {
		// change the sentence and hit run 
		String line = "It can also steal tokens to acquire administrative privileges.";
		TTPExtractor mat = new TTPExtractor(line);
		mat.readMasterData("C:\\Users\\still\\Desktop\\saved data\\NLPpathes-Ghaith_1.csv");
		ArrayList<String> sp = mat.extractRouteForAllWords(mat.sentence) ;
		mat.printAll(sp);
	}
	**/
	 
	
	/**
	 * All the background work is below
	 */
	
	
	StanfordCoreNLP pipeline;
	CoreDocument document;
	SemanticGraph dependencyParse;
	ArrayList<String> verbs_objects;
	ArrayList<String> goal1List;
	ArrayList<String> goal2List;
	private final String goal1 = "goal1";
	private final String goal2 = "goal2";
	private ArrayList<String> voDoc = new ArrayList<String>();
	private ArrayList<String> g1Doc = new ArrayList<String>();
	private ArrayList<String> g2Doc = new ArrayList<String>();
	String[] filter = new String[] {"det","mark"};
	
	
		
	public TTPExtractor(String text) {
		
		// writing some text cleaning rules here, to avoid issues
		text = text.replaceAll("--Example not given--", "");
		text = text.replaceAll("\\+", "and");
		text = text.trim();
		
		
		if (text.equals("") || text == null) {
			return;
		}
		
		
		// create a document object
		document = new CoreDocument(text);
		// set up pipeline properties
		Properties props = new Properties();
		// set the list of annotators to run
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");
		props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
		// build pipeline
		pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(document);
		try {
			String nlpPathFilePath = getClass().getResource("/app/ttpdrill/" + Constants.nlpPath).getPath();
			readMasterData(nlpPathFilePath);
		} catch (IOException e) {
			System.out.println("Exception reading master data csv");
			System.exit(1);
		}
		for(CoreSentence cs: document.sentences()) {
			dependencyParse = cs.dependencyParse();
			ArrayList<String> sp = extractRouteForAllWords(cs);
			printAll(sp);
		}
	}
	
	
	
	private void readMasterData(String path) throws IOException {
		if (verbs_objects!=null && goal1List!=null && goal2List!=null)
			return;
		verbs_objects = new ArrayList<String>();
		goal1List = new ArrayList<String>();
		goal2List = new ArrayList<String>();
		FileReader filereader = new FileReader(path);
        // create csvReader object passing
        // file reader as a parameter
        BufferedReader br = new BufferedReader(filereader);
        String line = null;
		while ((line = br.readLine()) != null) {
			String[] fields = line.split(",");
			String step = fields[3];
			String label = fields[1].trim().toString();
			if (label.equals(goal1))
				goal1List.add(step.trim());
			else if (label.equals(goal2))
				goal2List.add(step.trim());
			else verbs_objects.add(step.trim());
			
        }
        	br.close();
            System.out.println();
	}
	
	


/**
 * 
 */
	
	
	protected ArrayList<String> extractRouteForAllWords(CoreSentence sentence) {
		
		//getting array of words
		List<CoreLabel> tokens = sentence.tokens();
		// filter stop words and commas, etc. (keep nouns, adjectives, aux, verbs)
		ArrayList<String> sentencePathes = new ArrayList<String>();
		
		for( CoreLabel cl1 : tokens) {
			String src = cl1.toString().split("-")[0];
			for( CoreLabel cl2 : tokens) {
				String tgt = cl2.toString().split("-")[0];
				if(src.equalsIgnoreCase(tgt) || src.equals(".") || tgt.equals("."))
						continue;
				IndexedWord source, target;
				try {
					source = dependencyParse.getNodeByWordPattern(src);
					target = dependencyParse.getNodeByWordPattern(tgt);
				} catch(Exception ex) {
					//System.out.println("Exception in-----> source:"+src + " target:"+tgt );
					continue;
				}
					
					
				StringBuilder src_dest_path = new StringBuilder();
				List<SemanticGraphEdge> edgeList = dependencyParse.getShortestDirectedPathEdges(source, target);
				if (edgeList!=null && edgeList.size()>0) {
					for(SemanticGraphEdge edge: edgeList) {
						src_dest_path.append(edge.getRelation() + "->");//  e.g., nmod:as -> nmod:of -> compound -> (gov is verb and dep is ob or goal)
					}
					
					sentencePathes.add(src+","+tgt+","+src_dest_path);
					src_dest_path.setLength(0);
				
			}		
		}
	}
		if (sentencePathes.size()==0) {return null;}
		else
		{return sentencePathes;}
	}
	
	
	private void printAll(ArrayList<String> allPairs) {
		if (allPairs==null)
			return;
		for(String eachPair : allPairs) {
			String[] tokens = eachPair.split(",");
			String path = tokens[2];
			String[] pathArray = path.split("->");
			//last path should not be in filtered path like det or mark
			String lastElement = pathArray[pathArray.length-1];
			for (String exclude : filter) {
				if (!lastElement.equalsIgnoreCase(exclude)) {
					
					// collecting TA 
					if (verbs_objects.contains(path)) {
						if (!voDoc.contains(tokens[0]))
							voDoc.add(tokens[0]);
						if (!voDoc.contains(tokens[1]))
							voDoc.add(tokens[1]);
					}
					
					//collecting goal 1
					if (goal1List.contains(path)) {
						if (!g1Doc.contains(tokens[0]))
							g1Doc.add(tokens[0]);
						if (!g1Doc.contains(tokens[1]))
							g1Doc.add(tokens[1]);
					}
					
					//collecting goal 2
					if (goal2List.contains(path)) {
						if (!g2Doc.contains(tokens[0]))
							g2Doc.add(tokens[0]);
						if (!g2Doc.contains(tokens[1]))
							g2Doc.add(tokens[1]);
					}
					
						
					/**if (goal1List.contains(path))
						g1Doc.append(eachPair+"\n");
					if (goal2List.contains(path))
						g2Doc.append(eachPair+"\n"); **/
				}
			}
		
		
		/**System.out.println("\n --- Goal 1 ---");
			System.out.println(g1Doc);
		System.out.println("\n --- Goal 2 ---");
			System.out.println(g2Doc);
		System.out.println("\n --- Verbs and Objects ---");
			System.out.println(voDoc);**/
		
		}
	}



	
	
	
	/**
	 * Getters and Setters
	 */
	
	public ArrayList<String> getVoDoc() {
		return voDoc;
	}



	public void setVoDoc(ArrayList<String> voDoc) {
		this.voDoc = voDoc;
	}



	public ArrayList<String> getG1Doc() {
		return g1Doc;
	}



	public void setG1Doc(ArrayList<String> g1Doc) {
		this.g1Doc = g1Doc;
	}



	public ArrayList<String> getG2Doc() {
		return g2Doc;
	}



	public void setG2Doc(ArrayList<String> g2Doc) {
		this.g2Doc = g2Doc;
	}
	
	
	
}
