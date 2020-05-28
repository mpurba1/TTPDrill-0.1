package net.divineit.apps.security.stnfd.basic;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class Sentence{
	
		//NLP fields
		String action;
		public int getStartPos() {
			return startPos;
		}
		public void setStartPos(int startPos) {
			this.startPos = startPos;
		}
		public int getEndPos() {
			return endPos;
		}
		public void setEndPos(int endPos) {
			this.endPos = endPos;
		}
		String objct;
		private String objct2;
		String cmpnd;
		String cmpnd2;
		String cmpnd3;
		private String goal1;
		private String goal2;
		String nmod;
		String nmodCmpnd;
		String conj;
		String amod;
		
		// Text fields
		private int startPos;
		private int endPos;
		
		private String originalSentence;
		
		public String getOriginalSentence() {
			return originalSentence;
		}
		public void setOriginalSentence(String originalSentence) {
			this.originalSentence = originalSentence;
		}
		public String getObjct2() {
			return objct2;
		}
		public void setObjct2(String objct2) {
			this.objct2 = objct2;
		}
		public String getGoal1() {
			return goal1;
		}
		public void setGoal1(String goal1) {
			this.goal1 = goal1;
		}
		public String getGoal2() {
			return goal2;
		}
		public void setGoal2(String goal2) {
			this.goal2 = goal2;
		}
		private String text;
		private List<SemanticGraphEdge> dobjEdgeList = new ArrayList<SemanticGraphEdge>();
		private List<SemanticGraphEdge> compEdgeList = new ArrayList<SemanticGraphEdge>();
		private List<SemanticGraphEdge> amodEdgeList = new ArrayList<SemanticGraphEdge>();
		private List<SemanticGraphEdge> nsubjpassEdgeList = new ArrayList<SemanticGraphEdge>();
		private List<SemanticGraphEdge> nmodEdgeList = new ArrayList<SemanticGraphEdge>();
		private List<SemanticGraphEdge> conjEdgeList = new ArrayList<SemanticGraphEdge>();
		public List<SemanticGraphEdge> getDobjEdgeList() {
			return dobjEdgeList;
		}
		public void setDobjEdgeList(List<SemanticGraphEdge> dobjEdgeList) {
			this.dobjEdgeList = dobjEdgeList;
		}
		public List<SemanticGraphEdge> getCompEdgeList() {
			return compEdgeList;
		}
		public void setCompEdgeList(List<SemanticGraphEdge> compEdgeList) {
			this.compEdgeList = compEdgeList;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public List<SemanticGraphEdge> getAmodEdgeList() {
			return amodEdgeList;
		}
		public void setAmodEdgeList(List<SemanticGraphEdge> amodEdgeList) {
			this.amodEdgeList = amodEdgeList;
		}
		public List<SemanticGraphEdge> getNsubjpassEdgeList() {
			return nsubjpassEdgeList;
		}
		public void setNsubjpassEdgeList(List<SemanticGraphEdge> nsubjpassEdgeList) {
			this.nsubjpassEdgeList = nsubjpassEdgeList;
		}
		public List<SemanticGraphEdge> getNmodEdgeList() {
			return nmodEdgeList;
		}
		public void setNmodEdgeList(List<SemanticGraphEdge> nmodEdgeList) {
			this.nmodEdgeList = nmodEdgeList;
		}
		public List<SemanticGraphEdge> getConjEdgeList() {
			return conjEdgeList;
		}
		public void setConjEdgeList(List<SemanticGraphEdge> conjEdgeList) {
			this.conjEdgeList = conjEdgeList;
		}
	}