
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
    
    // The RuleSetList contains all RuleSets
    // It could be called our Rule Database
    public ArrayList <RuleSet> rulesetlist;
    private final RuleList rulelist;
    private final SensorList sensorList;
    
    
    public RuleSetList (RuleList rulelist, SensorList sList) {
        
        this.rulesetlist = new ArrayList <> ();
        this.rulelist = rulelist;
        this.sensorList = sList;
    }
    
    
    public void add (RuleSet ruleset) {

        this.rulesetlist.add(ruleset);
    }
    
    // Adds a new empty RuleSet to the RuleSetList
    public void add () {

        RuleSet ruleset = new RuleSet (this.rulelist, this.sensorList);
        this.rulesetlist.add(ruleset);
    }
    
    // Adds a Rule to a RuleSet
    // Then adds this RuleSet to the RuleSetList
    public void add (Rule rule) {
   
        RuleSet ruleset = new RuleSet (this.rulelist, this.sensorList);
        rule.ruleset_id = ruleset.id;
        ruleset.add(rule);
        this.rulesetlist.add(ruleset);
    }
    
    
    public RuleSet getRuleSet (int number) {
        
        return this.rulesetlist.get(number -1);
    }
    
    
    public void addRuleToRuleSet (Rule rule, int number) {
        
        if (number < this.rulesetlist.size()) {
        RuleSet ruleset = this.rulesetlist.get(number);
        rule.ruleset_id = ruleset.id;
        ruleset.add(rule);
        }
        
    }
    


    
  
    
    
    // Soft Printing
    public void printsoft () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 0; i < this.size(); i++) {
            
            System.out.println("RULESET ID : " + this.rulesetlist.get(i).id + " " + this.rulesetlist.get(i).references + " totalprob : " + this.rulesetlist.get(i).totalProb);
        }
    }
    
    
    // Exhaustive printing
    public void printall () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 0; i < this.size(); i++) {
            
            this.rulesetlist.get(i).printRules();
        }
    }
    
    
    
    // Exhaustive printing
    public void printall_RVLR () {
        
        System.out.println("\nPRINTING RULESETLIST");
        for (int i = 1; i < this.size(); i++) {
            
            this.getRuleSet(i).printRules_RVLR();
        }
    }
    
    public int size () {
        
        return this.rulesetlist.size();
    }
    
    
    // Builds the RuleSetList from the ClosedList in MSDD & ASDD Algorithms
    public void buildFromRuleList () {
        
        
        Rule current = this.rulelist.getRule(0);
        
  
        int i;
        
        for (int j = 0; j < this.rulelist.size(); j ++) {
            i = j;
            if (this.rulelist.getRule(j).ruleset_id == -1) {
                
                current = this.rulelist.getRule(j);
                this.add(current);
            }
            
        
            while (i < this.rulelist.size()-1) {

                i++;
                
                if (this.rulelist.getRule(i).isSameRuleSet(current) && this.rulelist.getRule(i).ruleset_id == -1) {
                    
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
    

 
    
    
    
    
    // Consolidate a RuleSet by adding all the Missing Rules
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
    

    
    
    

    
    
    
    //    GETS THE CONFLICS BETWEEN ALL RULESETS FROM THE RULESETLIST
    //    FIRST INDEX OF THE LIST IS THE NUMBER OF CONFLICTS DETECTED
    //
    //    EVERY CONFLICT BETWEEN RULESETS X AND Y IS LOCATED BOTH IN X AND Y INDEXES 
    //    OF THE LIST
    public ArrayList getConflicts (SensorList sList, SensorMap sMap, RuleMap rMap, boolean silent) {
        
        ArrayList <ArrayList> res = new ArrayList();

        int number = 0;
        int count = 0;
        
        for (int i = 1; i <= this.size()-1; i++) {
            

            if (!silent)    
                System.out.println(i + " / " + this.size());
            ArrayList <Integer> a = new ArrayList ();
            
            
            for (int j = i+1; j <= this.size(); j++) {
                      
                if ((this.getRuleSet(i).isConflicting(this.getRuleSet(j))) && (i != j)) {
                    a.add(this.getRuleSet(j).id);
                    number++;
                    
                    RuleSet RS12 = new RuleSet(this.getRuleSet(i),this.getRuleSet(j), sList, sMap, rMap);
                    
                    if ((RS12.totalProb > 0.0)) {
                        if(RS12.precedingOver(this.getRuleSet(i), this.getRuleSet(j))) {
                            this.getRuleSet(i).precedences.add(j); count++;
                        }
                        else {
                            this.getRuleSet(j).precedences.add(i); count++;
                        }
                    }
                    else {
                        
                        if ( this.getRuleSet(i).getPrecursorOccurrencies() > this.getRuleSet(j).getPrecursorOccurrencies()) {
                            
                            this.getRuleSet(i).precedences.add(j); count++; 
                        }
                        else 
                            this.getRuleSet(j).precedences.add(i); count++;
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
    
    
    
    
    
    // SAVES RuleSetList in RSLIST_INPUT_FILE
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
    
    
    
    
    // Loads the RuleSetList from RLIST_INPUT_FILE
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

                
                RuleSet RS = new RuleSet(this.rulelist,this.sensorList);
                
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
 
 
    
    
    // Returns the RVLR score (to determine the best action)
    public double get_RVLR_score (Sensor state) {

        double score = 0.0;
        
        for (int i = 1; i < this.size(); i++) {
            
            
            
            if (this.getRuleSet(i).getRule(0).getPrecondition().sensorMatch(state)) {
                
                score = score + this.getRuleSet(i).getRule(0).get_RVRL();
            }
            
        }

        return score;
    }
 

}
