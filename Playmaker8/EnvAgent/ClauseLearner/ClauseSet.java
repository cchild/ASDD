/*
 * RuleSet.java
 *
 * Created on 12 August 2003, 13:35
 */

//A rule set is a NodeList of rules with the same conditions and conclusion variable
//Rules with the same conclusion varaible will have a precedence against other rules
///for situations in which they compete

package EnvAgent.ClauseLearner;


import Logging.*;
import java.text.*;



/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClauseSet extends ClauseList/* implements Serializable*/ {

    protected ClauseSetRefs precedenceOver;
    protected ClauseSetRefs deferTo;
   
    
    int ref;
    boolean usedWithSupremacy;



    //Optimistic:  need to initialise all operators with optimistic estimates
  

    //Envt. operators: needs a reference to the Envt operator for each P-SPO (Null = not envt). Can this be done using the same references we use for supremacy etc.
    protected ClauseSetRefs environmentSet;



    //Updates: need to explore using the optimistic updates for a while and then use the random exploration based on the operator probability? Or based on pure randomness?
    //Future rewards need to have reasonable discount levels or they will not give low variance to operators with immediate rewards.
    //What to do about overlapping sets where (A^B is good, B^C is bad, B is good)?
   
    static protected ClauseSetMap globalClauseSetMap = null;
    static protected int globalRefCounter = 0;
    
    /** Creates new RuleSet */
    public ClauseSet() {
        precedenceOver = new ClauseSetRefs();
        deferTo =  new ClauseSetRefs();
        environmentSet =  new ClauseSetRefs();
        ref = globalRefCounter;
        globalRefCounter ++;
        usedWithSupremacy = false;
    }
    
    public int getRef() {
        return ref;
    }
    
    public double getError() {
        return get(0).getError();
    }
    
    public void setError(double newError) {
        get(0).setError(newError);
    }

    public boolean getUsedWithSpremacy() {
        return usedWithSupremacy;
    }

    public void setUsedWithSpremacy(boolean newUsed) {
       usedWithSupremacy = newUsed;
    }



    public double getOptimisticValue() {
        return get(0).getOptimisticValue();
    }

    public void setOptimisticValue(double newVal) {
        get(0).setOptimisticValue(newVal);
    }
    
    /* Call standard write object on the nodeList.
     * Must ensure that the nodes also have customizable write and read functions
     */
    /*public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.   
        s.defaultWriteObject();   
    }*/
  
    /* Call standard read object on the nodeList.
     * Must ensure that the nodes also have readObject functions
     */
   /* public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
         
        if (ref > globalRefCounter)
            globalRefCounter = ref;
    }*/
    
    public void setGlobalClauseSetMap(ClauseSetMap gcsm) {
        this.globalClauseSetMap = gcsm;
    }
    
    public ClauseSet generateRuleSet(ClauseNode currentNode, ClauseList nodes, boolean forceRecount) {
        
        //This will be done by a function which goes through the whole database at once
        //called generateRuleSets in RuleLearner
        
        if (!forceRecount)
            if (currentNode.getClauseSet() != null)
                return currentNode.getClauseSet();
        
        ClauseSet clauseSet = new ClauseSet();
        
       
        //if currentNode already has a ruleset then return that
       
        //first search all existing rule sets to see if one exists for the
        //set of preconditions and postcondition variable in currentNode
        
        //if a rule set exists return that
        
        //otherwise find all rules with these precondition and postcondition variable
        //add each found to the node list. When complete set their rule sets to be the
        //generated one
        
         //put(Object key, Object value)
        
        return clauseSet;
    }
    
    /*public String getOutputVaribleName() {
        //System.out.print("\nThis may depend on a variable binding for the body\n");
        Clause head = null;
        if (size() != 0) {
            head = (Clause)get(0).getClauseHead();
        }
        
        if (head != null) {
            //Currently returns say Direction 1, or Direction X
            return head.getPredicate(0).toString();
        }
        else
            return "NULL";
    }*/
    
    //All rules in the rule set have the same precursor after all
    //HMMM.... They don't any more
    public ClauseElements getBody() {
        if (size() == 0)
            return null;
        else
            return get(0).getClauseElements();
    }
    
     public Clause getClauseHead() {
        if (size() == 0)
            return null;
        else
            return (Clause)get(0).getClauseHead();
    }
    
    public boolean precedenceSet(ClauseSet set2) {
        if (precedenceOver.contains(set2.getRef()))
            return true;
        if (deferTo.contains(set2.getRef()))
            return true;
        
        return false;
    }
    
    public boolean hasPrecedenceOver(ClauseSet set2) {
        return precedenceOver.contains(set2.getRef());
    }
    
    public boolean defersTo(ClauseSet set2) {
        return deferTo.contains(set2.getRef());
    }
    
    public void setPrecedenceOver(ClauseSet set2) {
        if (!precedenceSet(set2)) {
            precedenceOver.addRef(set2.getRef());
        }
    }
    
    public void setDefersTo(ClauseSet set2) {
         if (!precedenceSet(set2)) {
            deferTo.addRef(set2.getRef());
        }
    }
    
    public ClauseSetRefs getDefersToList() {
        return deferTo;
    }
    
    public ClauseSetRefs getPrecedenceOverList() {
        return precedenceOver;
    }
    
    public double getTotalProb() {
        double totalProb = 0.0f;
        for (int i = 0; i < size(); i++) {
            totalProb += ((ClauseNode)get(i)).getProbability();
        }
        return totalProb;
    }
    
    public ValueWithUpdate getUtility() {

        return get(0).getUtility();
    }

    public ValueWithUpdate getBias() {

        return get(0).getBias();
    }

    public ValueWithUpdate getVariance() {

        return get(0).getVariance();
    }

    public ValueWithUpdate getTotalVariance() {

        return get(0).getTotalVariance();
    }

    public ValueWithUpdate getAggregationBias() {

        return get(0).getAggregationBias();
    }

    public ValueWithUpdate getLambda() {

         return get(0).getLambda();
    }
    
    public void restoreValueWithUpdateDefaults() {
        get(0).restoreValueWithUpdateDefaults();
    }
  


    public String toString() {
        DecimalFormat df = new DecimalFormat("#.#####");

        String theString = super.toString() + "\n Lrnd Val: " +  df.format(getUtility().getValue()) + " Err: " +df.format(getError()) + "TotVar: " + df.format(getTotalVariance().getValue()) +  "Var: " + df.format(getVariance().getValue()) + "Bia: " + df.format(getBias().getValue());
        if (isFrameRule())
            theString += " (FR) ";
        else
            theString += " (NF) ";
            
        theString += "\n";
        return theString;
    }
    
    

    public boolean isFrameRule() {

       return (get(0).getFrameRule());
    }

     public boolean bodyContainsActionAndConditions() {

        //If the rule contains an action and no conditins then it does
         //not tell us anything other than the general weight of the action

         //If the rule does not contain any actions then it does not
         //tell us anything about how good this action is, only the
         //conditions
        if (getBody().size() <= 1) {
            //if the 1 s an action then there are no conditions
            //if it's a conditoin then there are no actions
            ClauseElements bdyEls = getBody();
            bdyEls = bdyEls;
            return false;
        }

        if (!getBody().containsAnAction()) {
            //this is only conditions, no action
            //boolean whywhy = getBody().containsAnAction();
            ClauseElements bdyEls = getBody();
            bdyEls = bdyEls;
            return false;
        }

        //rule must have conditions and an action
        return true;
    }

    public boolean containsEnvironmentOperatorSet()
    {
        if (environmentSet.size() != 0)
            return true;
        return false;
    }

    public void makeEnvirnonmentOperatorSet()
    {
        environmentSet.addRef(getRef());
    }

    public int getEnvirnonmentOperatorRef()
    {
       if (containsEnvironmentOperatorSet())
            return environmentSet.get(0);
       else
           return -1;
    }

    public void setEnvirnonmentOperator(int ref)
    {
        environmentSet.addRef(ref);
    }

    public void addEnvirnonmentOperator(int ref)
    {
        environmentSet.addRef(ref);
    }

    public void orderByOutputID() {
        boolean sorted = false;
        if (size() > 1) {
           while(!sorted) {
               sorted = true;
               for (int i = 0; i < size()-1; i ++) {
                   int j = i+1;
                   if (get(i).getClauseHead().getUniqueID() > get(j).getClauseHead().getUniqueID()) {
                       ClauseNode temp = get(i);
                       set(i, get(j));
                       set(j, temp);
                       sorted = false;
                   }
               }
            }
        }
    }

}
