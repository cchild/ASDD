/*
 * NodeList.java
 *
 * Created on 20 November 2002, 18:34
 */

package EnvAgent.ClauseLearner;

import EnvModel.*;

import java.util.*;
import java.io.*;
import Logging.*;
//import com.samskivert.util.*;

//import EnvModel.PredatorModel.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClauseList implements Serializable {

    private ArrayList clauseList;
    ClauseSetMap clauseSetMap;
    
    public static boolean USE_WEIGHTED_RULE_VALUES = false;
    public static boolean USE_AVG_RULE_VALUES = false;
    public static boolean USE_MOST_SPECIFIC_RULE_VALUES = false;
    public static boolean USE_VARIANCE_RULE_WEIGHTS = true;
    public static boolean USE_NONFRAME_RULES_ONLY = false;
    public static boolean USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY = false;
    public static boolean USE_WEIGHTING_OF_ONE = false;
    
    private boolean listSorted;
    /** Creates new NodeList */
    public ClauseList() {
        clauseList = new ArrayList();
        listSorted = false;
        clauseSetMap = null;
    }
    
    public ArrayList getArrayList() {
        listSorted = false;
        return clauseList;
    }
    
    public ClauseNode get(int which) {
        return (ClauseNode)clauseList.get(which);
    }
    
    public void set(int which, ClauseNode val) {
        listSorted = false;
        clauseList.set(which, val);
    }
    
    public int size() {
        return clauseList.size();
    }
    
    public void remove(ClauseNode node) {
        clauseList.remove(node);
    }
    
    public void remove(int which) {
        clauseList.remove(which);
    }
    
    public void add(ClauseNode node) {
        listSorted = false;
        clauseList.add(node);
    }
    
    public boolean contains(ClauseElements clause) {
        //System.out.print("WARNING:should use a map structure for contains");
        for (int i = 0; i < clauseList.size(); i++) {
            ClauseNode currentNode = (ClauseNode)clauseList.get(i);
            if(currentNode.getClauseElements().isEqualTo(clause))
                return true;
        }
        return false;
    }
    
    public void addAt(int pos, ClauseNode node) {
        listSorted = false;
        clauseList.add(pos, node);
    }
    
    public void addAll(ClauseList otherList) {
        listSorted = false;
        clauseList.addAll(otherList.getArrayList());
    }
    
    public void nullClauseSetMap() {
        clauseSetMap = null;
        for (int nodeLoop = 0; nodeLoop < size(); nodeLoop ++) {
            get(nodeLoop).setClauseSet(null);
        }
        
    }
    
    public void sortNonIncreasingGenerality() {
        if (!listSorted) {
            System.out.print("\nWARNING: using number of clause elements for generality in CluaseList");
            if (this.size() > 0)
             //   QuickSort.sort(clauseList, get(0));

            for (int i =0; i < clauseList.size() -1; i++) {
                for (int j = 0; j < clauseList.size() -1; j++) {
                    if (get(j).clauseSize() > get(j+1).clauseSize()) {
                        ClauseNode temp = get(j);
                        clauseList.set(j, get(j+1));
                        clauseList.set(j+1, temp);
                    }
                }
            }

            listSorted = true;
        }
    }

     public void sortIncreasingLastElement() {
        if (size() > 0){
            for (int i =0; i < size() -1; i++) {
                get(i).getClauseElements().orderByUniqueID(null);
                for (int j = 0; j < size() -1; j++) {
                    get(j).getClauseElements().orderByUniqueID(null);
                    if (get(j).getClauseElements().getLastElementID() > get(j+1).getClauseElements().getLastElementID()) {
                        ClauseNode temp = get(j);
                        set(j, get(j+1));
                        set(j+1, temp);
                    }
                }
            }
        }
    }
    
    /*Call standard write object on the clauseList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        ClauseSetMap tempStore = clauseSetMap;
        clauseSetMap = null;
        s.defaultWriteObject();
        
        s.writeBoolean(listSorted);
        s.writeInt(clauseList.size());
        for (int i = 0; i < clauseList.size(); i++) {
            s.writeObject(clauseList.get(i));
        }
        clauseSetMap = tempStore;
    }
  
    /*Call standard read object on the clauseList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        
        listSorted = s.readBoolean();
        int newSize = s.readInt();
        for (int i =0; i < newSize; i++) {
            add((ClauseNode)s.readObject());
        }
        clauseSetMap = null;
    }
    
    public ClauseSetMap buildClauseSetMap() {
       
        //We could keep this list permanently so we don't have
        //to keep re-buildig it
        if (clauseSetMap != null) {
            return clauseSetMap;
        }
        
        clauseSetMap = new ClauseSetMap();
        
        for (int i = 0; i < size(); i++) {    
            //only add ground directions, not variables as direction is the varibale we're after
            boolean matchFound = false;
            for (int j = 0; j < clauseSetMap.size(); j++) {
                ClauseSet clauseSet = clauseSetMap.get(j);
                if (clauseSet == get(i).getClauseSet()) {
                    matchFound = true;
                    j = clauseSetMap.size();
                }
            }
            if (!matchFound) {
                clauseSetMap.add(get(i).getClauseSet());
            }
        }
        
        return clauseSetMap;
    }
    
    public ClauseSetMap getClauseSetMap() {
        return clauseSetMap;
    }
    
    /*This is tricky because we need to expicitly define what an output
     *varibale is. For the moment we'll hard code it*/
    public ArrayList rulesWithSameOutputVariable() {
        
        //System.out.print("\n WARNING: HARD-CODED OUTPUT VARIABLES in ClauseList, rulesWithSameOutputVariable");
        
        ArrayList outLists = new ArrayList();
        
        //This is currently hard-wired for our example domain
        //First: pick up each See(Direction, Object) - where See(Direction, X) is the varibale, with X being the value
        
        //ClauseList alreadyAdded = new ClauseList();
        for (int i = 0; i < size(); i++) {
            Clause head = (Clause)get(i).getClauseHead();
            if (head.isGround()) {
                //only add ground directions, not variables as direction is the varibale we're after
                boolean matchFound = false;
                for (int j = 0; j < outLists.size(); j++) {
                    ClauseList outList = (ClauseList)outLists.get(j);
                    if (((Clause)outList.get(0).getClauseElements().getHead()).sameOutVariable(head)) {
                        matchFound = true;
                        outList.add(get(i));
                        j = outLists.size();
                    }
                }
                if (!matchFound) {
                    ClauseList cList = new ClauseList();
                    cList.add(get(i));
                    outLists.add(cList);
                    //alreadyAdded.add(get(i));
                }
            }
        }
        
        /*
        //Go through each rule adding the successor to which it refers to the list
        for (int i = 0; i < size(); i++) {            
            boolean present = false;
            for (int j = 0; j < alreadyAdded.size(); j++) {
                if (get(i).equals(alreadyAdded.get(j)))
                    present = true;
            }
            
            if (!present) {
                Clause head = (Clause)get(i).getClauseHead();
                for (int j = 0; j < outLists.size(); j++) {
                    if (head.equals(((ClauseList)outLists.get(j)).get(0).getClauseHead())) { 
                        System.out.print("equals should take into account possible equals in rule body");
                        //not we can add to more than one outlist as the head can match more than one
                        ((ClauseList)outLists.get(j)).add(get(i));
                    }
                }
            }
        }
         */
        
        return outLists;
    }
    
    public ClauseList filterByPrecedence() {
        ArrayList sameOutputVariableRules = rulesWithSameOutputVariable();
        
        ClauseList finalRuleSet = new ClauseList();
        
        for (int outVariableLoop = 0; outVariableLoop < sameOutputVariableRules.size(); outVariableLoop ++) {
            ClauseList rulesWithSameOutput = (ClauseList)sameOutputVariableRules.get(outVariableLoop);
            for (int sameOutputLoop = rulesWithSameOutput.size() -1; sameOutputLoop >=0; sameOutputLoop --) {
                ClauseNode removableRule = rulesWithSameOutput.get(sameOutputLoop);                
                for (int checkAgainstLoop = rulesWithSameOutput.size() -1; checkAgainstLoop >=0; checkAgainstLoop --) {   
                    ClauseNode checkAgainstRule = rulesWithSameOutput.get(checkAgainstLoop);
                    if (removableRule.defersTo(checkAgainstRule)) {
                        rulesWithSameOutput.remove(sameOutputLoop);
                        checkAgainstLoop = -1;
                    } else {
                         //doesn' defer to, but add an extra check to make sure precedence has been set
                        if (removableRule != checkAgainstRule) {
                            if (!(removableRule.getClauseSet() == checkAgainstRule.getClauseSet())) {
                                if (!checkAgainstRule.defersTo(removableRule)) {
                                    //Thiese rules have never met, so keep the most general one
                                    if (removableRule.getPrecursorequals() <= checkAgainstRule.getPrecursorequals())
                                    {
                                        //System.out.print("\nRemoved never met clause:" + removableRule.toString() + " by " + checkAgainstRule.toString());
                                        rulesWithSameOutput.remove(sameOutputLoop);
                                        checkAgainstLoop = -1;
                                    }
                                }
                            }
                        }
                    }
                }                 
            }
            
            finalRuleSet.addAll(rulesWithSameOutput);
        }
        
        return finalRuleSet;
    }
    
    
    /*public void removeNodesWithPrecursorAndSuccessorPos(RuleElements precursor, int successorPos) {
        for (int i = size() -1; i >= 0; i--) {
            if (get(i).getPrecursor().isEqualTo(precursor)) {
                if (get(i).getSuccessor().getFirstNonWildcardPosition() == successorPos) {
                    LogFile logfile = new LogFile(1);
                    logfile.print("Removed because covered by rule with less Variables\n");
                    logfile.print(get(i).toString());
                    logfile.close();
                    clauseList.remove(i);
                }
            }
        }
    }*/
    
    public boolean ruleAlreadyExists(ClauseNode testNode) {
        for (int i = 0; i < size(); i++) {
            if (testNode.isEqualTo(get(i)))
                return true;
        }
        return false;
    }
    
    /*get a list of nodes whos precursor's match the current state from the complete node list*/
    public ClauseList matchingNodes(ClauseElements currentState) {
        ClauseList matchingClauses = new ClauseList();
        for (int i = 0; i < size(); i++) {
            ClauseNode toTest = get(i);
            if (get(i).bodyequals(currentState))
                matchingClauses.add(get(i));
        }
        
        return matchingClauses;
    }
    
    @Override
    public String toString() {
        String str = new String();
        for (int i = 0; i < size(); i ++) {
            str += "\n" + get(i).toString(); 
        }
        return str;
    }
    
    public void toLogFile(LogFile logfile) {
        for (int i = 0; i < size(); i ++) {
            logfile.print("\n");
            logfile.print(get(i).toString()); 
        }
    }

    public ClauseList findLevel1Clauses() {
        ClauseList level1Clauses = new ClauseList();
        
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).clauseSize() == 1)
                level1Clauses.add(this.get(i));
            else if (this.get(i).clauseSize() == 0) {
                if (this.get(i).getClauseHead() != null)
                    level1Clauses.add(this.get(i));
            }
                
        }
        
        return level1Clauses;
    }
    
    public void removeAllWithClauseSet(ClauseSet clauseSet){
        
        for (int i = size()-1; i >=0; i--) {
            if (get(i).getClauseSet() == clauseSet)
                remove(i);
        }
    }
    
    public double getWinningMatchingClausesReward(Percep percep) {
        return percep.reward();
    }
           
    public Action getBestAction(Percep percep,   Action action) {
        
        buildClauseSetMap();
        double maxActionValue = -10000;
        Action iterAction = (Action)action.clone();
        Action maxAction = (Action)action.clone();
        int equalLegalActions = 1;
        int legalActions = 0;
           
        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
        LogFile lf = null;
        
        
        if (outputLog) {
            lf = new LogFile(1);
            lf.print("Percep: " + percep.toString() + "\n");
        }
        for (int acIt = 0; acIt < iterAction.getNumValues(); acIt ++) {
            iterAction.setByValue(acIt);
            if (iterAction.legalAction()) {
                legalActions ++;
                double thisValue = getWinningMatchingClausesValue(percep, iterAction);
                if (thisValue >= maxActionValue) {
                    if (thisValue == maxActionValue)
                        equalLegalActions ++;
                    maxActionValue = thisValue;
                    maxAction.copy(iterAction);
                }
                
                //Lets output the contributors to this rule and see why its doing this
                if (outputLog) {
                    ClauseSetMap matchingClauseSets = clauseSetMap.getWinningMatching(percep, iterAction);
                    lf.print(matchingClauseSets.toString() + "\n");

                }

                if (outputLog)
                    lf.print("Action Value:" + iterAction.toString()+ " " + thisValue + "\n");
            }
        }
        
        if (outputLog) {
            lf.close();
        }
        
        if (legalActions != equalLegalActions)
            return maxAction;
        else {
            maxAction.randomAction();
            return maxAction;
        }
    }
           
    public double getWinningMatchingClausesValue(Percep percep, Action action) {
        buildClauseSetMap();
        ClauseSetMap matchingClauseSets = clauseSetMap.getWinningMatching(percep, action);
        if (USE_AVG_RULE_VALUES) {
            return matchingClauseSets.getAvgValue();
        } else {
            if (USE_WEIGHTED_RULE_VALUES) {
                return matchingClauseSets.getWeightedAvgValue();
            } else {
                if (USE_MOST_SPECIFIC_RULE_VALUES) {
                    return matchingClauseSets.getWeightedSpecificAvgValue
                            (USE_NONFRAME_RULES_ONLY,
                            USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY,
                            USE_WEIGHTING_OF_ONE);
                }  else {
                    if (USE_VARIANCE_RULE_WEIGHTS)
                    {
                        if (false)
                            return matchingClauseSets.getTotalVairenceWeightedAvgValue();
                        else
                            return matchingClauseSets.getVairenceWeightedAvgValue();
                    }  else {
                        System.out.print("Error in getWinning Matching Clause Values");
                        return 0;
                    }
                }
            }
        }
        
    }
   
    public void updateMatchingClausesValue(Percep percep, Action action, double reward, double totalMaxStateActionValue, double ALPHA, double GAMMA) {
        buildClauseSetMap();
        ClauseSetMap matchingClauseSets;
        //For now we're updating all the clause values, whether they won or now.
        if (true) {
            matchingClauseSets = clauseSetMap.getMatching(percep, action);
        }
        else
        {
            //ClauseSetMap matchingClauseSets = clauseSetMap.getWinningMatching(percep, action);
        }

        //fluberlump
        //here's a tricky one. Should we use the values of the winning rules to update these rules
        //or just the average values of the rules we're going to update?

        //ANSWER: We update each rule as if the total max state action value is what we are headed for
        //using the standard update function
         //first element of rule elements is the action
        //double ALPHA = 1.0f;
        //double GAMMA = 0.9f; //These are now passed in as variables
        double BETA = 0.2f;

        for (int i = 0; i < matchingClauseSets.size(); i++) {
             ValueWithUpdate utility = matchingClauseSets.get(i).getUtility();
             double oldVal = utility.getValue();
             double newVal = utility.updateValue(reward + GAMMA * totalMaxStateActionValue);

             if (true) { //used for USE_WEIGHTED_RULE_VALUES) {
                double oldError = matchingClauseSets.get(i).getError();
                //Notice that we are using totalMaxStateActionVlaue becuase we want the actual error
                //that the rules have compared to the avg, rather than compared to the
                //newClauseValue which is attenuated to stop big swings in value
                //NOTE!! - I've added the Gamma in here because you do need to discount the future rewards
                double newError = oldError + BETA * (Math.abs(reward + GAMMA * totalMaxStateActionValue  - oldVal) - oldError);
                matchingClauseSets.get(i).setError(newError);
             }

             ValueWithUpdate bias = matchingClauseSets.get(i).getBias();
             bias.updateValue(reward + GAMMA * totalMaxStateActionValue  - oldVal);
             ValueWithUpdate totalVariance = matchingClauseSets.get(i).getTotalVariance();
             totalVariance.updateValue((oldVal - (reward + GAMMA * totalMaxStateActionValue))*(oldVal - (reward + GAMMA * totalMaxStateActionValue)));
             ValueWithUpdate lambda =  matchingClauseSets.get(i).getLambda();
             lambda.lambdaUpdateValue(utility.getAlpha());
             ValueWithUpdate variance = matchingClauseSets.get(i).getVariance();
             variance.setValue((totalVariance.getValue()-(bias.getValue()*bias.getValue()))/(1+lambda.getValue()));
        }


        //What we used to do
        /*for (int i = 0; i < matchingClauseSets.size(); i++) {
            double oldValue = matchingClauseSets.get(i).getUtilityValue();
            double newClauseValue = oldValue + 
                                ALPHA *
                                (reward + GAMMA * totalMaxStateActionValue - oldValue);
            matchingClauseSets.get(i).setUtilityValue(newClauseValue);
            
            if (true) { //used for USE_WEIGHTED_RULE_VALUES) {
                double oldError = matchingClauseSets.get(i).getError();
                //Notice that we are using totalMaxStateActionVlaue becuase we want the actual error
                //that the rules have compared to the avg, rather than compared to the
                //newClauseValue which is attenuated to stop big swings in value
                double newError = oldError + BETA * (Math.abs(totalMaxStateActionValue + reward - oldValue) - oldError);
                matchingClauseSets.get(i).setError(newError);
            }
        }*/
         
    }
    
     //The removal of rules causes some to be removed for one successor Variable but not another
    //This means we don't get rules which add up to one.
    //Next step is to create all succesor values for each precursor and Variable
    //and check that they are either in the rule base of don't match with the database
    //Check each rule to make sure every Variable value is represented

    public void generateClauseSets() throws java.lang.OutOfMemoryError {
        //This is the bit that has to be written next. First of all we have to get over the problem
        //of how to define a varibale. It should be able to read this in from a file somehow?
     
        boolean outputLog = true;
        sortNonIncreasingGenerality();
        
        LogFile logfile = null;
        
        if (outputLog)
            logfile = new LogFile(1);
        
        int currentSpecificity = -1;
        ArrayList clauseSets = null;
        
        System.out.print("\nNodes size: " + size());
        for (int nodeLoop = 0; nodeLoop < size(); nodeLoop ++) {
            ClauseNode nodeToTest = (ClauseNode)get(nodeLoop);
            Clause testHead = (Clause)nodeToTest.getClauseHead();
            
            System.out.print("\nClause set loop: " + nodeLoop);
            System.out.flush();
            //we've just changed specificity so save time by starting a
            //new set of rule sets.
            if (nodeToTest.getBodySize() != currentSpecificity) {
                
                if (clauseSets != null) {
                    //we're starting a new rule set level so output the last one
                    for (int setCount = 0; setCount < clauseSets.size(); setCount ++) {
                        ClauseSet clauseSet = (ClauseSet)clauseSets.get(setCount);
                        //make sure the outputs of the clause sets alwyas appear in the same order
                        //useful for g-sat comparrisons later.
                        clauseSet.orderByOutputID();
                        if (outputLog) {
                            logfile.print("\nClause Set:");
                            clauseSet.toLogFile(logfile);
                        }
                    }
                }
                
                currentSpecificity = nodeToTest.getBodySize();
                clauseSets = new ArrayList();
                 
                //we also know that we'll be creating a new rule set for this rule
                ClauseSet clauseSet = new ClauseSet();
                clauseSet.add(nodeToTest);
                nodeToTest.setClauseSet(clauseSet);
                clauseSets.add(clauseSet);
            } else {
                //first check that we haven't already generated this rule set
                // if we have then just add the rule to it
                boolean alreadyPresent = false;
                for (int i = 0; i < clauseSets.size(); i ++) {
                    ClauseSet testSet = (ClauseSet)clauseSets.get(i);
                    Clause clauseHead = testSet.getClauseHead();
                    if (clauseHead.sameOutVariable(testHead)) {
                        if (testSet.getBody().bodyIsEqual(nodeToTest.getClauseElements())) {
                            alreadyPresent = true;
                            testSet.add(nodeToTest);
                            nodeToTest.setClauseSet(testSet);
                        }
                    }
                    if (alreadyPresent)
                        i = clauseSets.size();
                }
                
                if (!alreadyPresent) {
                    //we also know that we'll be creating a new rule set for this rule
                    ClauseSet clauseSet = new ClauseSet();
                    clauseSet.add(nodeToTest);
                    nodeToTest.setClauseSet(clauseSet);
                    clauseSets.add(clauseSet);
                }
            }
        } 
        
       System.out.print("\nAbout to print final sets");
       if (clauseSets != null) {
            //we're starting a new rule set level so output the last one
            for (int setCount = 0; setCount < clauseSets.size(); setCount ++) {
                ClauseSet clauseSet = (ClauseSet)clauseSets.get(setCount);
                if (outputLog) {
                    logfile.print("\nClause Set:");
                    clauseSet.toLogFile(logfile);
                }
                
                if (clauseSet.getTotalProb() < 0.999f)
                    if (outputLog)
                        logfile.print("WARNING Total prob:" + clauseSet.getTotalProb());
            }
        }
        System.out.print("\nFinished priting final sets");
         
        if (outputLog)
            logfile.close();
    }

    /*public void clearRewardAndError() {
        for (int i = clauseSetMap.size() -1; i > 0 ; i--) {
             ClauseSet seti = clauseSetMap.get(i);
             seti.setError(0);
             seti.setValue(0);
        }
    }*/

}



