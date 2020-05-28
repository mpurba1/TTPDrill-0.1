/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.divineit.apps.security.preProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mahmed27
 */
public class TextPreprocessor {
    
    public String preProcessText(File file) throws FileNotFoundException {
        String text = new Scanner(file).useDelimiter("\\Z").next();
        return replacePathWithRegex(text);
    }
    
    private String addDotAtEndOfLine(String text) {
        Pattern p = null;
        String out = "";
        
        p = Pattern.compile(Regex.endOfLine);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String tmp = m.group().trim();
            if(tmp.isEmpty()) {
                continue;
            }
            tmp = tmp.replaceAll(":", "");
            out +=  tmp+ ".\n";
        }
        return out;
    }
    
    public String replacePathWithRegex(String text) {
        Pattern p = null;
        String out = null;
        
        p = Pattern.compile(Regex.runKey);
        out = p.matcher(text).replaceAll(Regex.runKeyRText);
        
        p = Pattern.compile(Regex.service);
        out = p.matcher(out).replaceAll(Regex.serviceRText);
        
        p = Pattern.compile(Regex.registryKeyPath);
        out = p.matcher(out).replaceAll("registry entries");
        
        p = Pattern.compile(Regex.dllFile);
        out = p.matcher(out).replaceAll(Regex.dllFileRText);
        
        p = Pattern.compile(Regex.executableFile);
        out = p.matcher(out).replaceAll(Regex.executableFileRText);
        
        p = Pattern.compile(Regex.filePath);
        out = p.matcher(out).replaceAll("files");
        
        p = Pattern.compile(Regex.registryKeyPath);
        out = p.matcher(out).replaceAll("registry entries");
        
        p = Pattern.compile(Regex.url);
        out = p.matcher(out).replaceAll("URL");
        
        p = Pattern.compile(Regex.urlWithwww);
        out = p.matcher(out).replaceAll("URL");
        
        p = Pattern.compile(Regex.urlWithoutwww);
        out = p.matcher(out).replaceAll("website");
        
        p = Pattern.compile(Regex.IP);
        out = p.matcher(out).replaceAll("IP");
        
//        p = Pattern.compile(this.endOfLine);
//        out = p.matcher(out).replaceAll(".");
        //out = this.addDotAtEndOfLine(out);
        
        
        return out;
    }
    
}
