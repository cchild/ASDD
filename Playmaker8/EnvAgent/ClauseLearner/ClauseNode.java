package EnvAgent.ClauseLearner;

import java.util.*;
import java.io.*;
import java.text.*;



//a single node describes a rule, or dependency relationship in the data
public class ClauseNode extends Object implements Cloneable, Serializable, Comparator {
    private ClauseElements clauseElements;
    private boolean databaseOccurrencesCounted;
    private int databaseOccurrences;
    private boolean precursorMatchEvaluated;
    private int precursorequals;
   
 
    //The parents of a node are the nodes which preceed it in the generation tree
    private ClauseNode father;
    private ClauseNode mother;
    private HashSet matchingExamples;

     private boolean frameRule;

    //Moved this from Caluse sets
    //This was the old error measure.
    protected double error;

    protected double optimisticValue;


    //The main utility estimate for the rule
    protected ValueWithUpdate utility;

    //Bias, Variance, Aggregation bias: each of these needs a number, a number of times used and a current alpha.
    protected ValueWithUpdate bias;
    protected ValueWithUpdate variance;
    protected ValueWithUpdate totalVariance;
    protected ValueWithUpdate aggregationBias;

    protected ValueWithUpdate lambda;
    
    //A rule set is a NodeList of rules with the same conditions and conclusion variable
    //Rules with the same conclusion varaible will have a precedence against other rules
    ///for situations in which they compete
    ClauseSet clauseSet;
    
    ClauseNode(ClauseElements clauseElements) {
        this.clauseElements = (ClauseElements)clauseElements.clone();
        databaseOccurrencesCounted = false;
        precursorMatchEvaluated = false;
        precursorequals = 0;
        databaseOccurrences = 0;
        father = null;
        mother = null;
        clauseSet = null;
        matchingExamples = null;
        frameRule = false;
        utility = new ValueWithUpdate();
        bias = new ValueWithUpdate();
        variance = new ValueWithUpdate();
        totalVariance = new ValueWithUpdate();
        aggregationBias = new ValueWithUpdate();
        lambda = new ValueWithUpdate();
        restoreValueWithUpdateDefaults();
    }

    public final void restoreValueWithUpdateDefaults() {
       
        bias.resetDefaults();
        bias.setInitialAlpha(0.2f);
        bias.setFinalAlpha(0.2f);
        bias.setMcClainUpdate(false);
        bias.setValue(0);
        variance.resetDefaults();
        variance.setInitialAlpha(0.2f);
        variance.setFinalAlpha(0.2f);
        variance.setMcClainUpdate(false);
        variance.setValue(0);
        totalVariance.resetDefaults();
        totalVariance.setInitialAlpha(0.2f);
        totalVariance.setFinalAlpha(0.2f);
        totalVariance.setMcClainUpdate(false);
        totalVariance.setValue(0);
        aggregationBias.resetDefaults();
        aggregationBias.setInitialAlpha(0.2f);
        aggregationBias.setFinalAlpha(0.2f);
        aggregationBias.setMcClainUpdate(false);
        aggregationBias.setValue(10);
        lambda.resetDefaults();
        error = 0;
        optimisticValue = 0; //should be maxreward/(1-alpha) = 1/(1-0.9) = 10, but we're not using it
        utility.resetDefaults();
        utility.setFinalAlpha(0.1f);
        utility.setValue(optimisticValue);
    }

    public double getError() {
        return error;
    }

    public void setError(double newError) {
        error = newError;
    }

      public double getOptimisticValue() {
        return optimisticValue;
    }

    public void setOptimisticValue(double newOptimisticValue) {
        optimisticValue = newOptimisticValue;
    }

    void setMatchingExamples(HashSet matching) {matchingExamples = matching;}
    HashSet getMatchingExamples() 
    {
        if (matchingExamples != null)
            return matchingExamples;
        else {
            if (father == null) {
                matchingExamples = new HashSet();
                return matchingExamples;
            } else {
                HashSet intersection = (HashSet)father.getMatchingExamples().clone();
                intersection.retainAll(mother.getMatchingExamples());

                //this is faster but seems to take up too much memory in practice
                //matchingExamples = intersection;
                return intersection;
            }
        }
    }

    
    public Object clone(){
        try {
            ClauseNode n = (ClauseNode)super.clone();	// clone the percep
            n.clauseElements = (ClauseElements)clauseElements.clone();
            n.utility = new ValueWithUpdate();
            n.bias = new ValueWithUpdate();
            n.variance =new ValueWithUpdate();
            n.totalVariance = new ValueWithUpdate();
            n.aggregationBias = new ValueWithUpdate();
            n.lambda = new ValueWithUpdate();
            n.error = 0;
            n.optimisticValue = optimisticValue;
            restoreValueWithUpdateDefaults();
            n.databaseOccurrencesCounted = false;
            n.databaseOccurrences = 0;
            n.precursorMatchEvaluated = false;
            n.precursorequals = 0;
            n.father = null;
            n.mother = null;
            n.clauseSet = null;
            n.matchingExamples = null;
            n.frameRule = frameRule;
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
        
        //We do not serialize the clause set. Leads to stack overflow.
        //ClauseSet tempClauseSet = clauseSet;

        ClauseNode tempFather = father;
        ClauseNode tempMother = mother;


        //clauseSet = null;
        
        s.defaultWriteObject();
        

        //s.writeObject(clauseElements);
        //s.writeBoolean(databaseOccurrencesCounted);
        //s.writeBoolean(precursorMatchEvaluated);
        //s.writeInt(precursorequals);
        //s.writeInt(databaseOccurrences);
        ////s.writeObject(father);
        ////s.writeObject(mother);
        //s.writeObject(clauseSet);
        //s.writeDouble(value);
        
        //clauseSet = tempClauseSet;
        //father = tempFather;
        //mother = tempMother
    }
  
    /* Call standard read object on the nodeList.
     * Must ensure that the nodes also have readObject functions
     */
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        
        //clauseElements = (ClauseElements)s.readObject();
        //databaseOccurrencesCounted = s.readBoolean();
        //precursorMatchEvaluated = s.readBoolean();
        //precursorequals = s.readInt();
        //databaseOccurrences = s.readInt();
        ////parents = (ClauseList)s.readObject();
        //clauseSet = (ClauseSet)s.readObject();
        //value = s.readDouble();
    }
    
    public boolean isEqualTo(ClauseNode n) {
        if (n.getClauseElements().isEqualTo(clauseElements)) {
            return true;
        }
        return false;
    }


   /* public void copy(ClauseNode n) {
        clauseElements.copy(n.clauseElements);
        databaseOccurrencesCounted = false;
        databaseOccurrences = 0;
        precursorMatchEvaluated = false;
        precursorequals = 0;
        father = null;
        mother = null;
        ruleSet = null;
    }*/

    public ClauseSet getClauseSet() {
        return clauseSet;
    }
    
    public boolean defersTo(ClauseNode otherClause) {
        return getClauseSet().defersTo(otherClause.getClauseSet());
    }
    
    public boolean hasPrecedenceOver(ClauseNode otherRule) {
        return getClauseSet().hasPrecedenceOver(otherRule.getClauseSet());
    }
    
    public ClauseElements getClauseElements() {
        return clauseElements;
    }
   
    
    public Term getClauseHead() {
        return getClauseElements().getHead();
    }
    
    public int clauseSize() {
        return getClauseElements().size();
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        return clauseElements.toString() +  " Prob: " + df.format(getProbability());
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


    /*public boolean hasVariableWildcardInPrecursorOnly() {
        for (int i = 1; i < precursor.size(); i++) {
            //check that the successor has a non-wildcard element here
            if (!((Term)successor.get(i)).isWildcard()) {
                //the precursor does not have a non-wildcard element here so it's no use
                if (((Term)precursor.get(i)).isWildcard()) {
                    return true;
                }
            }
        }
        return false;
    }*/


    //REPLACE THIS WITH A GENERALITY MEASURE
    /*count the number of wildcards so we know how general the rules are*/
    //public int countWildcards() {
    //    return (precursor.countWildcards() + successor.countWildcards());
    //}   
    
    public void setFather(ClauseNode parent) {
        father = parent;
   }
    
    public void setMother(ClauseNode parent) {
        mother = parent;
    }
    
    public ClauseNode getFather() {
        return father;
    }

    public ClauseNode getMother() {
        return mother;
    }

    public void setClauseSet(ClauseSet clauseSet) {
        this.clauseSet = clauseSet;
    }
    
    public int getBodySize() {
        return clauseElements.getBodySize();
    }
    
    public boolean bodyequals(ClauseElements currentState) {
        return getClauseElements().bodyequals(currentState);
    }
    
    public boolean subsumes(ClauseNode other) {
        return getClauseElements().subsumes(other.getClauseElements());
    }
    
    public Term possibleHeadForBodyMatch(ClauseElements currentState) {
        return getClauseElements().possibleHeadForBodyMatch(currentState);
    }
    


     public boolean getFrameRule() {
       return frameRule;
    }

    public void setFrameRule(boolean frameRule) {
        this.frameRule = frameRule;
    }
    
    public int compare(Object a, Object b)
    {
        if (((ClauseNode)a).getBodySize() > ((ClauseNode)b).getBodySize())
            return 1;
        if (((ClauseNode)a).getBodySize() < ((ClauseNode)b).getBodySize())
            return -1;
        return 0;
    }
    
    @Override
    public boolean equals(Object a)
    {
        if (((ClauseNode)a).getBodySize() == getBodySize())
            return true;
        return false;
    }

     public boolean isActionUnchangedOutput() {

        //Frame rules tell us that output is not changed. This means output must be 1.0 prob
        //so there can't be more than one rule
        if (getProbability() != 1)
            return false;

        if (getBodySize() != 2) {
            //frame rule is telling us that this action has no effect
            //on this variable. If there are more conditions then it
            //means the action only has an effect under very specific
            //conditions
            return false;
        }

        //actually we should test that this contains exactly
        //one condition and one action and that the condition
        //refers to the same variable as the output - but the no action
        //part will be caught later anyway

        if (getClauseElements().containsBodyEquivalent((Clause)getClauseHead()))
            return true;
        else
            return false;
    }

    public ValueWithUpdate getUtility() {

        return utility;
    }

    public ValueWithUpdate getBias() {

        return bias;
    }

    public ValueWithUpdate getVariance() {

        return variance;
    }

      public ValueWithUpdate getTotalVariance() {

        return totalVariance;
    }

    public ValueWithUpdate getAggregationBias() {

        return aggregationBias;
    }

    public ValueWithUpdate getLambda() {

        return lambda;
    }
}
