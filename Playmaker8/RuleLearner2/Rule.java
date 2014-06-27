/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package RuleLearner2;

import Token.*;

/**
 *
 * @author virgile
 */
public class Rule {
    
    protected Sensor precondition;
    protected Sensor postcondition;
    public int occurrencies;
    
    
    
    
    public Rule (Sensor precondition, Sensor postcondition) {
        this.precondition = precondition;
        this.postcondition = postcondition;
        this.occurrencies = 0;
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
    
    
    
    public boolean sensorMatch (Sensor s, Sensor expression) {
        boolean res = true;
        
        if (s.tokens.size() != expression.tokens.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < s.tokens.size(); i++) {
            
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
        return (this.precondition.getString().toString() + this.postcondition.getString().toString());
    }
    
    
//    @Override
//    public boolean equals(Object obj){
//         if(obj == null)
//           return false;
//         else if(this==obj)
//           return true;
//         else 
//           return this.hashCode() == ((Rule)obj).hashCode();
//    }
//    
//    
//    @Override
//    public int hashCode() {
//         return (this.precondition.hashCode() + this.postcondition.hashCode());
//   }
    
    
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
    
    
    
    public RuleList getChildren (SensorList database) {
        RuleList children = new RuleList ();
        
        int length = this.size();
        //System.out.println(length);
        
        
        // SUCCESOR PART
        ////////////////
        for (int i=length/2-2; i>=0; i--) {
            
            if (this.postcondition.getToken(i).isWildcard()) {
                
            
                SensorList a = this.postcondition.expand(i);
            //a.printList();

                for (int j =0; j < a.size() ; j++) {
                    Rule rule = new Rule(this.precondition,a.getSensor(j+1));
                    children.addRule(rule);
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
                    Rule rule2 = new Rule(a.getSensor(j+1),this.postcondition);
                    children.addRule(rule2);
                }
            }
        }
        
        
        return children;
   
    }
    
}
