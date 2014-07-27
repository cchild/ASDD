/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_RuleLearner;

import Logging.LogFiles;
import V_Sensors.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class RuleSetList {
    
    
    public ArrayList <RuleSet> rulesetlist;
    private RuleList rulelist;
    private SensorList sList;
    
    
    public RuleSetList (RuleList rulelist, SensorList sList) {
        
        this.rulesetlist = new ArrayList <> ();
        this.rulelist = rulelist;
        this.sList = sList;
    }
    
    
    public void add (RuleSet ruleset) {

        this.rulesetlist.add(ruleset);
    }
    
    
    public void add () {
        //TODO
        RuleSet ruleset = new RuleSet (this.rulelist, this.sList);
        this.rulesetlist.add(ruleset);
    }
    
    public void add (Rule rule) {
        //TODO
        RuleSet ruleset = new RuleSet (this.rulelist, this.sList);
        rule.ruleset_id = ruleset.id;
        ruleset.add(rule);
        this.rulesetlist.add(ruleset);
    }
    
    public RuleSet getRuleSet (int number) {
        
        return this.rulesetlist.get(number -1);
    }
    
    public void addRuleToRuleSet (Rule rule, int number) {
        //TODO
        if (number < this.rulesetlist.size()) {
        RuleSet ruleset = this.rulesetlist.get(number);
        rule.ruleset_id = ruleset.id;
        ruleset.add(rule);
        }
        
    }
    


    
    
    public void print () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 0; i < this.size(); i++) {
            
            System.out.println("RULESET ID : " + this.rulesetlist.get(i).id + " " + this.rulesetlist.get(i) + " totalprob : " + this.rulesetlist.get(i).totalProb);
        }
    }
    
    public void printsoft () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 0; i < this.size(); i++) {
            
            System.out.println("RULESET ID : " + this.rulesetlist.get(i).id + " " + this.rulesetlist.get(i).references + " totalprob : " + this.rulesetlist.get(i).totalProb);
            //this.rulesetlist.get(i).printRules();
        }
    }
    
    
    public void printall () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 0; i < this.size(); i++) {
            
            //System.out.println("RULESET ID : " + this.rulesetlist.get(i).id + " " + this.rulesetlist.get(i) + " totalprob : " + this.rulesetlist.get(i).totalProb);
            this.rulesetlist.get(i).printRules();
        }
    }
    
    
    public int size () {
        
        return this.rulesetlist.size();
    }
    
    
    public void buildFromClosedList () {
        
        
        Rule current = this.rulelist.getRule(0);
        this.add(current);
        //System.out.println("FIRST RULE : " + current.toString());
        //System.out.println("FIRST RULE RuleSet: " + current.ruleset_id);
        
  
        int i;
        for (int j = 0; j < this.rulelist.size(); j ++) {
            i = j+1;
            if (this.rulelist.getRule(j).ruleset_id == -1) {
                
                current = this.rulelist.getRule(j);
                this.add(current);
            }
            
        
            while (i < this.rulelist.size()-2) {

                i++;
                //System.out.println("Looking at " + i);
                if (this.rulelist.getRule(i).isSameRuleSet(current) && this.rulelist.getRule(i).ruleset_id == -1) {
                    //System.out.println("FOUND ONE at index " + i);
                    this.rulelist.getRule(i).ruleset_id = current.ruleset_id;
                    this.getRuleSet(current.ruleset_id).add(this.rulelist.getRule(i));
                }
            }
        }
        
        
        // CALCULATING PROBABILITIES
        for (int j = 1; j <= this.size(); j++) {
            
            for (int h = 0; h < this.getRuleSet(j).references.size(); h++) {
                if (this.getRuleSet(j).totalProb == -1.0) {
                    this.getRuleSet(j).totalProb = 0.0;
                }
                this.getRuleSet(j).totalProb = this.getRuleSet(j).totalProb + this.rulelist.getRuleByID(this.getRuleSet(j).references.get(h)).getProb();
            }
            
            this.getRuleSet(j).totalProb = Math.round(this.getRuleSet(j).totalProb * 1000);
            this.getRuleSet(j).totalProb = this.getRuleSet(j).totalProb/1000;

        }
        
    }
    
    
    
    public int consolidate (SensorList sList, RuleList closedList, RuleMap rMap, boolean silent) {
        
        int counter = 0;
        int steps;
        for (int i = 0; i < this.size(); i++) {
            if ((i%100)==0) {
                if (!silent)
                    System.out.println(i + " / " + this.size());
            }
            steps = this.getRuleSet(i+1).consolidate(closedList, rMap);
            counter = counter + steps;
        }
        
        return counter;
    }
    

    
    
    

    
    
    /* 
        GETS THE CONFLICS BETWEEN ALL RULESETS FROM THE RULESETLIST
        FIRST INDEX OF THE LIST IS THE NUMBER OF CONFLICTS DETECTED
    
        EVERY CONFLICT BETWEEN RULESETS X AND Y IS LOCATED BOTH IN X AND Y INDEXES 
        OF THE LIST
    */
    
    public ArrayList getConflicts (SensorList sList, SensorMap sMap, RuleMap rMap, boolean silent) {
        
        ArrayList <ArrayList> res = new ArrayList();

        int number = 0;
        int count = 0;
        
        for (int i = 1; i <= this.size()-1; i++) {
            
//            if ((count%1000) == 0) 
//                System.out.println("CONFLICTS " + count);
            if (!silent)    
                System.out.println(i + " / " + this.size());
            ArrayList <Integer> a = new ArrayList ();
            //this.getRuleSet(i).
            
            for (int j = i+1; j <= this.size(); j++) {
                      
                if ((this.getRuleSet(i).isConflicting(this.getRuleSet(j))) && (i != j)) {
                    a.add(this.getRuleSet(j).id);
                    number++;
                    //ArrayList <Integer> b = this.getRuleSet(i).getIntersection(this.getRuleSet(j));
                    RuleSet RS12 = new RuleSet(this.getRuleSet(i),this.getRuleSet(j), sList, sMap, rMap);
                    
                    if ((RS12.totalProb > 0.0)) {
                        if(RS12.precedingOver(this.getRuleSet(i), this.getRuleSet(j))) {
                            this.getRuleSet(i).precedences.add(j); count++;
                        }
                        else {
                            this.getRuleSet(j).precedences.add(i); count++;
                        }
                    }
                    

                } 
            }
            
            res.add(i-1, a);
        }

        ArrayList <Integer> b = new ArrayList ();
        b.add(number);
        b.add(count);
        res.add(0, b);
        return res;
    }
    
    
    public void initIndexes (SensorList sList, SensorMap sMap) {
        
        for (int i = 0; i < this.size(); i++) {
            this.getRuleSet(i+1).initIndexes(sMap);
        }
    }
    
    
//    public void initPrecedences () {
//        
//        for (int i = 0; i < this.size(); i++) {
//            
//            for (int j = 0; j < this.size(); j++) {
//                
//                if (this.get)
//            }
//        }
//    }
    
    
    public void buildFromFile (RuleList rList, SensorList sList) {
        
        int max_index = 1;
        
        for (int j = 0; j < rList.size(); j++) {
            
            if (rList.getRule(j).ruleset_id > max_index)
                max_index = rList.getRule(j).ruleset_id;
        }
        
        for (int i = 0; i < max_index; i++) {
            
            RuleSet r = new RuleSet(rList, sList);
            
            this.add(r);
        }
        
        for (int j = 0; j < rList.size(); j++) {
            
            this.getRuleSet(rList.getRule(j).ruleset_id).add(rList.getRule(j));
        }
        
        
                // UPDATING RULESET PROBABILITY
        if (this.size() > 0) {
            for (int i = 0; i < this.size(); i++) {

                this.getRuleSet(i+1).totalProb = 0.0;
                for (int j = 0; j < this.getRuleSet(i+1).size(); j++) {
                    this.getRuleSet(i+1).totalProb = this.getRuleSet(i+1).totalProb + this.getRuleSet(i+1).getRule(j).getProb();
                }
                
                this.getRuleSet(i+1).totalProb = Math.round(this.getRuleSet(i+1).totalProb * 1000);
                this.getRuleSet(i+1).totalProb = this.getRuleSet(i+1).totalProb/1000;
            }
        }
        
    }
    
    
    
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.print(this.getRuleSet(i+1).id + " " , 3);
            
            for (int j = 0; j < this.getRuleSet(i+1).references.size(); j++) {
                logFiles.print(this.getRuleSet(i+1).references.get(j).toString() + " ", 3);
            }
            
            logFiles.println(" ", 3);
            
            for (int h = 0; h < this.getRuleSet(i+1).precedences.size(); h++) {
                logFiles.print(this.getRuleSet(i+1).precedences.get(h).toString() + " ", 3);
            }
            
            logFiles.println(" ", 3);
        }
    }
    
    
    
    
    
 public int fromFile () {
        
        String filePath = Logging.LogFiles.FILE_NAME_3;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                String [] a = line.split(" ");
                
                String line2 = scanner.nextLine();
                
                String [] b = line2.split(" ");

                
                RuleSet RS = new RuleSet(this.rulelist,this.sList);
                
                RS.id = Integer.parseInt(a[0]);
                RS.totalProb = 0.0;
                for (int h = 1; h < a.length; h++) {
                    //System.out.println("Looking for id " + Integer.parseInt(a[h]));
                    RS.add(this.rulelist.findRuleById(Integer.parseInt(a[h])));
                    RS.totalProb += this.rulelist.findRuleById(Integer.parseInt(a[h])).getProb();
                    
                }
                
                if (b.length > 0) {
                    
                    for (int t = 0; t < b.length; t++) {
                        
                        RS.precedences.add(Integer.parseInt(b[t]));
                    }
                }
                
                this.add(RS);
                
                
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }  
 
 
 

}
