
package V_RuleLearner;

import V_Sensors.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author virgile
 */
public class Rule {
    
    protected Sensor precondition;
    protected Sensor postcondition;
    public int occurrencies;
    public int prec_occurrencies;
    public int ruleset_id;
    public int id;
    public double RVRL_value;
    public static AtomicInteger counter = new AtomicInteger();
    
    
    
    // A Rule is Sensor + Sensor
    // A Unique ID is generated each time a Rule is created
    public Rule (Sensor precondition, Sensor postcondition) {
        
        this.precondition = precondition;
        this.postcondition = postcondition;

        this.ruleset_id = -1;
        this.id = counter.incrementAndGet();  
        this.RVRL_value = 0.0;

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
    
    
    public void increase_RVRL (double value) {
        
        this.RVRL_value = this.RVRL_value + value;

    }
    
    public double get_RVRL () {
        
        return this.RVRL_value;

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
    // I.e. both prec and post match the other one prec. and post.
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
    
    
    // Returns true if Prec or Post is Expendable, e.g. has at least One Wildcard
    public boolean isExpendable () {
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
    
    
    
    // Merges two Rules
    public Rule merge (Rule rule2) {
        
        Sensor prec1 = this.getPrecondition().copy();
        Sensor post1 = this.getPostcondition().copy();
        
        Sensor prec2 = rule2.getPrecondition().copy();
        Sensor post2 = rule2.getPostcondition().copy();
        
        Sensor merged_prec = prec1.merge(prec2);
        Sensor merged_post = post1.merge(post2);
        
        Rule rule = new Rule (merged_prec, merged_post);
        
        
        return rule;
    }
    
    
    // Returns the level of a Rule (Prec.level + Succ.level)
    public int detect_level () {
        
        return (this.getPrecondition().detect_level() + this.getPostcondition().detect_level());
    }
    
    
    
    // Returns all the Subsets of a Rule
    // (Rules with level k-1 at good indexes
    public RuleList get_subsets () {
        
        RuleList res = new RuleList ();
        
        SensorList preconditions = new SensorList ();
        SensorList postconditions = new SensorList ();
        
        
        ArrayList precondition_indexes = this.getPrecondition().get_nonWildcarded_indexes();
        ArrayList postcondition_indexes = this.getPostcondition().get_nonWildcarded_indexes();
        

        
        // Creates all Precondition Subsets
        for (int i = 0; i < precondition_indexes.size(); i++) {           
            
            Sensor prec = new Sensor (this.getPrecondition().tokenMap);
            
            for (int t = 0; t < prec.size(); t++) {
                
                prec.getToken(t).setReference(this.getPrecondition().getToken(t).getReference());
            }
            
            prec.getToken((int) precondition_indexes.get(i)).setReference(0);
            
            preconditions.addSensor(prec);
        }
        
        // We also need the original Precondition
        preconditions.addSensor(this.getPrecondition().copy());
        
        // Creates all Postcondition Subsets
        for (int h = 0; h < postcondition_indexes.size(); h++) {
            
            Sensor post = new Sensor (this.getPostcondition().tokenMap);
            
            for (int t = 0; t < post.size(); t++) {
                
                post.getToken(t).setReference(this.getPostcondition().getToken(t).getReference());
            }
            
            post.getToken((int) postcondition_indexes.get(h)).setReference(0);
            
            postconditions.addSensor(post);
        }
        
        
        // We also need the original Precondition
        postconditions.addSensor(this.getPostcondition().copy());
        
        
        for (int i = 0; i < preconditions.size(); i++) {
            
            for (int j = 0; j < postconditions.size(); j++) {
                
                Rule rule = new Rule (preconditions.getSensor(i+1), postconditions.getSensor(j+1));
                
                if (rule.detect_level() == this.detect_level() - 1)
                    res.addRule(rule);
            }
        }
        
        return res;
        
    }
    
    
    
    public void print () {
        
        System.out.println(this.precondition.toString() + this.postcondition);
    }
    
    
    
    // Returns true if the Rules have a common non-wildcarded index in either Prec or Post
    public boolean conflicts (Rule rule2) {
        
        ArrayList index_of_prec_1 = this.getPrecondition().get_nonWildcarded_indexes();
        ArrayList index_of_prec_2 = rule2.getPrecondition().get_nonWildcarded_indexes();
        ArrayList index_of_post_1 = this.getPostcondition().get_nonWildcarded_indexes();
        ArrayList index_of_post_2 = rule2.getPostcondition().get_nonWildcarded_indexes();
      
        
        for (int i = 0; i < index_of_prec_1.size(); i++) {
            
            int test = index_of_prec_2.indexOf(index_of_prec_1.get(i));
            
            if (test != -1)
                return true;
        }
        
        for (int j = 0; j < index_of_post_1.size(); j++) {
            
            int test = index_of_post_2.indexOf(index_of_post_1.get(j));
            
            if (test != -1)
                return true;
        }
        
        
        return false;
        
    }
    
    
    // Returns the index of last element
    public int get_last_element () {
        

        
        if (this.getPostcondition().has_effect()) {
            
            int res = this.getPrecondition().size();
            
            for (int i = this.getPostcondition().size() - 1; i >= 0 ; i--) {
                
                if (this.getPostcondition().getToken(i).isNotWildcard())
                    return (res + i);
            }
        }
        
        for (int i = this.getPrecondition().size() - 1; i >= 0 ; i--) {
                
                if (this.getPrecondition().getToken(i).isNotWildcard())
                    return i;
            }
        
        
        return -1;
        
    }
    
    
    // True if Rules match except the last element
    // Used in ASDD for subsets
    public boolean match_until_last_element (Rule rule2) {
        
        int this_elements = this.getPrecondition().numberOfNonWildcards() + this.getPostcondition().numberOfNonWildcards();
        int rule2_elements = rule2.getPrecondition().numberOfNonWildcards() + rule2.getPostcondition().numberOfNonWildcards();
        
        
        int count = 0;
        int i = 0;
        
        if (this_elements == 0 || rule2_elements == 0)
            return false;
        
        if (this_elements != rule2_elements)
            return false;
        
        while (count < rule2_elements - 1) {
            
            if (!rule2.getToken(i).match_exact(this.getToken(i)))
                return false;
            
            if (rule2.getToken(i).isNotWildcard())
                count++;
            
            i++;
        }

        return true;
    }

  
    
    
    public Token getToken (int position) {
        
        if (position >= this.getPrecondition().size()) {
            
            int count = position - this.getPrecondition().size();
            
            return this.getPostcondition().getToken(count);
            
            
        }
        
        else {
            
            return this.getPrecondition().getToken(position);
        }
    }
    
}
