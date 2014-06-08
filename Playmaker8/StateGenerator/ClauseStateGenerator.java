/*
 * StateGenerator.java
 *
 * Created on 20 November 2002, 13:59
 */

package StateGenerator;

import EnvModel.*;

import java.util.*;

import Logging.*;

//import EnvModel.PredatorModel.*;
import EnvAgent.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.RuleLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClauseStateGenerator extends StateGenerator {

    /** Creates new StateGenerator */
    
    ClauseList clauseNodes;
    
    ClauseList level1Clauses;
 
    
    public ClauseStateGenerator(ClauseList clauseNodes){
        this.clauseNodes = clauseNodes;
        this.level1Clauses = clauseNodes.findLevel1Clauses();
    }
    
    
    public ArrayList generateNextStates(Percep percep, Action action) {
        ClauseElements currentClauseState = new ClauseElements(percep, action, true, level1Clauses);
        return generateNextClauseStates(currentClauseState, percep, action);
    }
    
    public ArrayList generateNextClauseStates(ClauseElements currentState, Percep percep, Action action) {
        ClauseList matchingNodes = clauseNodes.matchingNodes(currentState);
        
        boolean outputLog = Logging.LogFiles.OUTPUT_LOG0;
          
        
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nState before action:",1);
            logfile.print(currentState.toString(),1);
            //logfile.print("\n===========\n===========\n=FINISHED==");
             
        }
        
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nThe original matching nodes are...\n===========\n===========\n",1);
            logfile.print(matchingNodes.toString(),1);
            logfile.print("\n===========\n===========\n=FINISHED==",1);
             
        }
        
        
        //Now we have precedence set between rule sets so we can just
        //pick the rules which have precedence and discard others
        matchingNodes = matchingNodes.filterByPrecedence();
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nThe rules after filtering by precedence are...\n===========\n===========\n",1);
            logfile.print(matchingNodes.toString(),1);
            logfile.print("\n===========\n===========\n=FINISHED==",1);
             
        }
        //false indicates filter if they have the same number of outcoes as well
        removeClausesWithMoreSpecificOutcome(matchingNodes, false);
        
        

        ArrayList clauseStatesAndProbs = generateClauseStates(matchingNodes);
        
        if (outputLog) {
            double totalProb = 0.0f;
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nThe generated states are...\n===========\n===========\n",1);

            //Print out the states before we filter
            for (int i = 0; i < clauseStatesAndProbs.size(); i++) {
                logfile.print(((ClauseStateAndProb)clauseStatesAndProbs.get(i)).getPercep().toString(),1);
                logfile.println(" " + ((ClauseStateAndProb)clauseStatesAndProbs.get(i)).getProbability(),1);
                totalProb += ((ClauseStateAndProb)clauseStatesAndProbs.get(i)).getProbability();
                
                
            }
              
            logfile.print("Total Pre illegal Prob:" + totalProb + "\n",1);
            if (totalProb < 0.9999f) {
                logfile.print("WARNING. TOTAL PROB IS " + totalProb + "\n",1);
            }
             
        }
        
        ArrayList ruleStatesAndProbs = new ArrayList();
        for (int sp = 0; sp < clauseStatesAndProbs.size(); sp ++) {
            ClauseStateAndProb clauseStateAndProb = (ClauseStateAndProb)clauseStatesAndProbs.get(sp);
            StateAndProb ruleStateAndProb = new StateAndProb();
            ruleStateAndProb.setProbability(clauseStateAndProb.getProbability());
            ruleStateAndProb.setPercep(clauseStateAndProb.getPercep().convertToPercep(percep, action));
            ruleStatesAndProbs.add(ruleStateAndProb);
        }
        
        //now remove the illegal states
        for (int i = ruleStatesAndProbs.size() -1; i >= 0; i--) {
            Percep percep2 = ((StateAndProb)ruleStatesAndProbs.get(i)).getPercep();
            if (!percep2.legalState())
               ruleStatesAndProbs.remove(i);
        }
        
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nThe generated states with illigal states removed are...\n===========\n===========\n",1);

            //Print out the states after we filter
            for (int i = 0; i < ruleStatesAndProbs.size(); i++) {

                logfile.print(((StateAndProb)ruleStatesAndProbs.get(i)).getPercep().getString(),1);
                logfile.println(" " + ((StateAndProb)ruleStatesAndProbs.get(i)).getProbability(),1);
                
            }
             
        }
        
        normaliseProbabilitiesOfGeneratedStates(ruleStatesAndProbs);
        
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nThe states with probabilities normalised are...\n===========\n===========\n",1);

            //Print out the states after we filter
            double totalProb = 0.0f;
            for (int i = 0; i < ruleStatesAndProbs.size(); i++) {

                logfile.print(((StateAndProb)ruleStatesAndProbs.get(i)).getPercep().getString(),1);
                logfile.println(" " + ((StateAndProb)ruleStatesAndProbs.get(i)).getProbability(),1);
                totalProb += ((StateAndProb)ruleStatesAndProbs.get(i)).getProbability();
                
            }
            logfile.print("\n Total prob: " + totalProb + "\n",1);
            if (totalProb < 1.0f) {
                logfile.print("WARNING. TOTAL PROB IS " + totalProb + "\n",1);
            }

            logfile.print("\n===========\n===========\n=FINISHED==",1);
             
        }
        
        return ruleStatesAndProbs;
    }
    
 
    
   
    /*
      Eliminate rules which are same specificity in the precursors
      but have more specific fluent values in the outcome.
     */
    public void removeClausesWithMoreSpecificOutcome(ClauseList matchingNodes, boolean less) { 
       
        //We shouldn't need to do this, but at the moment if
        //two clauses don't appear in the same situation then we have to
        //get rid of one of them. Leave the less specific ones
        //as we're already accounting for the situation where both happen
        
        ClauseSetMap clauseSetMap = matchingNodes.buildClauseSetMap();
        
        if (clauseSetMap.size() > 5) {
            int heelo = 1;
        }
        
        for (int i = clauseSetMap.size() -1; i > 0 ; i--) {
            for (int j = i-1; j >= 0 ; j--) {
                ClauseSet seti = clauseSetMap.get(i);
                ClauseSet setj = clauseSetMap.get(j);
                
                //if (seti.getOutputVaribleName().equals(setj.getOutputVaribleName()))
                if (seti.getClauseHead().sameOutVariable(setj.getClauseHead()))
                {
                    if (seti.size() < setj.size()) {
                        matchingNodes.removeAllWithClauseSet(seti);
                        clauseSetMap.remove(i);
                        j=0;
                    } else {
                        matchingNodes.removeAllWithClauseSet(setj);
                        clauseSetMap.remove(j);
                        i--;
                    }
                    
                }
            }
            
        }
       
        
        if (clauseSetMap.size() > 5) {
            int heelo = 1;
        }
        
    }
    
    //From a set of matching nodes, generate a possible next state according
    //*to the probabilities of each fluent.
    private ArrayList generateClauseStates(ClauseList matchingNodes) {
      
       ArrayList clauseStatesAndProb = new ArrayList();
       
       //build the clause sets from the matching nodes.
       ClauseSetMap clauseSetMap = matchingNodes.buildClauseSetMap();
        
        int totalStates = 0;
        for (int i = 0; i < clauseSetMap.size(); i++) {
            if (i == 0)
                totalStates = clauseSetMap.get(i).size();
            else
                totalStates *= clauseSetMap.get(i).size();
        }
        
        //now go through all the state and probability combinations adding them
        //to the list
        for (int whichState = 0; whichState < totalStates; whichState++) {
            //Create a percep for each state in turn corresponding to this number
            //using the fluent info
            clauseStatesAndProb.add(generateClauseStateAndProbByOrder(whichState, clauseSetMap, totalStates));
        }
       
        return clauseStatesAndProb;
    }
    
    public ClauseStateAndProb generateClauseStateAndProbByOrder(int whichState, ClauseSetMap clauseSetMap, int totalStates) { 
        ClauseStateAndProb clauseStateAndProb = new ClauseStateAndProb();
        ClauseElements predPercep = new ClauseElements();
        clauseStateAndProb.setPercep(predPercep);
        
        int fluentDivider = totalStates;
        int remainder = whichState;
        
        double prob = 1.0f;
        
        //This is a tricky bit of code, but basically ensures that we go through eah fluent only once
        for (int fluentNumber = 0; fluentNumber < clauseSetMap.size(); fluentNumber ++) {
             if (clauseSetMap.get(fluentNumber).size() == 0) {
                System.out.print("ERROR: Division by zero in StateGenerator");
             }
             fluentDivider = fluentDivider/ clauseSetMap.get(fluentNumber).size();
             if (fluentDivider == 0) {
                 System.out.print("ERROR: Division by zero in StateGenerator");
             }
             int fluentIndex = remainder/fluentDivider;
             remainder = remainder - fluentDivider*fluentIndex;
             Term fluentVal = clauseSetMap.get(fluentNumber).get(fluentIndex).getClauseHead();
             predPercep.add(fluentVal);
             prob *= clauseSetMap.get(fluentNumber).get(fluentIndex).getProbability();
        }
        
        clauseStateAndProb.setProbability(prob);
        
        return clauseStateAndProb;
    }
}
