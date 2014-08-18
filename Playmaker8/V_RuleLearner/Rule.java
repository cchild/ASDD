
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
    private SensorList sList;
    public int occurrencies;
    public int prec_occurrencies;
    public int ruleset_id;
    public int id;
    public static AtomicInteger counter = new AtomicInteger();
    
    
    
    
    public Rule (Sensor precondition, Sensor postcondition) {
        
        this.precondition = precondition;
        this.postcondition = postcondition;

        this.ruleset_id = -1;
        this.id = counter.incrementAndGet();       

    }
    
    public Sensor getPrecondition () {
        return precondition;
    }
    
    public Sensor getPostcondition () {
        return postcondition;
    }
    
    public void setPrecondition (Sensor s) {
        
        this.precondition = s;
        
    }
    
    public void setPostcondition (Sensor s) {
        
        this.postcondition = s;

    }
    
    
    // Returns the Probability of a Rule
    public double getProb () {
        
        double a = this.occurrencies;
        double b = this.prec_occurrencies;
        double res = a/b;
        
        res = Math.round(res * 1000);
        res = res/1000;
        return res;
    }
    
    
    // Returns a copy of the Rule
    public Rule copy () {
        
        Rule copy = new Rule (this.precondition,this.postcondition);
        copy.prec_occurrencies = this.prec_occurrencies;
        copy.occurrencies = this.occurrencies;
        return copy;
    }    
    
    
    // Another way of testing Sensor Matching
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
    
    
    // Returns true if both prec and post are matching inputs
    public boolean ruleMatch (Sensor precondition, Sensor postcondition) {
        
        return (sensorMatch(precondition,this.precondition) && sensorMatch(postcondition,this.postcondition));
    
    }
    
    // Returns true if a Rule matches another one.
    // E.g. both prec and post match the other one prec. and post.
    public boolean ruleMatch (Rule rule) {
        
        return (rule.precondition.sensorMatch(this.precondition) && rule.postcondition.sensorMatch(this.postcondition));
    
    }
    
    // Returns true if the two Rules are exactly matching
    public boolean ruleMatch_exact (Rule rule) {
        
        return (rule.precondition.sensorMatch_exact(this.precondition) && rule.postcondition.sensorMatch_exact(this.postcondition));
    
    }
    
    
    @Override
    public String toString () {
        
        return (this.getPrecondition().toString() + this.getPostcondition().toString());
    }
    

    
    
    public int size () {
        
        return this.precondition.size() + this.postcondition.size();
    }
    
    
    // Returns true if Prec or Post is Expandable, e.g. has at least One Wildcard
    public boolean isExpandable () {
        return (this.precondition.isExpandable()>(-1) || this.postcondition.isExpandable()>(-1) ) ;
    }
    
    
  
    
    // Returns MSDD children of a Rule
    //
    // See MSDD Algorithm
    public RuleList getChildren () {
        
        RuleList children = new RuleList ();
        
        int length = this.size();
        
        ////////////////
        // SUCCESOR PART
        ////////////////
        for (int i=length/2-2; i>=0; i--) {
            
            if (this.postcondition.numberOfNonWildcards() == 0) {
                if (this.postcondition.getToken(i).isWildcard()) {


                    SensorList a = this.postcondition.expand(i);
                

                    for (int j =0; j < a.size() ; j++) {
                        Rule rule = new Rule(this.precondition,a.getSensor(j+1));
                        children.addRule(rule);
                    }
                }
            }
        }
        
        /////////////////
        // PRECURSOR PART
        /////////////////
        for (int i=length/2-1; i>=0; i--) {
            
            if (this.precondition.getToken(i).isWildcard()) {
             

                SensorList a = this.precondition.expand(i);
            

                for (int j =0; j < a.size() ; j++) {
                    Rule rule2 = new Rule(a.getSensor(j+1),this.postcondition);
                    children.addRule(rule2);
                }
            }
        }
        
        
        return children;
   
    }    
    
    
    // Returns true if the input Rule is a more generalized version of this one
    public boolean isMoreGeneralizedRule (Rule prospect) {
        
        
        if (this.precondition.numberOfWildcards() >= prospect.precondition.numberOfWildcards())
                return false;
            
                
        if (!this.postcondition.sensorMatch_exact(prospect.postcondition))           
                return false;       
        
        for (int i=0; i<this.precondition.size(); i++) {
            
            if ((this.precondition.getToken(i).isWildcard()) && (prospect.precondition.getToken(i).isNotWildcard())) 
                    return false;
                
                
            if (!this.precondition.getToken(i).isWildcard() ) {
                
                if (!this.precondition.getToken(i).match(prospect.precondition.getToken(i))) {
                                        
                    return false;
                }
                    
            }
        }

        
    
    
        return true;
    }
    
    
    
    // Returns true if input Rule belongs to the Same RuleSet.
    //
    // e.g. has same Prec, and same Non-Wildcarded indexes in post.
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
    
    
    // Generates a RuleList of all the "similar" Rules that should be in the Same RuleSet
    public RuleList getSameRuleSetRules (SensorList sList,RuleList closedList, RuleMap rMap) {
        
        RuleList res = new RuleList ();
        
        SensorList expanded = this.postcondition.expandNonWildcards();
        
        for (int i = 0; i < expanded.size(); i++) {
            
            Rule a = new Rule (this.precondition, expanded.getSensor(i+1));
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
