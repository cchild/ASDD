package RuleLearner2;

import Token.SensorList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author virgile
 */
public class RuleList {
    
    public ArrayList <Rule> rulelist;
    
    
    public RuleList() {
        
        rulelist = new ArrayList <> ();
        
    }
    
    
    public RuleList(RuleList rulelisttocopy) {
        
        rulelist = new ArrayList <> ();
        for (int i=0; i<rulelisttocopy.size(); i++) {
            rulelist.add(rulelisttocopy.getRule(i));
        }
        
    }
    
    public Rule getRule (int i) {
        return this.rulelist.get(i);
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
    
    public int addWithCheck (RuleList rulez, SensorList sList) {
        int a = 0;
        int b = 0;
        
        for (int i=0; i<rulez.size(); i++) {
            b = sList.indexOfRule(rulez.getRule(i)).size();
            if ((!this.containsRule(rulez.getRule(i))) && (b > 0)) {
                Rule rule = new Rule (rulez.getRule(i).precondition,rulez.getRule(i).postcondition);
                rule.occurrencies = b;
                this.addRule(rule);
                a++;
            }
        }
        
        return a;
    }
    
    
    public int remove () {
        this.rulelist.remove(0);
        
        return 0;
    }
    
    
    public int findRule (Rule rule) {
        
        int occurrences = Collections.frequency(this.rulelist, rule);
        return occurrences;
    }
    
    
     public int findRule_exact (Rule rule) {
        
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
            System.out.println("Rule " + (i+1) + " " + this.rulelist.get(i).toString() + " " + this.rulelist.get(i).occurrencies + "x");
        }
        
        
        System.out.println("RULELIST " + str + " SIZE : " + n);
        return 0;
    }
    
    
    public int printListByWithProb (String str, SensorList sList, int number) {
        
        double ruleOcc;
        int ruleOcc_int;
        double precOcc;
        
        int n = this.rulelist.size();
        System.out.println("\nPRINTING "+ str + " RULELIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            if ((i % number) ==0) {
                precOcc = sList.indexesOfSensor(this.getRule(i).precondition).size();
                int succOcc = sList.indexesOfSensor(this.getRule(i).postcondition).size();
                ruleOcc = this.getRule(i).occurrencies;
                ruleOcc_int = (int) ruleOcc;
                
                double proba = ruleOcc / precOcc;
                proba = Math.round(proba * 100);
                proba = proba/100;
                
                System.out.println("Rule " + (i+1) + " " + this.getRule(i).toString() + " " + ruleOcc_int + "x  PROB :" + proba );
            }
        }
        
        
        System.out.println("RULELIST " + str + " SIZE : " + n);
        return 0;
    }
    
    
    public int printList (String str, ArrayList stock) {
        
        int n = this.rulelist.size();
        System.out.println("\nPRINTING "+ str + " RULELIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            System.out.println("Rule " + (i+1) + " " + this.rulelist.get(i).toString() + " " + this.rulelist.get(i).occurrencies + "x  " + "Prob : " + stock.get(i));
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
        
        public int getMostFrequentRuleNumber () {
    
            
            return this.getMostFrequentRuleIndex()+1;
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

    public RuleList removeUnseenRules (SensorList sList) {
        
        RuleList copy = new RuleList();
        for(int i=0; i<this.size(); i++) {
            if (sList.containsRule(this.getRule(i)))
                copy.addRule(this.getRule(i));
        } 
        
        return copy;
    }
    
    
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
    
    
    
    

    
}
