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
    
    // RuleMaps are defined like this : 
    //
    // Rule 1245 [W, E, E, E, A, W][W, E, E, E, A, E] Occurrencies : 4 tab : [4586, 42138, 83673, 90124]
    //
    // Rule + Occurrencies + Indexes of Occurrencies
    public ArrayList <ArrayList> map;
    private final TokenMap tokenMap;
    
    
    
    
    
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
    
    
    // Adds a Rule with its indexes at insert_index
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
    
    
    // Replace existing Rule at index bu "rule"
    public void setRule (Rule rule, int index) {
        
        
        this.addRule(rule, this.getIndexes(index), index+1);
        
        this.remove(index);
    }
    
    
    // REPLACE EXISTING INDEXLIST AT (INDEX) BY A
    public void setIndexes (ArrayList a, int index) {
        
        
        this.addRule(this.getRule(index), a, index+1);
        
        this.remove(index);
    }    
    
    
    
    public void addNewIndex (int i, int newindex) {
        
        ArrayList a = (ArrayList) this.map.get(i).get(1);
        
        a.add(newindex);
        
        this.setIndexes(a, i);
    } 
    
    
    // Returns the first exact matching Rule
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
    
    
    
    // Builds the RuleMap from INPUT_FILE
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

                Rule r = new Rule(previous,post);
               
                int tip = this.findRule(r);
                
                //System.out.println("FIND : " + tip);
                if (tip > -1) {
                    
                    this.addNewIndex(tip, i);
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
    
    
    
    // Retruns how many times Rule is matched in the RuleMap
    public int getMatchingOccurencies (Rule rule) {
        
        int a = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch(rule))
                a = a + this.getOccurrencies(i);
        }
        
        
        
        return a;
    }
    
    
    // Returns the indexes of matching Rules
    public ArrayList <Integer> getMatchingIndexes (Rule rule) {
        
        ArrayList <Integer> a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getRule(i).ruleMatch(rule))
                a.addAll(this.getIndexes(i));
        }
        
        
        
        return a;
    }    
    
    
}
