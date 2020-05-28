package net.divineit.apps.security.ontologyParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import net.divineit.apps.security.modals.Document_Terms;
import net.divineit.apps.security.stnfd.basic.Sentence;

public class Utilities {

	/**
	 * This remove stop words from the text
	 * removeStopWords
	 */
	public static final String[] regExStrings = new String[] {
			"HKEY.*\\\\[Rr]un\\\\.*\\. ",
			"HKEY.*\\\\[Ss]ervices?\\\\.*\\.",
			"Rundll\\.[Ee][Xx][Ee]",
			"CVE-\\d*-\\d*"
	};

	public static HashMap<String, String> regExMap; //key = Technique No, value = regular expression.

	public static HashMap<String, String> techTactHashMap = null;
	static {
		setTechTactNameMap();
		createRegExMap();
	}

	public static void createRegExMap() {
		regExMap = new HashMap<>();
//		regExMap.put("Registry Run Keys / Start Folder,Persistence","HKEY.*\\\\[Rr]un\\\\.*\\. ");
//		regExMap.put("New Service,Persistence;;Privilege Escalation","HKEY.*\\\\[Ss]ervices?\\\\.*");
//		regExMap.put("Rundll32,Defense Evasion;;Execution","Rundll\\.[Ee][Xx][Ee]");
//		regExMap.put("Standard Application Layer Protocol,Command and Control","CVE-\\d*-\\d*");
		regExMap.put("T1060","HKEY.*\\\\[Rr]un\\\\");
		regExMap.put("T1050","HKEY.*\\\\[Ss]ervices?\\\\");
		regExMap.put("T1085","Rundll\\.[Ee][Xx][Ee]");
		regExMap.put("T1068","CVE-\\d*-\\d*");
	}

	public static String removeStopWords(String textFile) {
		CharArraySet stopWords = getCustomStopWords();
		stopWords.addAll(EnglishAnalyzer.getDefaultStopSet());
		Analyzer analyzer = new StopAnalyzer(stopWords);
		TokenStream tokenStream
				= analyzer.tokenStream("contents",
				new StringReader(textFile));
		analyzer.close();
		tokenStream = new StopFilter(tokenStream, stopWords);
		StringBuilder sb = new StringBuilder();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				String term = charTermAttribute.toString();
				sb.append(term + " ");
			} tokenStream.close(); }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}



	/**
	 * This method stems the word
	 * stem
	 */
	public static String stem(String term, boolean stopFilter) {
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream result = analyzer.tokenStream(null, term);
		result = new PorterStemFilter(result);
		analyzer.close();
		StringBuilder stemmedResult = null;
		if(stopFilter)
			result = new StopFilter(result, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		CharTermAttribute resultAttr = result.addAttribute(CharTermAttribute.class);
		try {
			result.reset();
			stemmedResult = new StringBuilder();
			while (result.incrementToken()) {
				stemmedResult.append(resultAttr.toString() + Constants.space);
			}
			result.close();
			return stemmedResult.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * convertSentencetoDocument(String sentence)
	 * @param sentence
	 * @return
	 */
	public static Document_Terms convertSentencetoDocument(String sentence) {
		//System.out.println("No NLP - sentence to doc");
		Document_Terms tempActionDoc_q = new Document_Terms();
		String [] words = sentence.split(" ");
		for(String word: words) {
			if (!word.equalsIgnoreCase(""))
				tempActionDoc_q.Add_Term_(word);
		}
		//System.out.println(tempActionDoc_q.term_freq_hmap.size());
		return tempActionDoc_q;
	}




	/**
	 * Temporary
	 * to return a dummy doc
	 * @param
	 */
	// This method is not used for now
	public static Document_Terms getDummyDoc() {
		Document_Terms tempActionDoc_q = new Document_Terms();
		tempActionDoc_q.Add_Term_Frequency("controlled", 1);
		tempActionDoc_q.Add_Term_Frequency("HAMMERTOSS", 1);
		tempActionDoc_q.Add_Term_Frequency("commands", 1);
		tempActionDoc_q.Add_Term_Frequency("appended", 1);
		tempActionDoc_q.Add_Term_Frequency("image", 1);
		tempActionDoc_q.Add_Term_Frequency("files", 1);
		return tempActionDoc_q;
	}

	/**
	 * Convert Text into separate sentences and then parse with nlp
	 * text2Sen ()
	 */

	public static List<Sentence> breakText (String text) {
		List<Sentence> sentenceList = new ArrayList<Sentence>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next();
			 end != BreakIterator.DONE;
			 start = end, end = iterator.next()) {
			Sentence sentence = new Sentence();
			sentence.setOriginalSentence(text.substring(start,end).trim());
			sentence.setStartPos(start);
			sentence.setEndPos(end);
			sentenceList.add(sentence);
		}
		return sentenceList;
	}

	/**
	 * convertDocument2DocumentTerms
	 * @param text
	 * @return
	 */
	public static ArrayList<Document_Terms> convertDocument2DocumentTerms(String text){
		ArrayList<Document_Terms> dtermsList = new ArrayList<Document_Terms>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next();
			 end != BreakIterator.DONE;
			 start = end, end = iterator.next()) {
			TTPExtractor ex = new TTPExtractor(text.substring(start,end));
			// extract 3 files, goal1, goal2 and verb_action
			Document_Terms TA = null;         Document_Terms G1 = null;         Document_Terms G2 = null;
			for(String word: ex.getVoDoc()) {
				if (TA==null)
					TA = new Document_Terms();
				if (!word.equalsIgnoreCase(""))
					TA.Add_Term_(word);
			}
			dtermsList.add(TA);
			for(String word: ex.getG1Doc()) {
				if (G1==null)
					G1 = new Document_Terms();
				if (!word.equalsIgnoreCase(""))
					G1.Add_Term_(word);
			}
			dtermsList.add(G1);
			for(String word: ex.getG2Doc()) {
				if (G2==null)
					G2 = new Document_Terms();
				if (!word.equalsIgnoreCase(""))
					G2.Add_Term_(word);
			}
			dtermsList.add(G2);
		}
		return dtermsList;
	}

	/**
	 *
	 */
	public static String convertTechIDtoTechName(String techID) {
		String result = null;
		if (!techID.startsWith("T"))
			return result;
		String csvFile = Utilities.class.getResource("/app/ttpdrill/tech-tact.csv").getPath();
//       String csvFile = "C:\\Ankit\\Data\\input\\tech-tact.csv";
		BufferedReader br; String line; String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] fields = line.split(cvsSplitBy);
				if (fields[2].toString().trim().equals(techID.trim()))
					result = fields[0] + "," + fields[1].toString();

			}
			br.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return result;

	}

	public static void setTechTactNameMap() {
		if (techTactHashMap == null) {
			techTactHashMap = new HashMap<>();
			String csvFile = Utilities.class.getResource("/app/ttpdrill/tech-tact-hashmap.csv").getPath();
			BufferedReader br;
			String cvsSplitBy = ",";
			String line;
			try {
				br = new BufferedReader(new FileReader(csvFile));
				while ((line = br.readLine()) != null) {

					System.out.println(line);
					String[] fields = line.split(cvsSplitBy);

					if (fields.length >= 3) {
						techTactHashMap.put(fields[0] + "," + fields[1], fields[2]);
					}
				}
				br.close();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * Additional stop words
	 */

	private static CharArraySet getCustomStopWords() {
		List<String> stopwords = new ArrayList<String>();
		String[] sw = new String[] {"campaign", "technique", "used malware", "mechanism", "make", "make use","it","they",
				"malware", "client", "family", "victim", "variant", "function", "scheme"};
		for (String x : sw) {
			stopwords.add(x);
		}
		CharArraySet charArraySet = new CharArraySet(stopwords, true);
		return charArraySet;
	}



	/**
	 * scanning
	 */
	public static Map<String, String> scanning(String text) {
		//omiting last '.'
		if (text.charAt(text.length()-1) == '.') {
			text = text.substring(0, text.length() - 1);
		}
		text = " " + text + " ";
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> dictionary = Utilities.Dictionary();
		for (String x:dictionary.keySet()) {
			boolean flag = Pattern.compile(Pattern.quote(x), Pattern.CASE_INSENSITIVE).matcher(text).find();
			if (flag) {
				map.put(x, dictionary.get(x));
			}
		}
		// search for every tech-tact name
		if (techTactHashMap == null) {
			System.out.println("Tech Tact Map is null.");
			setTechTactNameMap();
		}
		for (String techTactName : techTactHashMap.keySet()) {
			String[] techName = techTactName.split(",");
			if (techName.length >=2) {
				boolean flag = Pattern.compile(Pattern.quote(techName[0]), Pattern.CASE_INSENSITIVE).matcher(text).find();
				if (flag) {
					map.put(techName[0], techTactHashMap.get(techTactName));
				}
			}

		}
		//Regex calculator
		if (regExMap == null) {
			System.out.println("RegEx Map is null.");
			createRegExMap();
		}

		for (String key : regExMap.keySet()) {
			String[] techName = key.split(",");
			Matcher matcher = Pattern.compile(regExMap.get(key), Pattern.CASE_INSENSITIVE).matcher(text);
			while (matcher.find()) {
				System.out.println("regex" + matcher.group());
//				if (matcher.group().startsWith("CVE")) {
//					map.put(matcher.group(), matcher.group());
//				} else {
					map.put(matcher.group(), techName[0]);
//				}
			}
		}


		return map;
	}


		/**
		 * Creating a dictionary of fixed tech types
		 */
		private static Map<String, String> Dictionary() {
			Map<String, String> map = new HashMap<String, String>();
			map.put(" DLL injection ", "T1055");
			map.put(" Data Exfiltration ", "TA0010");
			map.put(" spear phishing ","T1193");
			map.put(" spear-phishing ","T1193");
			map.put(" spearphishing ","T1193");
			map.put(" phishing ","T1193");
			map.put(" DDE ","T1173");
			map.put(" keylogging ","T1056");
			map.put(" keystrokes ","T1056");
			map.put(" keylog ","T1056");
			map.put(" (C2) ","T1071");
			map.put(" Base64-encoded ","T1027");
			map.put(" PostScript ","T1064");
			map.put(" script ","T1064");
			map.put(" VBScript ","T1064");
			map.put(" Tor ","T1188");
			map.put("shellcode","T1064");



			return map;
		}
}
