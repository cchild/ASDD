/*
 * StateGenerator.java
 *
 * Created on 20 November 2002, 13:59
 */

package StateGenerator;

import EnvModel.*;

import java.util.*;

import Logging.*;

import EnvModel.PredatorModel.*;
import EnvAgent.*;
import EnvAgent.RuleLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class RuleStateGenerator extends StateGenerator {

    /** Creates new StateGenerator */
    
    NodeList ruleNodes;
 
    
    public RuleStateGenerator(NodeList ruleNodes){
        this.ruleNodes = ruleNodes;
    }
    
    public ArrayList generateNextStates(  Percep percep,   Action action) {
        RuleElements currentState = new RuleElements();
        currentState.add(action);
        for(int i = 0; i < percep.getNumFluents(); i++) {
            currentState.add(percep.getFluent(i));
        }
        
        NodeList matchingNodes = ruleNodes.matchingNodes(currentState);
        
        LogFiles logfile = LogFiles.getInstance();
        
        boolean outputLog = Logging.LogFiles.OUTPUT_LOG0;
        
        if (outputLog) {
            
            logfile.print("\nThe original matching nodes are...\n===========\n===========\n",1);
            logfile.print(matchingNodes.toString(),1);
            logfile.print("\n===========\n===========\n=FINISHED==",1);
             
        }
        
        boolean USE_THE_OLD_WAY_OF_DOING_IT = false;
        
        if (!USE_THE_OLD_WAY_OF_DOING_IT) {
            //Now we have precedence set between rule sets so we can just
            //pick the rules which have precedence and discard others
            matchingNodes = matchingNodes.filterByPrecedence();
            if (outputLog) {
                
                logfile.print("\nThe rules after filtering by precedence are...\n===========\n===========\n",1);
                logfile.print(matchingNodes.toString(),1);
                logfile.print("\n===========\n===========\n=FINISHED==",1);
                 
            }
            //false indicates filter if they have the same number of outcoes as well
            removeRulesWithLessSpecificOutcome(matchingNodes, false);
        } else {
        
            //true indicates actually less outcomes, not the same
            removeRulesWithLessSpecificOutcome(matchingNodes, true);

            if (outputLog) {
                
                logfile.print("\nThe pre-filtering covered by more specific nodes...\n===========\n===========\n",1);
                logfile.print(matchingNodes.toString(),1);
                logfile.flush(1);
                 
            }

            filterCoveredGeneralNodes(matchingNodes);

            if (outputLog) {
                
                logfile.print("\nThe pre-filtered for same number of outcomes set of rules is...\n===========\n===========\n",1);
                logfile.print(matchingNodes.toString(),1);
                logfile.flush(1);
                 
            }

            //false indicates filter if they have the same number of outcoes as well
            removeRulesWithLessSpecificOutcome(matchingNodes, false);

            if (outputLog) {
                
                logfile.print("\nThe generated states set of rules is...\n===========\n===========\n",1);
                logfile.print(matchingNodes.toString(),1);
                logfile.flush(1);
                 
            }
        } 

        ArrayList statesAndProbs = generateStates(matchingNodes, percep);
        
        if (outputLog) {
            
            logfile.print("\nThe generated states are...\n===========\n===========\n",1);

            //Print out the states before we filter
            for (int i = 0; i < statesAndProbs.size(); i++) {
                logfile.print(((StateAndProb)statesAndProbs.get(i)).getPercep().getString(),1);
                logfile.println(" " + ((StateAndProb)statesAndProbs.get(i)).getProbability(),1);
                
            }
             
        }
        
        //now remove the illegal states
        for (int i = statesAndProbs.size() -1; i >= 0; i--) {
            Percep returnPercep = ((StateAndProb)statesAndProbs.get(i)).getPercep();
            if (!returnPercep.legalState())
               statesAndProbs.remove(i);
        }
        
        if (outputLog) {
            
            logfile.print("\nThe generated states with illigal states removed are...\n===========\n===========\n",1);

            //Print out the states after we filter
            for (int i = 0; i < statesAndProbs.size(); i++) {

                logfile.print(((StateAndProb)statesAndProbs.get(i)).getPercep().getString(),1);
                logfile.println(" " + ((StateAndProb)statesAndProbs.get(i)).getProbability(),1);
                
            }
             
        }
        
        normaliseProbabilitiesOfGeneratedStates(statesAndProbs);
        
        if (outputLog) {
            
            logfile.print("\nThe states with probabilities normalised are...\n===========\n===========\n",1);

            //Print out the states after we filter
            double totalProb = 0.0f;
            for (int i = 0; i < statesAndProbs.size(); i++) {

                logfile.print(((StateAndProb)statesAndProbs.get(i)).getPercep().getString(),1);
                logfile.println(" " + ((StateAndProb)statesAndProbs.get(i)).getProbability(),1);
                totalProb += ((StateAndProb)statesAndProbs.get(i)).getProbability();
                
            }
            logfile.print("\n Total prob: " + totalProb + "\n",1);
            if (totalProb < 1.0f) {
                logfile.print("WARNING. TOTAL PROB IS " + totalProb + "\n",1);
            }

            logfile.print("\n===========\n===========\n=FINISHED==",1);
             
        }
        
        return statesAndProbs;
    }
    
 
    
    public void filterCoveredGeneralNodes(NodeList matchingNodes) { 
        /* go through nodes from most general to least eliminating rules
         * which are matched by more specific examples. As we already have
         * already matched to a state with no wildcards any rule which equals
         * a more general one covers it.
         *
         *We choose more specific rules because they should tell us more about
         *the actual state we're interested in.
         *
         *Note: on the other hand the more general ones will have been matched more
         *often so would give better statistics.
         */
        matchingNodes.sortNonIncreasingGenerality();
        
        for (int i = matchingNodes.size() -1; i >= 0; i--) {
            RuleNode nodeToTest = matchingNodes.get(i);
            //We're only interested in the precursor
            RuleElements testSuccessor = nodeToTest.getSuccessor();
            RuleElements testPrecursor = nodeToTest.getPrecursor();
            for (int j = i-1; j >= 0; j--) {
                //first check that they are both rules for the same successor fluent
                //successor's only have one non-wildcarded fluent
                //start from 1 as successor always have wildcarded action in first element
                int succFlu = 1;
                for (succFlu = 1; succFlu < testSuccessor.size(); succFlu++) {
                    //when we've found a non-wildcard break out of the loop
                    if (!testSuccessor.get(succFlu).isWildcard())
                        break;
                } //endfor
                
                //if we pass this test then these rules are for the same successor fluent
                if (!matchingNodes.get(j).getSuccessor().get(succFlu).isWildcard()) { 
                    //second make sure the rule we're trying to eliminate is more general (not equal)
                    if (matchingNodes.get(j).getPrecursor().countWildcards() > testPrecursor.countWildcards()) {
                        matchingNodes.remove(j);
                        
                        //We've removed a rule so the rule in the i loop will be one less
                        i--;
                    } //endif
                } //endif
            } //endfor (more general nodes)
        } //endfor (matching nodes)
    }
    
    /*Step 2:
      Eliminate rules which are same specificity in the precursors
      but have less specific fluent values in the outcome.
     */
    public void removeRulesWithLessSpecificOutcome(NodeList matchingNodes, boolean less) { 
       
        //Rules are in fact rule-sets as they have outcomes which will sum to one
        for (int i = matchingNodes.size() -1; i >= 0; i--) {
            RuleNode nodeToTest = matchingNodes.get(i);
            //We're only interested in the precursor
            RuleElements testSuccessor = nodeToTest.getSuccessor();
            RuleElements testPrecursor = nodeToTest.getPrecursor();
            int nonWildcardPos = testSuccessor.getFirstNonWildcardPosition();
            RuleElements otherPrecursor = null;
            int diffPrecursorNumFluentValues = 0;
            int samePrecursorNumFluentValues = 0;
            for (int j = matchingNodes.size() -1; j >= 0; j--) {
                if ((matchingNodes.get(j).getSuccessor().getFirstNonWildcardPosition() == nonWildcardPos) &&
                    !matchingNodes.get(j).getPrecursor().isEqualTo(testPrecursor)) {
                        if (otherPrecursor == null) {
                            //we haven't found other matching rules so use this one
                            diffPrecursorNumFluentValues = 1;
                            otherPrecursor = matchingNodes.get(j).getPrecursor();
                        } else {
                            //we have found matching rules before so test that
                            //this is the same precursor as before
                            if (otherPrecursor.isEqualTo(matchingNodes.get(j).getPrecursor())) {
                                diffPrecursorNumFluentValues++;
                            }
                        }
                } else {
                    if ((matchingNodes.get(j).getSuccessor().getFirstNonWildcardPosition() == nonWildcardPos) &&
                        matchingNodes.get(j).getPrecursor().isEqualTo(testPrecursor)) {
                        samePrecursorNumFluentValues ++;
                    } //endfor
                }
            }         
            
            //The different precursor has less values and isn't 0 therefore remove all nodes
            //matching the one we were testing
            boolean remove = false;
            
            
            if (!less) {
                if ((diffPrecursorNumFluentValues <= samePrecursorNumFluentValues) &&
                    (diffPrecursorNumFluentValues != 0)) 
                    remove = true;
            } else {
                 if ((diffPrecursorNumFluentValues < samePrecursorNumFluentValues) &&
                    (diffPrecursorNumFluentValues != 0)) 
                    remove = true; 
            }
            
            if (remove) {        
                matchingNodes.removeNodesWithPrecursorAndSuccessorPos(testPrecursor, nonWildcardPos);
                //We've removed a rule so the rule in the i loop will be one less
                i = matchingNodes.size() -1;
            } else {
                //The one we were testing is more specific so remove the other rules
                if ((diffPrecursorNumFluentValues > samePrecursorNumFluentValues) &&
                    (otherPrecursor != null)) {
                    matchingNodes.removeNodesWithPrecursorAndSuccessorPos(otherPrecursor, nonWildcardPos);
                    //We've removed a rule so the rule in the i loop will be one less
                    i = matchingNodes.size() -1;
                }
            }
           
        } //endfor (matching nodes)
        
    }
    
    /*From a set of matching nodes, generate a possible next state according
     *to the probabilities of each fluent.
     *If the new state is illigal, recurse until a valid state is generated*/
    private ArrayList generateStates(NodeList matchingNodes, Percep percep) {
      
        ArrayList statesAndProb = new ArrayList();
        RuleElements generatedState = (RuleElements)(matchingNodes.get(0).getPrecursor().clone());
        
        generatedState.get(0).setWildcard();
      
        ArrayList fluentInfo = new ArrayList();
        int totalStates = 0;
        for (int i = 1; i < generatedState.size(); i++) {
            
            //Create an array for possible fluent values of this fluent
            float fluentProbs[] = new float[generatedState.get(i).getNumValues()];
            for (int flLoop = 0; flLoop < fluentProbs.length; flLoop ++) {
                fluentProbs[flLoop] = 0.0f;
            }
            
            //loop through all matching rules;
            for (int matchLoop = 0; matchLoop < matchingNodes.size(); matchLoop ++) {
                //if the rule is non-wildcarded for this fluent
                if (!matchingNodes.get(matchLoop).getSuccessor().get(i).isWildcard()) {
                    //ERROR. This is wrong. We should average the probability for all rules at this fluent
                    //set the probabability of this fluent being this value in the fluentProbs table
                    fluentProbs[matchingNodes.get(matchLoop).getSuccessor().get(i).getValue()] = matchingNodes.get(matchLoop).getProbability();
                }
            }
            
            int numValues = 0;
            for (int fluentValuesLoop =0; fluentValuesLoop < fluentProbs.length; fluentValuesLoop ++) {
                if (fluentProbs[fluentValuesLoop] !=  0.0f)
                    numValues ++;
            }
            
            FluentValuesAndProb fluentValuesAndProb = new FluentValuesAndProb(numValues);
            fluentInfo.add(fluentValuesAndProb);
            int values = 0;
            for (int fluentValuesLoop =0; fluentValuesLoop < fluentProbs.length; fluentValuesLoop ++) {
                if (fluentProbs[fluentValuesLoop] !=  0.0f){
                    fluentValuesAndProb.setValue(values, fluentValuesLoop);
                    fluentValuesAndProb.setProbability (values, fluentProbs[fluentValuesLoop]);
                    values ++;
                }
            }
            
            //work out total combinations of states
            if (totalStates == 0)
                totalStates = values;
            else 
                totalStates *= values;
            
        }
        
        //now go through all the state and probability combinations adding them
        //to the list
        for (int whichState = 0; whichState < totalStates; whichState++) {
            //Create a percep for each state in turn corresponding to this number
            //using the fluent info
            statesAndProb.add(generateStateAndProbByOrder(percep, whichState, fluentInfo, totalStates));
        }
       
        return statesAndProb;
    }
    
    public StateAndProb generateStateAndProbByOrder(Percep percep, int whichState, ArrayList fluentInfo, int totalStates) {
        StateAndProb stateAndProb = new StateAndProb();
        Percep dummyPercep = (Percep)percep.clone();
        stateAndProb.setPercep((Percep)dummyPercep);
        
        int fluentDivider = totalStates;
        int remainder = whichState;
        
        double prob = 1.0f;
        
        //This is a tricky bit of code, but basically ensures that we go through eah fluent only once
        for (int fluentNumber = 0; fluentNumber < dummyPercep.getNumFluents(); fluentNumber ++) {
             if (((FluentValuesAndProb)fluentInfo.get(fluentNumber)).getNumValues() == 0) {
                System.out.print("ERROR: Division by zero in StateGenerator");
             }
             fluentDivider = fluentDivider/ ((FluentValuesAndProb)fluentInfo.get(fluentNumber)).getNumValues();
             if (fluentDivider == 0) {
                 System.out.print("ERROR: Division by zero in StateGenerator");
             }
             int fluentIndex = remainder/fluentDivider;
             remainder = remainder - fluentDivider*fluentIndex;
             int fluentVal = ((FluentValuesAndProb)fluentInfo.get(fluentNumber)).getValue(fluentIndex);
             dummyPercep.getFluent(fluentNumber).setByValue(fluentVal);
             prob *= ((FluentValuesAndProb)fluentInfo.get(fluentNumber)).getProbability(fluentIndex);
        }
        
        stateAndProb.setProbability(prob);
        
        return stateAndProb;
    }
}
