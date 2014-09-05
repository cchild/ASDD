package V_RuleLearner;

import Logging.*;
import V_Sensors.*;
import java.io.*;
import java.util.*;


/**
 *
 * @author virgile
 */
public class RuleList {
    
    public ArrayList <Rule> rulelist;
    
    
    public RuleList() {
        
        rulelist = new ArrayList <> ();
        
    }
    
    // Returns a copy of this RuleList
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
    
    
    // Returns the Rule with unique ID "id"
    public Rule getRuleByID (int id) {
        for (int i = 0; i < this.size(); i++) {
            if (this.rulelist.get(i).id == id)
                return this.rulelist.get(i);
        }
        
        // IF ID DOES NOT EXIST, WHAT SHOULD WE RETURN ?
        // Never happens anyway
        return null; 
    }    
    
    
    
    
    public int addRule (Rule rule) {
        
        rulelist.add(rule);
        
        return 0;
    }
    
    
    // Adds all Rules from input RuleList
    public int addRuleList (RuleList rulelist) {
        int a = 0;
        for (int i=0; i<rulelist.size(); i++) {
            if (!this.containsRule(rulelist.getRule(i))) {
                this.rulelist.add(rulelist.getRule(i));
                a++;
            }
        }
        
        return a;
    }
    
    
    // Old version was using SensorList
    //
    // This new one uses SensorMap & RuleMap
    //
    // To use the old version, just uncomment old b and c calls
    // Returns the number of Rules added
    public int addWithCheck (RuleList rulez, SensorList sList, SensorMap sMap, RuleMap rMap) {
        int a = 0;
        int b ;
        int c ;
        
        for (int i=0; i<rulez.size(); i++) {
            
            //b = sList.indexesOfRule(rulez.getRule(i)).size();
            b = rMap.getMatchingOccurencies(rulez.getRule(i));

            if (b>0) {

                c = sMap.getMatchingOccurencies(rulez.getRule(i).getPrecondition());
                //c = sList.indexesOfSensor(rulez.getRule(i).getPrecondition()).size();
                if ((!this.containsRule(rulez.getRule(i))) ) {

                    Rule rule = new Rule (rulez.getRule(i).precondition,rulez.getRule(i).postcondition);
                    rule.occurrencies = b;
                    rule.prec_occurrencies = c;
                    this.addRule(rule);
                    a++;
                    
                }
            }  

        }
        
        return a;
    }
    
    
    
    public int remove (int i) {
        this.rulelist.remove(i);
        
        return 0;
    }
    

    
    // Returns the index of input Rule in this RuleList
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
    

    // Returns a matching index
    public int findMatchingRuleIndex (Rule rule) {
        int i = 0;
        
        while (i < this.size()) {
            
            if (this.getRule(i).ruleMatch(rule)) {
                return i;
            }
            
            i++;
        }
        
        return -1;
    }
    
    


    // Returns the Rule with unique ID "id"
    public Rule findRuleById (int id) {
        
        for (int i = 0; i<this.size(); i++) {
            
            if (this.getRule(i).id == id) { 
                
                return this.getRule(i);
            }
        }
        
       
        return null;
    }
    
    
    public int printList () {

        return printList("");
    }
        
    public int printList (String str) {
        
        int n = this.rulelist.size();
        
        System.out.println("\nPRINTING "+ str + " RULELIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            
            System.out.println("Rule " + (i+1) + " (ID" + this.rulelist.get(i).id + ") " + this.rulelist.get(i).toString() + " " + this.rulelist.get(i).occurrencies + "x" + " prob : " + this.rulelist.get(i).getProb());
        }
        
        
        System.out.println("RULELIST " + str + " SIZE : " + n);
        
        return 0;
    }
    
    

    
    // Returns the occurrencies of the most occurring Rule
    public int getMaxOcc () {
        int max = 0;
        for (int i=0; i<this.rulelist.size(); i++) {
            if (this.rulelist.get(i).occurrencies > max) {
                max = this.rulelist.get(i).occurrencies;
            }
        }

        return max;
    }
        
      
    // Returns the indexes of Rules that occurs the same numer of time then ruleOcc
    public ArrayList getRuleIndexes (int ruleOcc) {

        ArrayList indexes = new ArrayList ();
        
        for (int i=0; i<this.rulelist.size(); i++) {
            if (this.rulelist.get(i).occurrencies == ruleOcc) {

                indexes.add(i);
            }
        }

        return indexes;
    }
        

  
    public int size () {
            
            return this.rulelist.size();
        }
        
 
    // Sorts the OpenList for MSDD Algorithm
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
    
    
    // Returns true if input Rule is in the RuleList
    public boolean containsRule (Rule rule) {
        
        for (int i=0; i<this.size(); i++) {
            if (this.getRule(i).ruleMatch_exact(rule)) 
                return true;
        }
        return false;
    }
    
    
    // Removes all of the Rules that have "root" Post.
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
    
   
    // Returns an ArrayList with the Indexes of Freeloaders
    //
    // It is then used in removeFreeloaders below
    public ArrayList findFreeloaders (SensorMap sMap) {
        
        ArrayList res = new ArrayList();
         
        for (int i = this.size()-1; i > 0; i--) {
            
            if (((this.size()-i)%2000) == 0 )
                System.out.println((this.size()-i) + " / " + (this.size()-1));
            for (int j=i-1; j  >= 0; j--) {
                
                 
                if (this.getRule(i).isMoreGeneralizedRule(this.getRule(j))) {
                    
                    
                    float a = sMap.Gstatistic(this.getRule(i), this.getRule(j));
                    
                    if ( a < 3.841) {
                       //3.841 gives 5% statistical significance
                       //2.706 gives 10%
                       //0.455 gives 50%
                   
                       //System.out.println("Freeloader from " + this.getRule(i) + " at rule : " + this.getRule(j) + " G-Stat : " + a);
                       res.add(i);
                       break;
                   }
               } 
            }

        }
        
        return res;
    }


    // Removes the specified Freeloaders
    //
    // Backwards to avoid silly mistakes
    public RuleList removeFreeloaders (ArrayList indexesToRemove) {
    
        for (int i = indexesToRemove.size() - 1; i >= 0 ; i--) {
            System.out.println("Removing index " + indexesToRemove.get(i) + " size : " + this.size());
            this.remove((int) indexesToRemove.get(i));
        }

        return this;
    }

    
    
    // Removes the specified Freeloaders
    //
    // Backwards to avoid silly mistakes
    public RuleList removeFreeloaders_ASDD (ArrayList indexesToRemove) {
    
        for (int i = 0; i < indexesToRemove.size() ; i++) {
            this.remove((int) indexesToRemove.get(i));
        }

        return this;
    }

    
    // Exports in file 5
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.println("[" + this.getRule(i).precondition.simple() + "] [" + this.getRule(i).postcondition.simple() + "] "+ this.getRule(i).id + " " + this.getRule(i).ruleset_id + " " + this.getRule(i).prec_occurrencies + " " + this.getRule(i).occurrencies, 5);
        }
    }


    // Builds the ClosedList from FILE_NAME_5
    public int fromFile (SensorList sList, TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_5;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                String [] a = line.split(" ");
                
                String sen1 = "";
                String sen2 = "";
                
                for (int j = 1; j < a[0].length() -1; j++) {
                    
                    sen1 = sen1.concat(String.valueOf(a[0].charAt(j)));
                }
                
                for (int j = 1; j < a[1].length() -1; j++) {
                    
                    sen2 = sen2.concat(String.valueOf(a[1].charAt(j)));
                }
                
                Sensor s1 = new Sensor(sen1, t, 2);
                Sensor s2 = new Sensor(sen2, t, 2);
                Rule r1 = new Rule(s1, s2);
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
    
    
    
    
    // Generates all the Level 1 Rules
    // 
    // Precondition with action included, Postcondition without
    //
    // ASDD Algorithm
    public RuleList generate_level1_rules (TokenMap tokenMap, RuleMap rMap, SensorMap sMap) {
        
        RuleList res = new RuleList ();

        Sensor root = new Sensor (tokenMap);
        
        for (int i = 0; i < tokenMap.TokenList.size(); i++) {
            
            SensorList expanded_at_i = root.expand(i);
            
            for (int j = 0; j < expanded_at_i.size(); j++) {
                
                // Precondition = root
                Rule rule = new Rule (expanded_at_i.getSensor(j+1), root);
                rule.ruleset_id = -1;
                res.addRule(rule);
                

            }
            
            
            for (int j = 0; j < expanded_at_i.size(); j++) {
                             
                // Postcondition = root
                if (i < tokenMap.TokenList.size() - 1) {
                
                    Rule rule = new Rule (root, expanded_at_i.getSensor(j+1));
                    rule.ruleset_id = -1;
                    res.addRule(rule);
                }
                
            }
        }
        
        res.update_occurrencies(rMap, sMap);
        return res;
    }
    
    
    // ASDD Apriori Gen function
    public RuleList apriori_gen (RuleMap rMap, SensorMap sMap, int current_level) {
        
        RuleList res = new RuleList ();
        
        
        
        for (int i = 0; i < this.size()-1; i++) {
            
            if (i == 500)
                System.out.println("");
            
            if (i % 500 == 0 && i != 0)
                System.out.println(i + " / " + (this.size()-1));
            
            for (int j = i+1; j < this.size(); j++) {
                
                
                // For Each continue, we avoid generating a combined Rule, because something 
                // tells us it's useless
                
                // Both effect Rules
                if (this.getRule(i).getPostcondition().has_effect() && this.getRule(j).getPostcondition().has_effect()) {

                    continue;
                }

                // Same Rule
                if (!this.getRule(i).match_until_last_element(this.getRule(j))) {

                   continue;
                }               
              


                Rule rule = this.getRule(i).merge(this.getRule(j));
                rule.ruleset_id = -1;

                // Postcondition with too much Wildcards
                if (rule.getPostcondition().numberOfNonWildcards() > 1)
                    continue;

                // Level != k + 1
                if (rule.detect_level() != this.getRule(i).detect_level() + 1)
                    continue;



                boolean all_subsets_are_good = true;

                RuleList rule_subsets = rule.get_subsets();

                // Subsets part
                for (int h = 0; h < rule_subsets.size(); h++) {

                     
                    if (this.findMatchingRuleIndex(rule_subsets.getRule(h)) == -1) {
                        all_subsets_are_good = false;
                        
                        break;
                    }
                }

                if (all_subsets_are_good) {

                        if (res.findRuleIndex(rule) == -1) {
                            res.addRule(rule);
                            
                        }

                        
                }

                
            } // for j
        } // for i
        
        
        
        res.update_occurrencies(rMap, sMap);
        
        return res;
    }
    
    
    // Occurrencies Pruning, or "support" pruning
    // 
    // rule_index != -1 means support = 1, 
    // change to rule_index.size() < X to get support to X
    public RuleList ASDD_pruning_occurrencies (RuleMap rMap) {
        
        RuleList res = new RuleList ();
        
        System.out.println("Running Occurrencies Prunning");
        
        for (int i = 0; i < this.size(); i++) {
            
            int rule_index = rMap.findRule(this.getRule(i));
            
            if (rule_index != -1) {
                
                res.addRule(this.getRule(i).copy());
            }
            
            else {
                //System.out.println(this.getRule(i) + " was Pruned");
            }
                
        }
        
        
        return res;
    }
    
    
    
    
    public void update_occurrencies (RuleMap rMap, SensorMap sMap) {
        
        for (int i = 0; i < this.size(); i++) {
            
            this.getRule(i).occurrencies = rMap.getMatchingOccurencies(this.getRule(i));
            this.getRule(i).prec_occurrencies = sMap.getMatchingOccurencies(this.getRule(i).getPrecondition());
            
        }
    }

    
    

    

    
}
