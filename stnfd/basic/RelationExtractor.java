package net.divineit.apps.security.stnfd.basic;

import java.util.ArrayList; 
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import net.divineit.apps.security.ontologyParser.Constants;
import net.divineit.apps.security.ontologyParser.OntologyParser;
import net.divineit.apps.security.ontologyParser.Utilities;
import net.divineit.apps.security.modals.Document_Terms;

public class RelationExtractor {

	private static final String space = CommonConstants.space;
	private static List<Sentence> sentenceList = null;
	private static boolean isStemming = true;
	static StanfordCoreNLP pipeline;
	CoreDocument document;
	Sentence st;
	// flag to use later
	boolean containsDobj = false;
	static boolean iswriteToFile = false;
	private ArrayList<String> resultList;

	
	public RelationExtractor(Sentence sentence, boolean onlyPrintEdgesFlag, boolean writeToFileFlag) {
		initializeStanford();
		System.out.println("\n\n Sentence: " + sentence.getOriginalSentence());
		findImportantStuff(sentence.getOriginalSentence(), onlyPrintEdgesFlag);
		iswriteToFile = writeToFileFlag;
		
		/**
		 * Rule Engine below
		 */
		
		// Rule 1: Look for Dobj
		lookForRelations(sentence, CommonConstants.dobj);
		
		// Rule 2: Look for Nsubjpass
		lookForRelations(sentence, CommonConstants.nsubjpass);
		
		// Rule 3: If Dobj does not exist, look for nmods 		
		if (!containsDobj) {
			lookForRelations(sentence, CommonConstants.nmod);
		}

//		if (!containsDobj) {
//			lookForRelations(sentence, CommonConstants.compound);
//		}
		
	}

	
	
	/**
	 * initializeStanford
	 */
	private static void initializeStanford() {
		// set up pipeline properties
		Properties props = new Properties();
		// set the list of annotators to run
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");
		props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
		// build pipeline
		pipeline = new StanfordCoreNLP(props);
		
	}
	

	/**
	 * findImportantStuff,
	 * param pipeline, st
	 * @param
	 * @param
	 */

	private void findImportantStuff(String text, boolean onlyPrintEdgesFlag) {
		sentenceList = new ArrayList<>();
		System.out.println("findImportantStuff()");
		st = new Sentence();
		st.setText(text);
		// create a document object
		document = new CoreDocument(text);
		pipeline.annotate(document);
		CoreSentence sentence = document.sentences().get(0);

		SemanticGraph dependencyParse = sentence.dependencyParse();
		for (SemanticGraphEdge edge : dependencyParse.edgeIterable()) {
			
			// This condition only prints the edges and then loops out
			if (onlyPrintEdgesFlag) {
				System.out.println(edge);
				continue;
			}
			
			GrammaticalRelation relation = edge.getRelation();
			String relationString = relation.toString();
			IndexedWord source = edge.getSource();
			IndexedWord target = edge.getTarget();

			// Rule 1
			// Get all the Dobjs

			if (relationString.equals(CommonConstants.dobj)) {
				SemanticGraphEdge onlyDobjEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getDobjEdgeList().add(onlyDobjEdge);
			}

			// Rule 2
			// Find the compound
			if (relationString.equals(CommonConstants.compound)) {
				SemanticGraphEdge onlyCmpoundEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getCompEdgeList().add(onlyCmpoundEdge);
			}
			
			// Rule 3
			// Find all the amods
			if (relationString.equals(CommonConstants.amod)) {
				SemanticGraphEdge onlyAmodEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getAmodEdgeList().add(onlyAmodEdge);
			}
			
			
			// Rule 4
			// Find all the nsubjpass
			if (relationString.equals(CommonConstants.nsubjpass)) {
				SemanticGraphEdge onlyNsubjPassEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getNsubjpassEdgeList().add(onlyNsubjPassEdge);
			}
			
			
			// Rule 5
			// Find all the nmods
			if (relationString.contains(CommonConstants.nmod)) {
				SemanticGraphEdge onlyNmodEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getNmodEdgeList().add(onlyNmodEdge);
			}
			
			// Rule 6
			// Find all the conjs
			if (relationString.contains(CommonConstants.conj)) {
				SemanticGraphEdge onlyConjEdge = new SemanticGraphEdge(source, target, relation, 0.0, false);
				st.getConjEdgeList().add(onlyConjEdge);
			}	

			sentenceList.add(st);
		}
	}
	
	
	
	/**
	 * printer
	 * param strings
	 * @param
	 */
	@SafeVarargs
	private static String printer(String... relations) {
		StringBuilder result = new StringBuilder();
			for (String string: relations) {
				if (string!=null && !string.equals(space.trim())) {
					//System.out.print(string + space);
					result.append(string + space);
				}
			}
		System.out.println();
		return result.toString().trim();
	}
	
	
	
	/**
	 * Look for relations
	 */
	private void lookForRelations(Sentence sent, String relation) {
			List<SemanticGraphEdge> edgeList = null;
			
			switch (relation) {
			case CommonConstants.dobj: edgeList = st.getDobjEdgeList();
				break;
			
			case CommonConstants.nsubjpass: edgeList = st.getNsubjpassEdgeList();
				break;
				
			case CommonConstants.nmod: edgeList = st.getNmodEdgeList();

				break;

//			case CommonConstants.compound: edgeList = st.getCompEdgeList();
//					break;
				
			default: edgeList = null;
				break;
			}
			
			
			if (edgeList == null || edgeList.size()==0) return;

			System.out.println("Loogforrelation : edgelist size" + edgeList.size());
			for (SemanticGraphEdge edge : edgeList) {
				st.action = getNodeValue(edge.getSource());
				st.objct = getNodeValue(edge.getTarget());
				
				// Look for compound relationship

				System.out.println("Loogforrelation : st list getCompEdgeList() size" + st.getCompEdgeList().size());
				for (SemanticGraphEdge compoundEdge : st.getCompEdgeList()) {
					IndexedWord cmpndSource = compoundEdge.getSource();
					IndexedWord cmpndTarget = compoundEdge.getTarget();
					if (cmpndSource.equals(edge.getTarget())) {
						if (st.cmpnd == null || st.cmpnd.equals(Constants.blank))
							st.cmpnd = getNodeValue(cmpndTarget);
						else if (st.cmpnd2 == null || st.cmpnd2.equals(Constants.blank))
							st.cmpnd2 = getNodeValue(cmpndTarget);
						else st.cmpnd3 = getNodeValue(cmpndTarget);
						
					}
				}
			/*
				// Look for amod relationship
				
				for (SemanticGraphEdge amodEdge : st.getAmodEdgeList()) {
					IndexedWord amodSource = amodEdge.getSource();
					IndexedWord amodTarget = amodEdge.getTarget();
					if (amodSource.equals(edge.getTarget())) {
						st.amod = (getNodeValue(amodTarget));
					}
				}
				
			*/
				if (relation.equals(CommonConstants.dobj)) {
						// Look for nmod relationship
						for (SemanticGraphEdge nmodEdge : st.getNmodEdgeList()) {
							IndexedWord nmodSource = nmodEdge.getSource();
							IndexedWord nmodTarget = nmodEdge.getTarget();
							if (nmodSource.equals(edge.getSource())
									|| nmodSource.equals(edge.getTarget())) {
								st.nmod = (getNodeValue(nmodTarget));
								//now look for compound of the nmod
								for (SemanticGraphEdge compoundEdge : st.getCompEdgeList()) {
									if (st.nmod.equals(getNodeValue(compoundEdge.getSource())))
										st.nmodCmpnd = (getNodeValue(compoundEdge.getTarget()));
								}
							}
						}
				/*
						// Look for conj relationship
						for (SemanticGraphEdge conjEdge : st.getConjEdgeList()) {
							IndexedWord conjSource = conjEdge.getSource();
							IndexedWord conjTarget = conjEdge.getTarget();
							if (conjSource.equals(edge.getSource()))
								st.conj = (getNodeValue(conjTarget));
							if (conjTarget.equals(edge.getSource()))
								st.conj = (getNodeValue(conjSource));
						}
				*/
				
				}
				
				// finally print relationship
				
				if (st.cmpnd != null || st.amod!=null || st.nmod!=null || st.conj!=null 
						|| (st.action !=null && st.objct != null)) {
					// finally print sentence
					String result = printer(st.action, st.conj, st.amod,st.cmpnd, st.cmpnd2, st.cmpnd3, st.objct, st.nmod, st.nmodCmpnd);
					OntologyParser.queryOntology(sent, result, getDocTermOutput(st.action,st.amod,
							st.cmpnd, st.cmpnd2, st.cmpnd3, st.objct, st.nmod, st.nmodCmpnd));
					
					// if write to file is true
					if (iswriteToFile) {
						
					}
					st.cmpnd = null;
					st.cmpnd2 = null;
					st.cmpnd3 = null;
					st.amod = null;
					st.nmod = null;
					st.nmodCmpnd = null;
					st.conj = null;
				}
			}
	}
	
	
	
	/**
	 * getNodeValue
	 */
	private String getNodeValue(IndexedWord iw) {
		return iw.toString().split("/")[0];
	}
	
	/**
	 * Create a document term from the output
	 */
	@SafeVarargs
	private final Document_Terms getDocTermOutput(String... output) {
		Document_Terms dt = new Document_Terms();
			for (String x: output) {
				if (x!=null && !x.equals(space.trim())) {
					if (isStemming)
						x= Utilities.stem(x, false);
					dt.Add_Term_(x);
				}
			}
		return dt;
	}


}
