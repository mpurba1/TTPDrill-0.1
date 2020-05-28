/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.divineit.apps.security.preProcess;

import java.io.FileNotFoundException;

/**
 *
 * @author mahmed27
 */
public class ReportPreprocessorNLP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
//        NLPCoreExtractor extractor = NLPCoreExtractor.getInstance();
        //("The Trojan may arrive on the compromised computer after being downloaded by website");//
//        extractor.extractAction("The Trojan may open a back door on the compromised computer and connect to the following location using hardcoded keys.");
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        extractor.extractActionTreeBasedApproach("The Trojan may arrive on the compromised computer after being downloaded by website."); //When the Trojan is executed, it creates the following file
//        extractor.preProcessText("dssf [https://]www.amazon.com/Men-War-PC/dp/B001QZGVEC/EsoftTeam/watchc[REMOVED]");
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Preprocessor().setVisible(true);
            }
        });
    }
    
}
