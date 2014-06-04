/*
 * RuleSet.java
 *
 * Created on 12 August 2003, 13:35
 */

//A rule set is a NodeList of rules with the same conditions and conclusion variable
//Rules with the same conclusion varaible will have a precedence against other rules
///for situations in which they compete

package EnvAgent.RuleLearner;

import java.util.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class RuleSet extends NodeList {

    protected RuleSetMap precedenceOver;
    protected RuleSetMap deferTo;
    
    /** Creates new RuleSet */
    public RuleSet() {
        precedenceOver = new RuleSetMap();
        deferTo =  new RuleSetMap();
    }
    
    public RuleSet generateRuleSet(RuleNode currentNode, NodeList nodes, boolean forceRecount) {
        
        //This will be done by a function which goes through the whole database at once
        //called generateRuleSets in RuleLearner
        
        if (!forceRecount)
            if (currentNode.getRuleSet() != null)
                return currentNode.getRuleSet();
        
        RuleSet ruleSet = new RuleSet();
        
       
        //if currentNode already has a ruleset then return that
       
        //first search all existing rule sets to see if one exists for the
        //set of preconditions and postcondition variable in currentNode
        
        //if a rule set exists return that
        
        //otherwise find all rules with these precondition and postcondition variable
        //add each found to the node list. When complete set their rule sets to be the
        //generated one
        
         //put(Object key, Object value)
        
        return ruleSet;
    }
    
    public int getOutputVariblePos() {
        if (size() != 0)
            return get(0).getSuccessor().getFirstNonWildcardPosition();
        else
            return -1;
    }
    
    //All rules in the rule set have the same precursor after all
    public RuleElements getPrecursor() {
        if (size() == 0)
            return null;
        else
            return get(0).getPrecursor();
    }
    
    public boolean precedenceSet(RuleSet set2) {
        if (precedenceOver.contains(set2))
            return true;
        if (deferTo.contains(set2))
            return true;
        
        return false;
    }
    
    public boolean hasPrecedenceOver(RuleSet set2) {
        return precedenceOver.contains(set2);
    }
    
    public boolean defersTo(RuleSet set2) {
        return deferTo.contains(set2);
    }
    
    public void setPrecedenceOver(RuleSet set2) {
        if (!precedenceSet(set2)) {
            precedenceOver.add(set2);
        }
    }
    
    public void setDefersTo(RuleSet set2) {
         if (!precedenceSet(set2)) {
            deferTo.add(set2);
        }
    }
    
    public RuleSetMap getDefersToList() {
        return deferTo;
    }
    
    public RuleSetMap getPrecedenceOverList() {
        return precedenceOver;
    }
}
