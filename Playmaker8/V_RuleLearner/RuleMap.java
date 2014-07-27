/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_RuleLearner;


import V_Sensors.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class RuleMap {
    
    
    public ArrayList <ArrayList> map;
    private TokenMap tokenMap;
    
    
    
    
    
    public RuleMap (TokenMap t) {
        
        this.map = new ArrayList ();
        this.tokenMap = t;
    }
    
 
    
    public Rule getRule (int i) {
        
        return (Rule) this.map.get(i).get(0);
    }
    
    
    public ArrayList <Integer> getIndexes (int i) {
        
        return (ArrayList <Integer>) this.map.get(i).get(1);
    } 
    
    
    public int getOccurrencies (int i) {
        
        ArrayList a = (ArrayList) this.map.get(i).get(1);
        return a.size();
    } 
    
    
    public void remove (int i) {
        
        this.map.remove(i);
    }
    
    
    public void addRule (Rule rule, ArrayList indexes, int insert_index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(rule);
        
        a.add(indexes);
        
        
        this.map.add(insert_index, a);
    }
 
    
    public void addRule (Rule rule, ArrayList indexes) {
        
        ArrayList a = new ArrayList ();
        
        a.add(rule);
        
        a.add(indexes);
        
        
        this.map.add(a);
    }
    
    public void addRule (Rule rule, int firstIndex) {
        
        ArrayList a = new ArrayList ();
        ArrayList b = new ArrayList ();
        
        b.add(firstIndex);
        a.add(rule);
        
        a.add(b);
        
        
        this.map.add(a);
    }    
    
    
    
    public void setRule (Rule rule, int index) {
        
        
        this.addRule(rule, this.getIndexes(index), index+1);
        
        this.remove(index);
    }
    
    // REPLACE EXISTING INDEXLIST AT (INDEX) BY A
    public void setIndexes (ArrayList a, int index) {
        
        
        this.addRule(this.getRule(index), a, index+1);
        
        this.remove(index);
    }    
    
    
    public void increaseIndexes (int i, int newindex) {
        
        ArrayList a = (ArrayList) this.map.get(i).get(1);
        
        a.add(newindex);
        
        this.setIndexes(a, i);
    } 
    
    
    public int findRule (Rule rule) {
        
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch(rule))
                return i;
        }
        
        return -1;
    }
    
     
    public int size () {
        
        return this.map.size();
    }
    
    
    
    
    public int fromFile (SensorList sList) {
        

            String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            String line = scanner.nextLine();
            Sensor previous = new Sensor(line,tokenMap);
            
            while (scanner.hasNextLine()) {
                i++;
                String line2 = scanner.nextLine();
                

                Sensor post = new Sensor(line2,tokenMap);

                Rule r = new Rule(previous,post,sList);
               
                int tip = this.findRule(r);
                
                //System.out.println("FIND : " + tip);
                if (tip > -1) {
                    
                    this.increaseIndexes(tip, i);
                }
                
                else {
                    this.addRule(r, i);
                }
                
                
                previous = post;
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }
    
    
    
    public void printList (String str) {
        
        System.err.println("\nPRINTING " + str + " RULEMAP (" + this.size() + " entries).");
        for (int i =0; i < this.size(); i++) {
            
            System.out.println("Rule " + i + " " + this.getRule(i) + " Occurrencies : " + this.getOccurrencies(i) + " tab : " + this.getIndexes(i) );
        }
    }
    
    
    
    
    public int getMatchingOccurencies (Rule rule) {
        
        int a = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch(rule))
                a = a + this.getOccurrencies(i);
        }
        
        
        
        return a;
    }
    
    
    public ArrayList <Integer> getMatchingIndexes (Rule rule) {
        
        ArrayList <Integer> a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch(rule))
                a.addAll(this.getIndexes(i));
        }
        
        
        
        return a;
    }    
    
    
    public int getExactMatchingOccurencies (Rule rule) {
        
        int a = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch_exact(rule))
                return this.getOccurrencies(i);
        }
        
        
        
        return a;
    }
    
    public ArrayList getExactMatchingIndexes (Rule rule) {
        
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch_exact(rule))
                return this.getIndexes(i);
        }
        
        
        
        return a;
    }   
    
    
//    public ArrayList <Integer> indexesOfRule (Rule rule) {
//        
//        ArrayList res = new ArrayList ();
//        ArrayList <Integer> a = this.getMatchingIndexes(rule.getPrecondition());
//        ArrayList b = this.getMatchingIndexes(rule.getPostcondition());
//        
//        
//        for (int i = 0; i < a.size(); i++) {
//            
//            if (b.contains(a.get(i)+1))
//                res.add(i);
//        }
//        
//        return res;
//    }
    
    
    
    
}
