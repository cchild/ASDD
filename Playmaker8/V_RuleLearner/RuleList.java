package V_RuleLearner;

import Logging.*;

import V_Sensors.*;


import java.io.File;
import java.io.FileNotFoundException;

import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class RuleList {
    
    public ArrayList <Rule> rulelist;
    
    
    public RuleList() {
        
        rulelist = new ArrayList <> ();
        
    }
    
    
    public RuleList copy () {
        
        RuleList res = new RuleList();
        res.rulelist = new ArrayList <> ();
        for (int i=0; i<this.size(); i++) {
            res.rulelist.add(this.getRule(i));
        }
        
        return res;
      
    }
    
    
    public Rule getRule (int i) {
        return this.rulelist.get(i);
    }
    
    public Rule getRuleByID (int id) {
        for (int i = 0; i < this.size(); i++) {
            if (this.rulelist.get(i).id == id)
                return this.rulelist.get(i);
        }
        
        // IF ID DOES NOT EXIST, WHAT SHOULD WE RETURN ?
        return null; 
    }    
    
    
    
    
    public int addRule (Rule rule) {
        
        rulelist.add(rule);
        
        return 0;
    }
    
    
    public int add (RuleList rulelist) {
        int a = 0;
        for (int i=0; i<rulelist.size(); i++) {
            if (!this.containsRule(rulelist.getRule(i))) {
                this.rulelist.add(rulelist.getRule(i));
                a++;
            }
        }
        
        return a;
    }
    
    public int addWithCheck (RuleList rulez, SensorList sList, SensorMap sMap, RuleMap rMap) {
        int a = 0;
        int b ;
        int c ;
        
        for (int i=0; i<rulez.size(); i++) {
            
            //b = sList.indexesOfRule(rulez.getRule(i)).size();
            b = rMap.getMatchingOccurencies(rulez.getRule(i));
            //if (sList.containsRule_fast(rulez.getRule(i)) > 0) {
            if (b>0) {
            //c = rulez.getRule(i).prec_occurrencies;
                //System.out.println(" C  :" + c);
                c = sMap.getMatchingOccurencies(rulez.getRule(i).getPrecondition());
                //c = sList.indexesOfSensor(rulez.getRule(i).getPrecondition()).size();
                if ((!this.containsRule(rulez.getRule(i))) ) {
////                    // EXPLICIT PRINTING OF RULE W**E**S
////                    if ( (rulez.getRule(i).precondition.getToken(0).getReference() == 1) && (rulez.getRule(i).precondition.getToken(5).getReference() == 3) && (rulez.getRule(i).precondition.getToken(3).getReference() == 1) ) {
////                        System.out.println("Adding : " + rulez.getRule(i) + " from ");
////                        //rulez.printList();
////                        //System.out.println();
////                    }
                    Rule rule = new Rule (rulez.getRule(i).precondition,rulez.getRule(i).postcondition, sList);
                    rule.occurrencies = b;
                    rule.prec_occurrencies = c;
                    this.addRule(rule);
                    a++;
                    continue;
                }
            }

                //System.out.println("Rule : " + rulez.getRule(i).precondition + " " + rulez.getRule(i).postcondition + " was pruned. (B " + b + ") >> " + this.containsRule(rulez.getRule(i))) ;



        }
        
        return a;
    }
    
    
    public int remove (int i) {
        this.rulelist.remove(i);
        
        return 0;
    }
    
    
    public int findRuleOcc (Rule rule) {
        
        int occurrences = Collections.frequency(this.rulelist, rule);
        return occurrences;
    }
    
    
     public int findRuleOcc_exact (Rule rule) {
        
        int occurrences = exactfrequency(this.rulelist, rule);
        return occurrences;
    }
    
    public static int exactfrequency(Collection<?> c, Object o) {
        int result = 0;
        if (o == null) {
            for (Object e : c)
                if (e == null)
                    result++;
        } else {
            for (Object e : c)
                if (o.hashCode() == e.hashCode())
                    result++;
        }
        return result;
    }
    
    
    public int findRuleIndex (Rule rule) {
        int i = 0;
        
        while (i < this.size()) {
            
            if (this.getRule(i).ruleMatch_exact(rule)) {
                return i;
            }
            
            i++;
        }
        
        return -1;
    }
    
        
    public int findRuleId (Rule rule) {
        
        int index = findRuleIndex(rule);
        if (index == -1)
            return -1;
        
        return this.getRule(index).id;
        
    }
    
    public int findRuleIdFromSensors (Sensor s1, Sensor s2) {
        
        for (int i =0; i < this.size(); i ++) {
            
            if ((this.getRule(i).precondition.sensorMatch_exact(s1)) && (this.getRule(i).postcondition.sensorMatch_exact(s2)))
                return this.getRule(i).id;
        }
        
        return -1;
        
    }    
    
    public Rule findRuleById (int id) {
        
        for (int i = 0; i<this.size(); i++) {
            
            if (this.getRule(i).id == id) { 
                //System.out.println("RULE " + this.getRule(i).id + " found.");
                return this.getRule(i);
            }
        }
        
        //System.out.println("Rule "+ id + " not found.");
        return null;
    }
    
    
    public int printList () {

        return printList("");
    }
        
    public int printList (String str) {
        
        return printListBy(str,1);
    }
    
    public int printListBy (String str, int number) {
        
        int n = this.rulelist.size();
        System.out.println("\nPRINTING "+ str + " RULELIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            if ((i % number) ==0)
            System.out.println("Rule " + (i+1) + " (ID" + this.rulelist.get(i).id + ") " + this.rulelist.get(i).toString() + " " + this.rulelist.get(i).occurrencies + "x" + " prob : " + this.rulelist.get(i).getProb());
        }
        
        
        System.out.println("RULELIST " + str + " SIZE : " + n);
        return 0;
    }
    
    
    public int printListByWithProb (String str, SensorList sList, int number, LogFiles logfiles) {
        return printListByWithProb (str, sList, number, this.size(),logfiles);
    }
    
    public int printListByWithProb (String str, SensorList sList, int number, int limit, LogFiles logfiles) {

        
        int n = min(this.rulelist.size(),limit);
        System.out.println("\nPRINTING "+ str + " RULELIST (SIZE:" + n + ")");
        
        //LogFiles logfile = getInstance();
        
        for (int i = 0; i < n; i++) {
            if ((i % number) ==0) {
         
                System.out.println((i+1) + " " + this.getRule(i).toString() + " " + this.getRule(i).occurrencies + "x out of " + this.getRule(i).prec_occurrencies + " PROB :" + this.getRule(i).getProb());
                // PRINTING IN RULES_MSDD.TXT
                logfiles.println((i+1) + " " + this.getRule(i).toString() + " " + this.getRule(i).occurrencies + "x out of " + this.getRule(i).prec_occurrencies + " PROB :" + this.getRule(i).getProb(), 5);
            }
        }
        
        
        System.out.println("RULELIST " + str + " SIZE : " + n);
        return 0;
    }
    
        
        
        public int getMostFrequentRuleIndex () {
            double max = 0;
            int index = 0;
            for (int i=0; i<this.rulelist.size(); i++) {
                if (this.rulelist.get(i).occurrencies > max) {
                    max = this.rulelist.get(i).occurrencies;
                    index = i;
                }
            }
            
            return index;
        }
        
        public int getMaxOcc () {
            int max = 0;
            for (int i=0; i<this.rulelist.size(); i++) {
                if (this.rulelist.get(i).occurrencies > max) {
                    max = this.rulelist.get(i).occurrencies;
                }
            }
            
            return max;
        }
        
        
        public ArrayList getRuleIndexes (int ruleOcc) {
            
            ArrayList indexes = new ArrayList ();
            for (int i=0; i<this.rulelist.size(); i++) {
                if (this.rulelist.get(i).occurrencies == ruleOcc) {
                    
                    indexes.add(i);
                }
            }
            
            return indexes;
        }
        

        
        public Rule getMostFrequentRule () {
    
            
            return this.rulelist.get(this.getMostFrequentRuleIndex());
        }
        
        
        
        
    public int size () {
            
            return this.rulelist.size();
        }
        
        
    public RuleList removeUnexpandable () {
        
        RuleList copy = new RuleList();
        for(int i=0; i<this.size(); i++) {
            if (this.getRule(i).isExpandable())
                copy.addRule(this.getRule(i));
        } 
        
        return copy;
    }
//
//    public RuleList removeUnseenRules (SensorList sList) {
//        
//        RuleList copy = new RuleList();
//        for(int i=0; i<this.size(); i++) {
//            if (sList.containsRule(this.getRule(i)))
//                copy.addRule(this.getRule(i));
//        } 
//        
//        return copy;
//    }
    
    
    public RuleList sort () {
        RuleList copy = new RuleList();
        int max = this.getMaxOcc();
        for(int i=max; i>=0; i--) {
            ArrayList <Integer> indexes = this.getRuleIndexes(i);
            if (!indexes.isEmpty()) {
                for (int y=0; y < indexes.size(); y++) {
                    copy.addRule(this.getRule(indexes.get(y)));
                }
            }
            
        }
        
        return copy;
    }
    
    
    public boolean containsRule (Rule rule) {
        
        for (int i=0; i<this.size(); i++) {
            if (this.getRule(i).ruleMatch_exact(rule)) 
                return true;
        }
        return false;
    }
    
    public RuleList removeWildcardSuccessors () {
        
        RuleList res = new RuleList();
        String str = "******";
        Sensor root = new Sensor (str, this.getRule(0).precondition.tokenMap);
        for (int i = 0; i < this.size(); i++) {
            
            if (!this.getRule(i).postcondition.sensorMatch_exact(root)) {
                res.addRule(this.getRule(i));
            } 
        }
        
        return res;
    }
    
   
    
public ArrayList findFreeloaders (SensorList sList, SensorMap sMap) {
        
        ArrayList res = new ArrayList();
        int count = 0;
        for (int i = this.size()-1; i > 0; i--) {
            
            if (((this.size()-i)%2000) == 0 )
                System.out.println((this.size()-i) + " / " + (this.size()-1));
            for (int j=i-1; j  >= 0; j--) {
                
                 
                if (this.getRule(i).isMoreGeneralizedRule(this.getRule(j))) {
                    float a = sMap.Gstatistic(this.getRule(i), this.getRule(j));
                    //float a = sList.Gstatistic(this.getRule(i), this.getRule(j));
                    if ( a < 3.841) {
                       //3.841 gives 5% statistical significance
                       //2.706 gives 10%
                       //0.455 gives 50%
                   
                       count++;
                       res.add(i);
                       //System.out.println("Freeloader " + count + " from rule : " + this.getRule(i) + "(ID " + this.getRule(i).id + " at rule : " + this.getRule(j) + " Gstat : " + a);
                       break;
                   }
               } 
            }

        }
        
        return res;
    }



    public RuleList removeFreeloaders (ArrayList indexesToRemove) {
    
    for (int i=0; i < indexesToRemove.size(); i++) {
        this.remove((int) indexesToRemove.get(i));
    }
    
    return this;
}


    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.println(this.getRule(i).precondition.simple() + " " + this.getRule(i).postcondition.simple() + " "+ this.getRule(i).id + " " + this.getRule(i).ruleset_id + " " + this.getRule(i).prec_occurrencies + " " + this.getRule(i).occurrencies, 5);
        }
    }



    public int fromFile (SensorList sList, TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_5;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                String [] a = line.split(" ");
                
                
//                System.out.println("Line " + i + " : " + line );
//                
//                for (int i2 = 0; i2 < a.length; i2++) {
//                    
//                    System.out.println(a[i2]);
//                }
                Sensor s1 = new Sensor(a[0], t);
                Sensor s2 = new Sensor(a[1], t);
                Rule r1 = new Rule(s1, s2, sList);
                r1.id = Integer.parseInt(a[2]);
                r1.ruleset_id = Integer.parseInt(a[3]);
                r1.prec_occurrencies = Integer.parseInt(a[4]);
                r1.occurrencies = Integer.parseInt(a[5]);
                
                this.addRule(r1);
                
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
