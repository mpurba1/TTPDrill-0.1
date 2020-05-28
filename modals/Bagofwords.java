/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.divineit.apps.security.modals;

import java.util.ArrayList;

/**
 *
 * @author ghusari
 */
public class Bagofwords {
   public ArrayList<String> synonyms;
   public int popularity; // no of docs that mention any of synonyms
   
    
    
    public Bagofwords()
    {
        Set();
    }
    
    public boolean Contains(String s)
    {
        if(synonyms.isEmpty()){return false;}
        
        for(String x:synonyms)
        {
            if(x.equals(s))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean Contains(ArrayList<String> list)
    {
        if(synonyms.isEmpty()){return false;}
        
        for(String x:synonyms)
        {
            for(String y:list)
            {
            if(x.equals(y))
            {
                return true;
            }
            }
        }
        return false;
    }
    
    
    
    private void Set()
    {
        synonyms=new ArrayList<>();
        popularity=0;
    }
    
}
