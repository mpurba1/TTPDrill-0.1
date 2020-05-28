package net.divineit.apps.security.ontologyParser;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.divineit.apps.security.preProcess.TextPreprocessor;

public class GUI {
	
	private static JFrame frame;
	private static JTextArea jTextArea;
	private static JTextField jTextFieldThreshold, jTextFieldTop;
	private static JComboBox<String> cb;
	private static JComboBox<String> stemCb;
	public static boolean isStemming;
	
	
	/**
	 * Variables for Controlling output
	 */
	static int xout;
	static double threshold;
	
	
	public static void createFrame() {
		frame = new JFrame("G.Corp Ontology Parser");

		// create our jbutton
	    JButton runButton = new JButton("Run");
	    
	    // add the listener to the jbutton to handle the "pressed" event
	    runButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        // display/center the jdialog when the button is pressed
	    	  String sentence = jTextArea.getText();
	    	  TextPreprocessor tpre = new TextPreprocessor();
	    	  sentence = tpre.replacePathWithRegex(sentence);
	    	  threshold = Double.parseDouble(jTextFieldThreshold.getText());
	    	  xout = Integer.parseInt(jTextFieldTop.getText());
	    	  String selected_option = cb.getSelectedItem().toString();
	    	  String stem = stemCb.getSelectedItem().toString();
	    	  if (stem.equals(Constants.yes))
	    		  isStemming = true;
	    	  else isStemming = false;
	    	  stemCb.disable();
	    	  OntologyParser op = new OntologyParser();
	    	  op.loading_method(sentence, selected_option);
	      }
	    });

	    // put the button on the frame
	    frame.getContentPane().setLayout(new FlowLayout());
	    frame.add(runButton);
	    frame.setResizable(false);
	    
	    // put a text area
	    jTextArea = new JTextArea();
	    jTextArea.setColumns(40);
	    jTextArea.setRows(10);
	    jTextArea.setLineWrap(true);
	    frame.add(jTextArea);
	    
	    
	    // put a drop down -- new code 22 Oct
	    String[] choices = { Constants.nlpParser_key, Constants.wordSpacing_key, Constants.manualNlp_key,
	    		Constants.edgeTree_key};
	    cb = new JComboBox<String>(choices);
	    cb.setVisible(true);
	    frame.add(cb);
	    
	    
	    // Add stemming label
	    // Stemming yes no choice to user
	    JLabel label_stem = new JLabel("Stem words?");
	    frame.add(label_stem);
	    
	    // put a drop down for stemming -- new code 23 Oct
	    String[] stemChoices = { Constants.yes, Constants.no};
	    stemCb = new JComboBox<String>(stemChoices);
	    stemCb.setVisible(true);
	    frame.add(stemCb);

	    // put a scroll
	    JScrollPane scroll = new JScrollPane (jTextArea);
	    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    //scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	    frame.add(scroll);
	    
	    
	    // put a text field for BM25 threshold value
	    JLabel label_threshold = new JLabel("Threshold");
	    frame.add(label_threshold);
	    jTextFieldThreshold = new JTextField();
	    jTextFieldThreshold.setText("1.7");
	    jTextFieldThreshold.setColumns(10);
	    frame.add(jTextFieldThreshold);
	    
	    // put a field to limit top 10 results
	    JLabel label_topResult = new JLabel("Top Results");
	    frame.add(label_topResult);
	    jTextFieldTop = new JTextField();
	    jTextFieldTop.setText("10");
	    jTextFieldTop.setColumns(10);
	    frame.add(jTextFieldTop);
	    
	    // set up the jframe, then display it
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(500, 300));
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	  }
	
	
}
