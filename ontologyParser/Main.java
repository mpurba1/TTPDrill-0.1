package net.divineit.apps.security.ontologyParser;

import net.divineit.apps.security.models.SecurityThreatCategory;
import net.divineit.apps.security.ontologyParser.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		// Option 1: Read from directory
		boolean  folderload = false; 
		
		if(folderload){
			String target_dirFilePath = Main.class.getResource("/app/ttpdrill/" + Constants.target_dir).getPath();
			 String target_dir = target_dirFilePath;
			 OfflineMode.runOnDirectory(target_dir);
		}
		
		// Option 2: Read from GUI
		else GUI.createFrame(); // crate Java GUI frame
	}
	
	
	// Option 3: Returns a hash map
	public static List<Map<String,String>> runOntologyParser(String text, List<SecurityThreatCategory> ontologyList, HashMap configMap) {
		if (!text.trim().equals("")) {
//			OntologyParser op = new OntologyParser(ontologyList, configMap);
			OntologyParser op = OntologyParser.getInstance(ontologyList,configMap);
			op.use_manual_nlp_rules_extract(text, false, true);
			List<Map<String,String>> result = op.getResultMap();
			op.setResultMap(null);
        	return result;
		} else return null;
	}
	
	

}
