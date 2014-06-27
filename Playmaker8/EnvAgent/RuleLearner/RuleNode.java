package EnvAgent.RuleLearner;

import EnvModel.*;

import java.util.*;
import java.io.*;

//a single node describes a rule, or dependency relationship in the data
public class RuleNode extends Object implements Cloneable, Serializable  {
    RuleElements precursor;
    RuleElements successor;
    boolean databaseOccurrencesCounted;
    int databaseOccurrences;
    boolean precursorMatchEvaluated;
    int precursorequals;
 
    //The parents of a node are the nodes which preceed it in the generation tree
    NodeList parents;
    
    //A rule set is a NodeList of rules with the same conditions and conclusion variable
    //Rules with the same conclusion varaible will have a precedence against other rules
    ///for situations in which they compete
    RuleSet ruleSet;
    
    RuleNode(RuleElements precursor, RuleElements successor) {
        this.precursor = (RuleElements)precursor.clone();
        this.successor = (RuleElements)successor.clone();
        databaseOccurrencesCounted = false;
        precursorMatchEvaluated = false;
        precursorequals = 0;
        databaseOccurrences = 0;
        parents = null;
        ruleSet = null;
    }
    
    public Object clone(){
        try {
            RuleNode n = (RuleNode)super.clone();	// clone the percep
            n.precursor = (RuleElements)precursor.clone();
            n.successor = (RuleElements)successor.clone();
            n.databaseOccurrencesCounted = false;
            n.databaseOccurrences = 0;
            n.precursorMatchEvaluated = false;
            n.precursorequals = 0;
            n.parents = null;
            n.ruleSet = null;
            return n;				// return the clone
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
            throw new InternalError();
        }
    }

    /* Call standard write object on the nodeList.
     * Must ensure that the nodes also have customizable write and read functions
     */
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(precursor);
        s.writeObject(successor);
        s.writeBoolean(databaseOccurrencesCounted);
        s.writeBoolean(precursorMatchEvaluated);
        s.writeInt(precursorequals);
        s.writeInt(databaseOccurrences);
        s.writeObject(parents);
        s.writeObject(ruleSet);
    }
  
    /* Call standard read object on the nodeList.
     * Must ensure that the nodes also have readObject functions
     */
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        
        precursor = (RuleElements)s.readObject();
        successor = (RuleElements)s.readObject();
        databaseOccurrencesCounted = s.readBoolean();
        precursorMatchEvaluated = s.readBoolean();
        precursorequals = s.readInt();
        databaseOccurrences = s.readInt();
        parents = (NodeList)s.readObject();
        ruleSet = (RuleSet)s.readObject();
    }
    
    public boolean isEqualTo(RuleNode n) {
        if (n.getPrecursor().isEqualTo(precursor)) {
            if (n.getSuccessor().isEqualTo(successor)) {
                return true;
            }
        }
        return false;
    }


    public void copy(RuleNode n) {
        precursor.copy(n.precursor);
        successor.copy(n.successor);
        databaseOccurrencesCounted = false;
        databaseOccurrences = 0;
        precursorMatchEvaluated = false;
        precursorequals = 0;
        parents = null;
        ruleSet = null;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }
    
    public boolean defersTo(RuleNode otherRule) {
        return getRuleSet().defersTo(otherRule.getRuleSet());
    }
    
    public boolean hasPrecedenceOver(RuleNode otherRule) {
        return getRuleSet().hasPrecedenceOver(otherRule.getRuleSet());
    }
    
    public RuleElements getPrecursor() {
        return precursor;
    }

    public RuleElements getSuccessor() {
        return successor;
    }

    public String toString() {
        return precursor.toString() + ">" + successor.toString() + " Prob: " + getProbability();
    }
    
    public String translation() {
        return precursor.toString() + successor.toString() + " Prob: " + getProbability();
    }    

    public boolean getHeuristicEvaluated() {
        return getDatabaseOccurrencesCounted();
    }

    public boolean getDatabaseOccurrencesCounted() {
        return databaseOccurrencesCounted;
    }

    public boolean getPrecursorMatchEvaluated() {
        return precursorMatchEvaluated;
    }

    public void setPrecursorMatchEvaluated(boolean newVal) {
        precursorMatchEvaluated = newVal;
    }

    public void setDatabaseOccurrencesCounted(boolean newVal) {
        databaseOccurrencesCounted = newVal;
    }

    public void setDatabaseOccurrences(int newVal) {
        databaseOccurrences = newVal;
        setDatabaseOccurrencesCounted(true);
    }
    
    public void incrementOccurrences() {
        databaseOccurrences ++;
    }

    public int getDatabaseOccurrences() {
        return databaseOccurrences;
    }

    public int getHeuristicValue() {
        return getDatabaseOccurrences();
    }

    public float getProbability() {
        return ((float)getDatabaseOccurrences())/((float)getPrecursorequals());
    }

    public void setPrecursorequals(int newVal) {
        precursorequals = newVal;
        setPrecursorMatchEvaluated(true);
    }

    public int getPrecursorequals() {
        return precursorequals;
    }


    public boolean hasFluentWildcardInPrecursorOnly() {
        for (int i = 1; i < precursor.size(); i++) {
            //check that the successor has a non-wildcard element here
            if (!((RuleObject)successor.get(i)).isWildcard()) {
                //the precursor does not have a non-wildcard element here so it's no use
                if (((RuleObject)precursor.get(i)).isWildcard()) {
                    return true;
                }
            }
        }
        return false;
    }


    /*count the number of wildcards so we know how general the rules are*/
    public int countWildcards() {
        return (precursor.countWildcards() + successor.countWildcards());
    }   
    
    public void addParent(RuleNode parent) {
        if (parents == null) {
            parents = new NodeList();
        }
        
        parents.add(parent);
    }
    
    public void setParent(RuleNode parent) {
        if (parents == null) {
            parents = new NodeList();
            parents.add(parent);
        }
    }
    
    public RuleNode getParent() {
        return parents.get(0);
    }
    
    public RuleNode getParent(int which) {
        if (parentsSize() < which)
            return parents.get(which);
        else
            return null;
    }
    
    public int parentsSize() {
        return parents.size();
    }
    
    public NodeList getParents() {
        return parents;
    }
    
    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }
}
