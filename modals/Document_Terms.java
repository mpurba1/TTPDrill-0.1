/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.divineit.apps.security.modals;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ghusari
 */
public class Document_Terms {
	public String Name, ID;
	public int totalterms; // sum of terms ; i.e., frequenicies the total of all terms (non-unique) in this
							// documents
	public int totalDocs; // added by Ankit to maintain total number of documents in the corpus
	public HashMap<String, Integer> term_freq_hmap;
																																																																																																																																																																																
	////////////////////////
	public Document_Terms() {
		totalterms = 0;
		term_freq_hmap = new HashMap<String, Integer>();
		Name = "";
		ID = "";
	}

	public Document_Terms(String Id) {
		totalterms = 0;
		term_freq_hmap = new HashMap<String, Integer>();
		this.Name = "";
		ID = Id;
	}

	public Document_Terms(String Name, String Id) {
		totalterms = 0;
		term_freq_hmap = new HashMap<String, Integer>();
		this.Name = Name;
		ID = Id;
	}

	//
	public void Add_Term_(String word) {
		// looks if it is in the hashmap if yes, then increase freq in hashmap by 1, and
		if(term_freq_hmap.containsKey(word))
		{
			term_freq_hmap.put(word, new Integer(term_freq_hmap.get(word))+1);
		}
		else
		{
			term_freq_hmap.put(word, new Integer(1));
		}
		totalterms++;
		// total terms by 1
		// if not add new entry with freq of 1 in the hashmap and totalterms++
	}

	public void Add_Term_Frequency(String term, int freq) // this function increases the number of times this term has
															// been mentioned in this document (tf(t))
	{
		// System.out.println("adding the term "+term+" with freq "+freq+" to doc "+ID+"
		// --Document-Terms add term freq");
		term_freq_hmap.put(term, new Integer(freq));
		totalterms += freq;

	}

	public double Get_Normalized_Term_Frequency_inDoc(String term) // normalized means the term frequency is divided by
																	// the summation of all terms frequencies
	{
		Integer temp = term_freq_hmap.get(term);
		if (temp == null) {
			return 0;
		} else {
			return (double) (temp) / (double) (totalterms);
		}

	}

	public double Get__Term_Frequency_inDoc(String term) // normalized means the term frequency is divided by the
															// summation of all terms frequencies
	{
		Integer temp = term_freq_hmap.get(term);
		if (temp == null) {
			return 0;
		} else {
			return (double) (temp);
		}

	}

	public double Get_Document_length() { // returns the number of all terms in the document (non-unique)

		return totalterms;
	}

	public boolean Contains_Term(String s) {
		if (term_freq_hmap.get(s) == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean Contains_BagTerm(ArrayList<String> list) {
		if (list == null || list.isEmpty() || term_freq_hmap == null || term_freq_hmap.isEmpty()) {
			return false;
		}

		for (String s : list) {
			if (term_freq_hmap.get(s) == null) {

			} else {
				return true;
			}
		}
		return false;

	}

	public double Get__Term_Frequency_inDoc_bagofwords(ArrayList<String> synons) {
		Integer temp;
		Integer tf_synonyms = 0;
		for (String term : synons) {
			temp = term_freq_hmap.get(term);
			if (temp == null) {
				// keep going, we are looking for any synonym
			} else {
				tf_synonyms += temp;
				// System.out.println("found a synoyns so increasing tf in doc -- doc-terms
				// class");
			}
		}
		return (double) (tf_synonyms);
	}
}
