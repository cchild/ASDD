package V_RuleLearner;

import Logging.*;

import V_Sensors.*;


import java.io.File;
import java.io.FileNotFoundException;

import static java.lang.Math.min;
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
    // To return to the old version, just uncomment old b and c calls
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
    

    
    // Returns the occurrences of the most occurring Rule
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
            this.remove((int) indexesToRemove.get(i));
        }

        return this;
    }


    // Exports in file 5
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.println(this.getRule(i).precondition.simple() + " " + this.getRule(i).postcondition.simple() + " "+ this.getRule(i).id + " " + this.getRule(i).ruleset_id + " " + this.getRule(i).prec_occurrencies + " " + this.getRule(i).occurrencies, 5);
        }
    }


    // Builds a RuleList from FILE_NAME_5
    public int fromFile (SensorList sList, TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_5;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                String [] a = line.split(" ");
                

                Sensor s1 = new Sensor(a[0], t);
                Sensor s2 = new Sensor(a[1], t);
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

    
    
    
    

    
}
