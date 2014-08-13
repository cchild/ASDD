/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_RuleLearner;

import V_Sensors.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author virgile
 */
public class Rule {
    
    protected Sensor precondition;
    protected Sensor postcondition;
    //public ArrayList <Integer> prec_indexes;
    private SensorList sList;
    public int occurrencies;
    public int prec_occurrencies;
    public int ruleset_id;
    public int id;
    public static AtomicInteger counter = new AtomicInteger();
    
    
    
    
    public Rule (Sensor precondition, Sensor postcondition, SensorList sList) {
        this.precondition = precondition;
        this.postcondition = postcondition;
        //this.prec_indexes = sList.indexesOfSensor(precondition);
        
        //this.prec_occurrencies = 0;
        this.ruleset_id = -1;
        this.id = counter.incrementAndGet();
        
        //ArrayList <Integer> a = sList.indexesOfSensor(postcondition);
        

    }
    
    public Sensor getPrecondition () {
        return precondition;
    }
    
    public Sensor getPostcondition () {
        return postcondition;
    }
    
    public int setPrecondition (Sensor s) {
        
        this.precondition = s;
        return 0;
    }
    
    public int setPostcondition (Sensor s) {
        
        this.postcondition = s;
        return 0;
    }
    
    
    public double getProb () {
        
        double a = this.occurrencies;
        double b = this.prec_occurrencies;
        double res = a/b;
        
        res = Math.round(res * 1000);
        res = res/1000;
        return res;
    }
    
    
    
   public Rule copy () {
        
        Rule copy = new Rule (this.precondition,this.postcondition, this.sList);
        copy.prec_occurrencies = this.prec_occurrencies;
        copy.occurrencies = this.occurrencies;
        return copy;
    }    
    
    
    
    public boolean sensorMatch (Sensor s, Sensor expression) {
        boolean res = true;
        
        if (s.tokenList.size() != expression.tokenList.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < s.tokenList.size(); i++) {
            
            if(!s.getToken(i).match(expression.getToken(i))) {
                //System.out.println();
                res = false;
            }
        }
        
        
        return res;
    }
    
    
    
    public boolean ruleMatch (Sensor precondition, Sensor postcondition) {
        
        return (sensorMatch(precondition,this.precondition) && sensorMatch(postcondition,this.postcondition));
    
    }
    
    public boolean ruleMatch (Rule rule) {
        
        return (rule.precondition.sensorMatch(this.precondition) && rule.postcondition.sensorMatch(this.postcondition));
    
    }
    
    public boolean ruleMatch_exact (Rule rule) {
        
        return (rule.precondition.sensorMatch_exact(this.precondition) && rule.postcondition.sensorMatch_exact(this.postcondition));
    
    }
    
    
    @Override
    public String toString () {
        
        return (this.getPrecondition().toString() + this.getPostcondition().toString());
    }
    
    
     @Override
    public boolean equals(Object obj){
         if(obj == null)
           return false;
         if(this==obj)
           return true;
         
         Rule rule = (Rule) obj;
         
         return (this.ruleMatch(rule));
    }
    
    
    @Override
    public int hashCode() {
         return (this.precondition.hashCode() + this.postcondition.hashCode());
   }
    
    
    public int size () {
        
        return this.precondition.size() + this.postcondition.size();
    }
    
    
    public boolean isExpandable () {
        return (this.precondition.isExpandable()>(-1) || this.postcondition.isExpandable()>(-1) ) ;
    }
    
    
    
//////    public RuleList getChildren () {
//////        RuleList children = new RuleList ();
//////        
//////        int length = this.size();
//////        //System.out.println(length);
//////        
//////        
//////        // SUCCESOR PART
//////        ////////////////
//////        for (int i=length/2-2; i>=0; i--) {
//////            
//////            if (this.postcondition.getToken(i).isWildcard()) {
//////                
//////            
//////                SensorList a = this.postcondition.expand(i);
//////            //a.printList();
//////
//////                for (int j =0; j < a.size() ; j++) {
//////                    Rule rule = new Rule(this.precondition,a.getSensor(j+1), this.sList);
//////                    children.addRule(rule);
//////                }
//////            }
//////        }
//////        
//////        
//////        // PRECURSOR PART
//////        /////////////////
//////        for (int i=length/2-1; i>=0; i--) {
//////            
//////            if (this.precondition.getToken(i).isWildcard()) {
//////             
//////
//////                SensorList a = this.precondition.expand(i);
//////            //a.printList();
//////
//////                for (int j =0; j < a.size() ; j++) {
//////                    Rule rule2 = new Rule(a.getSensor(j+1),this.postcondition, this.sList);
//////                    children.addRule(rule2);
//////                }
//////            }
//////        }
//////        
//////        
//////        return children;
//////   
//////    }
    
    
    public RuleList getChildren () {
        RuleList children = new RuleList ();
        
        int length = this.size();
        //System.out.println(length);
        
        
        // SUCCESOR PART
        ////////////////
        for (int i=length/2-2; i>=0; i--) {
            
            if (this.postcondition.numberOfNonWildcards() == 0) {
                if (this.postcondition.getToken(i).isWildcard()) {


                    SensorList a = this.postcondition.expand(i);
                //a.printList();

                    for (int j =0; j < a.size() ; j++) {
                        Rule rule = new Rule(this.precondition,a.getSensor(j+1), this.sList);
                        children.addRule(rule);
                    }
                }
            }
        }
        
        
        // PRECURSOR PART
        /////////////////
        for (int i=length/2-1; i>=0; i--) {
            
            if (this.precondition.getToken(i).isWildcard()) {
             

                SensorList a = this.precondition.expand(i);
            //a.printList();

                for (int j =0; j < a.size() ; j++) {
                    Rule rule2 = new Rule(a.getSensor(j+1),this.postcondition, this.sList);
                    children.addRule(rule2);
                }
            }
        }
        
        
        return children;
   
    }    
    
    public boolean isMoreGeneralizedRule (Rule prospect) {
        
        
        if (this.precondition.numberOfWildcards() >= prospect.precondition.numberOfWildcards())
                return false;
            
                
        if (!this.postcondition.sensorMatch_exact(prospect.postcondition))           
                return false;
//        
        
        for (int i=0; i<this.precondition.size(); i++) {
            
            if ((this.precondition.getToken(i).isWildcard()) && (prospect.precondition.getToken(i).isNotWildcard())) {
                    return false;
                }
                // !this.precondition.getToken(i).match(prospect.precondition.getToken(i)))
            if (!this.precondition.getToken(i).isWildcard() ) {
                
                if (!this.precondition.getToken(i).match(prospect.precondition.getToken(i))) {
                    
                    
                    return false;
                }
                    
            }
        }

        
    
    
        return true;
    }
    
    
    
    public boolean isSameRuleSet (Rule prospect) {
        
        if (this.size() != prospect.size())
            return false; 
        
        
        if (this.precondition.numberOfWildcards() != prospect.precondition.numberOfWildcards())
            return false;        
        
        if(!this.precondition.sensorMatch_exact(prospect.precondition))
            return false;
        
        for (int i=0; i<this.precondition.size(); i++) {
            
            if (this.postcondition.getToken(i).isWildcard()) {
                
                if (prospect.postcondition.getToken(i).isNotWildcard())
                    return false;
            }
            
            if (this.postcondition.getToken(i).isNotWildcard()) {
                
                if (prospect.postcondition.getToken(i).isWildcard())
                    return false;
                
                
            }
        }
        
        return true;
        
    }
    
    
    public RuleList getSameRuleSetRules (SensorList sList,RuleList closedList, RuleMap rMap) {
        
        RuleList res = new RuleList ();
        
        SensorList expanded = this.postcondition.expandNonWildcards();
        
        for (int i = 0; i < expanded.size(); i++) {
            
            Rule a = new Rule (this.precondition, expanded.getSensor(i+1), this.sList);
            a.prec_occurrencies = this.prec_occurrencies;
            a.ruleset_id = this.ruleset_id;
            // USING RMAP
            a.occurrencies = rMap.getMatchingOccurencies(a);
            if (a.occurrencies > 0)
                res.addRule(a);
        }
        
        return res;
    }

  
    
}
