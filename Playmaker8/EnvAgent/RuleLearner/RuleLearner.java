/*
 * RuleLearner.java
 *
 * Created on July 18, 2002, 5:27 PM
 */

package EnvAgent.RuleLearner;

import EnvModel.*;
import Logging.*;

import java.util.*;
import EnvAgent.*;

/**
 *
 * @author  eu779
 * @version 
 */



public abstract class RuleLearner {
    
    protected int ruleLearningMethod;
    
    protected PercepRecord percepRecord;
    protected ActionRecord actionRecord;
    
    public static final int MSDD = 0, APRIORI = 1;
    
    //GSTATISTIC
    protected static final float FINAL_G = 3.841f; //threshold which G(d1,d2,H) must exceed before d1 and d2 are considered
                       //to be different dependencies.
                       //3.841 gives 5% statistical significance
                       //2.706 gives 10%
                       //0.455 gives 50%
    
    /** Creates new RuleLearner */
    public RuleLearner(PercepRecord percepRecord, ActionRecord actionRecord) {
        this.percepRecord = percepRecord;
        this.actionRecord = actionRecord;


       // if (percepRecord.size() > 20000) { //This isn't set yet so will not work
            if (FINAL_G <= 2.706f)
                 System.out.print("\n*****STOP******STGOP\nFINAL_G SET FOR < 20k EVIDENCE at 2.706");
            else
               if (FINAL_G > 2.706f)
                   System.out.print("\n*****STOP******STGOP\nFINAL_G SET FOR > 20k EVIDENCE at > 2.706");
        //}
    }
    
    /*This chooses the appropriate learning algorithm for the domain area*/
    /*In the simplest case we can just learn entire state transitions*/
    /*MSDD method is implemented, based on:
     *  "Learning Planning Operators with Conditional and Probabilistic Rules"
     *     Tim Oates & Paul R. Cohen
     */
    public abstract NodeList learnRules();
   
    public RuleElements convertDatabaseEntryToRuleElements(int whichEntry, boolean precursor) {
        //Wildcard fluent, which can also match actions
       
        RuleElements ruleElements = null;
        
    
        if (precursor == true) {
            ruleElements = new RuleElements(actionRecord.getAction(whichEntry), percepRecord.getPercep(whichEntry));
        }
        else
        {
            ruleElements = new RuleElements(null, percepRecord.getPercep(whichEntry+1));
        }
       
        
        return ruleElements;
    }
    
    //count the number of times this RuleNode equals the database
    public int countDatabaseOccurrences(RuleNode n) {
        
        if (n.getDatabaseOccurrencesCounted())
            return (int)n.getDatabaseOccurrences();   
        
        int counter = 0;
        int dnMatch = 0;
      
        int precursorequals = 0;
        int precursorDNMatch = 0;
        
        RuleElements precursorFromDatabase;
        RuleElements postconditionsFromDatabase;
        
        for (int i = 0; i < percepRecord.size() -1; i++) {
            //If it's all wildcards it'll match everything
            //so we don't need to do the matching
            boolean match = false;
            boolean precursorMatch = false;
            if (!n.getPrecursor().hasANonWildcard())
                precursorMatch = true;
            else {
                precursorFromDatabase = convertDatabaseEntryToRuleElements(i, true);
                precursorMatch = precursorFromDatabase.equals(n.getPrecursor());
            }
            
            if (precursorMatch) {
                if (!n.getSuccessor().hasANonWildcard())
                    match = true;
                else {
                    postconditionsFromDatabase = convertDatabaseEntryToRuleElements(i, false);
                    if (postconditionsFromDatabase.equals(n.getSuccessor()))
                        match = true;
                }
            }
            
            if (precursorMatch)
                precursorequals ++;
            else
                precursorDNMatch ++;
            
            if (match)
                counter ++;
            else
                dnMatch ++;
        }
        
        //make sure we don't do this again for the same RuleNode
        n.setDatabaseOccurrencesCounted(true);
        n.setDatabaseOccurrences(counter);
        
        n.setPrecursorMatchEvaluated(true);
        n.setPrecursorequals(precursorequals);
        
        return counter;
    }
   
    
    /*Check a set of rules against a percep record. When two rules disagree about
     *the probability of a result and are indistinguishable by the fewer results
     *method and one is not a specialisation of the other: then we have to see 
     *which is most accurate on the intersection of percep record they apply to.
     *If one rule is accurate we mark it as being the one to use in situations where
     *the rules overlap. If neither is accurate create a new rule which is a
     *combination of the two rules*/
    
    public void supremecyRules(NodeList rules) {
        /*We check the rules by the usual state gen process of rule elimination
         *then when we discover conflicting rules test them against the
         *intersection of rules for which they work to see which one should be used
         *when they conflict.*/
        boolean outputLog = true;
        LogFile logfile = new LogFile(1);
            
        RuleSetMap ruleSetMap = new RuleSetMap();
        
        for (int i = 0; i < percepRecord.size() - 1; i++) {
            RuleElements testState = convertDatabaseEntryToRuleElements(i, true);
            
            //find all the nodes whos precursors match this state
            NodeList matchingNodes = rules.matchingNodes(testState);
            
            //Find all the rule sets present which match this state
            //(although the outputs may not be the same)
            ArrayList ruleSets = new ArrayList();
            
            for (int matchingCount = 0; matchingCount < matchingNodes.size(); matchingCount ++) {
                boolean alreadyPresent = false;
                for (int ruleSetCount = 0; ruleSetCount < ruleSets.size(); ruleSetCount ++) {
                    if (((RuleSet)ruleSets.get(ruleSetCount)) == matchingNodes.get(matchingCount).getRuleSet()) {
                        alreadyPresent = true;
                        ruleSetCount = ruleSets.size();
                    }
                }
                if (!alreadyPresent) {
                    ruleSets.add(matchingNodes.get(matchingCount).getRuleSet());
                    ruleSetMap.add(matchingNodes.get(matchingCount).getRuleSet());
                }
            }
            
            //Now compair each rule set in turn to find which wins in situations where they
            //are both applicable
            for (int ruleSetCount1 = 0; ruleSetCount1 < ruleSets.size(); ruleSetCount1 ++) {
                for (int ruleSetCount2 = 0; ruleSetCount2 < ruleSets.size(); ruleSetCount2 ++) {
                    RuleSet set1 = (RuleSet)ruleSets.get(ruleSetCount1);
                    RuleSet set2 = (RuleSet)ruleSets.get(ruleSetCount2);

                    //only test for precedence if they have the same output variable. Otherwise
                    //they are just different rule sets.
                    if (set1 != set2) {
                        if (set1.getOutputVariblePos() == set2.getOutputVariblePos()) {
                            if (!set1.precedenceSet(set2)) {
                                /*add code here to set presedence*/
                                if (firstRuleSetSuperior(set1, set2)) {
                                    set1.setPrecedenceOver(set2);
                                    set2.setDefersTo(set1);
                                }
                                else {
                                    set1.setDefersTo(set2);
                                    set2.setPrecedenceOver(set1);
                                }
                            }
                        }
                    }
                }
            }
          
            
        }
        
        ArrayList ruleSetsList = ruleSetMap.getArrayList();
        
        for (int i = 0; i < ruleSetsList.size(); i++) {
            logfile.print("\n\n\n TheRuleSet: \n");
            RuleSet ruleSet = (RuleSet)ruleSetsList.get(i);
            logfile.print(ruleSet.toString());
            logfile.print("\n Defers to:");
            for (int def = 0; def < ruleSet.getDefersToList().size(); def ++) {
                logfile.print("\n" + ruleSet.getDefersToList().get(def).toString());
            }
                       
            logfile.print("\n Precedence over: \n");
            for (int def = 0; def < ruleSet.getPrecedenceOverList().size(); def ++) {
                logfile.print("\n" + ruleSet.getPrecedenceOverList().get(def).toString());
            }
        }
        
        logfile.close();
        
    }
    
    /*We compair rule sets by first combining their precursors and then 
     *comparing the performance of each rule situations which match the
     *combined precursors.
     
     *We could do this much more quickly with a tid list approach.
     *
     *We could also do this in-line if we compair every situation in
     *which the two conflict in-line
     */ 
    public boolean firstRuleSetSuperior(RuleSet set1, RuleSet set2) {
        
        /*add code here to make sure presedence has not already been set*/
        if (set1.precedenceSet(set2))
            return set1.hasPrecedenceOver(set2);
        
        RuleElements combinedPrecursor = new RuleElements();
        combinedPrecursor = combinedPrecursor.getCombinedMostSpecific(set1.getPrecursor(), set2.getPrecursor());

        //Go through percep record counting the actual values for output fluents
        ArrayList outputFluents = new ArrayList();
        for (int i =0; i < set1.size(); i ++) {
            outputFluents.add(set1.get(i).getSuccessor().getFirstNonWildcard());
        }

        for (int i =0; i < set2.size(); i ++) {
            boolean alreadyPresent = false;
            RuleObject set2OutputFluent = set2.get(i).getSuccessor().getFirstNonWildcard();
            for (int outputCount = 0; outputCount < outputFluents.size(); outputCount ++) { 
                if (set2OutputFluent.isEqual((RuleObject)outputFluents.get(outputCount))) {
                    alreadyPresent = true; break;
                }
            }

            if (!alreadyPresent) {
                outputFluents.add(set2OutputFluent);
            }
        }

        int outputCounts[] = new int[outputFluents.size()];
        for (int i =0; i < outputFluents.size(); i++) {
            outputCounts[i] = 0;
        }

        int outputFluentNo = set1.get(0).getSuccessor().getFirstNonWildcardPosition();
        for (int i = 0; i < percepRecord.size() -1; i++) {
            final RuleElements precursorFromDatabase = convertDatabaseEntryToRuleElements(i, true);
            if (combinedPrecursor.equals(precursorFromDatabase)) {
                RuleElements postconditionsFromDatabase = convertDatabaseEntryToRuleElements(i, false);

                RuleObject databaseOutput = postconditionsFromDatabase.get(outputFluentNo);

                for (int outputCount = 0; outputCount < outputFluents.size(); outputCount ++) {
                    if (((RuleObject)outputFluents.get(outputCount)).isEqual(databaseOutput)) {
                        outputCounts[outputCount] += 1;
                        break;
                    }
                }
            }
        }
        
        int totalCount = 0;
        for (int i = 0; i < outputCounts.length; i++) {
            totalCount += outputCounts[i];
        }
        
        double outputProbabilities[] = new double[outputFluents.size()];
        
        for (int i = 0; i < outputProbabilities.length; i++) {
            outputProbabilities[i] = (double)outputCounts[i]/(double)totalCount;
        }
 
        double errorForSet1 = ruleSetErrorMeasure(set1, outputFluents, outputProbabilities);
        double errorForSet2 = ruleSetErrorMeasure(set2, outputFluents, outputProbabilities);
        
        return (errorForSet1 < errorForSet2);
    }
    
    
    /*Not quite sure how this deals with a sitaution where there is an output fluent
     *not present in the outputFluents. This should be a big error, but might get overshaddowed
     *by an error like real values of 0.5, 0.5 vs rule values of 06., 0.4
     *because the extra fluent may have a very low probability like 0.001
     **/
    public double ruleSetErrorMeasure(RuleSet ruleSet, ArrayList outputFluents, double outputProbabilities[]) {
        
        double totalError = 0.0f;
        for (int outputCount = 0; outputCount < outputFluents.size(); outputCount ++) {
        
            RuleObject outputFluent = (RuleObject)outputFluents.get(outputCount);
            boolean presentInRuleSetFluents = false;
            double error = 0.0f;
            for (int ruleSetCount = 0; ruleSetCount < ruleSet.size(); ruleSetCount ++) {
                if (outputFluent.isEqual(ruleSet.get(ruleSetCount).getSuccessor().getFirstNonWildcard())) {
                    presentInRuleSetFluents = true;
                    error = ruleSet.get(ruleSetCount).getProbability() - outputProbabilities[outputCount];
                    if (error < 0) /*this shuld be Mod (or is it Abs) but not sure of suntax*/
                        error = 0 - error; 
                    ruleSetCount = ruleSet.size(); //break;
                }
            }
            
            //For the moment I'm adding an error of 1,0 for every incorrect fluent
            //this is very arbitrary. The "real" error is the probability assigned to the
            //"wrong" fluent
            if (!presentInRuleSetFluents && (outputProbabilities[outputCount] != 0.0f)) {
                error = 1.0f;
            }
            totalError += error;
        }
        
        return totalError;
    }
    
    //count the number of times this RuleNodes precursor equals the database
    public int countPrecursorequals(RuleNode n) {
        
        if (n.getPrecursorMatchEvaluated())
            return (int)n.getPrecursorequals();
        
        /*First check: if it's all wildcards then it'll match everything*/
        if (!n.getPrecursor().hasANonWildcard()) {
            n.setPrecursorMatchEvaluated(true);
            n.setPrecursorequals(percepRecord.size()-1);
        
            return percepRecord.size()-1;
        }
        
        int counter = 0;
        
        for (int i = 0; i < percepRecord.size() -1; i++) {
            RuleElements precursorFromDatabase = convertDatabaseEntryToRuleElements(i, true);
            if (precursorFromDatabase.equals(n.getPrecursor())) {
                counter ++;
            }
        }
        
        n.setPrecursorMatchEvaluated(true);
        n.setPrecursorequals(counter);
        
        return counter;
    }
    
    /*This function counts the number of times in "H" (the observerd data set), that
     *the precursor of n is followed by the successor of n. In the case the precursor
     *means perception at time t, and the successor perception at time t+1)
     *
     *This is the function "f" in the MSDD algorithm. 
     *The majority of the computational load is done here.
     *The function will typically count co-occurances of the
     *MSDDRuleNodes precursor and successor, requiring a complete
     *pass over H (the perception/action data set).
    */
    
    public float heuristicEvaluationFunction(RuleNode n) {
        /*Go through preconditions and postconditions matching against
         *percep record and action record
        *The heuristic used in this instance simply counts co-occurances of
         *the precursor and successor*/
        
        return countDatabaseOccurrences(n);
    }
    
  
    
    public float GStatistic(int n1, int n2, int n3, int n4) {
        long r1 = n1 + n2;
        long r2 = n3 + n4;
        long c1 = n1 + n3;
        long c2 = n2 + n4;
        long t = r1 + r2;

        if ((n1 == n3) && (n2 == n4))
            return 0;

        /*both these rules have 100% reliability,*/
        /*so we remove the specific one as it should be covered by the general one*/
        if ((n2 + n4) == 0)
            return 0;

        if (n4 == 0)
            return 100;
        
        return 2.0f * (float)(
            (float)n1*Math.log((double)(n1*t)/(double)(r1*c1)) +
            (float)n2*Math.log((double)(n2*t)/(double)(r1*c2)) +
            (float)n3*Math.log((double)(n3*t)/(double)(r2*c1)) +
            (float)n4*Math.log((double)(n4*t)/(double)(r2*c2)));
    }
    
    /*The function subsumes return true if dependency d1 is a generalisation of
     *dependency d2*/
    public boolean subsumes(RuleNode d1, RuleNode d2) {
        
        /*Successor must be identical. We will likely know where the first non-wildcard
         element is as it's stored*/
        RuleElements d1succ = d1.getSuccessor();
        RuleElements d2succ = d2.getSuccessor();
        
        if (d1succ.getFirstNonWildcardPosition() != d2succ.getFirstNonWildcardPosition())
            return false;
        
        if (!d1succ.isEqualAt(d1succ.getFirstNonWildcardPosition(), d2succ.get(d1succ.getFirstNonWildcardPosition())))
            return false;
        
        RuleElements d1prec = d1.getPrecursor();
        RuleElements d2prec = d2.getPrecursor();
        for (int i =0; i < d1prec.size(); i++) {        
            //if d1 in position is not a wildcard and d2 is then d1 is not a generalisation
            if ((!d1prec.get(i).isWildcard()) && d2prec.get(i).isWildcard())
                return false;

            //if neither are wildcards and they are not equal then d1 is not a generalisation of d2
            if ((!d1prec.get(i).isWildcard()) && (!d2prec.get(i).isWildcard())) {
                if (!d1prec.isEqualAt(i, d2prec.get(i)))
                    return false;
            }
            
            /*we've got this far so at this point either they're not wildcards and 
             *both the same, or d1 has a wildcard and d2 does not, or both are 
             *wildcards*/
        }     
  
         //d1 has wildcards in at least all the positions that d2 has. When not wildcards
         //they have the same value.
         return true;
    }
    
    /*The g statistic measures whether the conditional probability of d2's successor
     *given its predecessor is different from the conditional probability of d2's 
     *successor given it's predecessor.
     *d1 must subsume d2
     */
    public float Gstatistic(RuleNode d1, RuleNode d2) {
        //1.count d1 predecessor equals with the database
        int d1Precursorequals = countPrecursorequals(d1);
        //2. count d1 full equals with the database
        int d1Fullequals = countDatabaseOccurrences(d1);
        
        //3. count d2 predecessor full equals with the database
        int d2Precursorequals = countPrecursorequals(d2);
        //4. count d2 successor full equals with the database
        int d2Fullequals = countDatabaseOccurrences(d2);
        
        //calculate numbers for the Gstatistic
        int n1 = d1Fullequals;
        int n2 = d1Precursorequals - d1Fullequals;
        int n3 = d2Fullequals;
        int n4 = d2Precursorequals -d2Fullequals;
        
        /*Ive put these bit in myself to cope with divide by zero, I think it gets shot of 
         *the right rules on the basis that both are match the same things but one subsumes the
         other*/
        if ((n1 == n3) && (n2 == n4))
            return 0;
        
        /*both these rules have 100% reliability,*/
        /*so we remove the specific one as it should be covered by the general one*/
        if ((n2 + n4) == 0)
            return 0;
        
        if (n4 == 0)
            return 100;
        
        float Gstat = GStatistic(n1, n2, n3, n4);
       
        
        return Gstat;
    }

    
    /*Remove general rules which are convered by more specific rules in the 
     *rule set. If not every fluent value is covered by more specific examples
     *check against the database to ensure that this fluent value ever comes 
     *up in the given context
     */
    public void removeGeneralRulesCoveredBySpecific(NodeList nodes) {
        //Sort nodes in non-increasing order of generality
        nodes.sortNonIncreasingGenerality();
        
        /* go through nodes from most general to least eliminating rules
         * which are covered by more specific examples. 
         * We look for rules which differ in one wildcard position from
         *the rule being tested.
         */
        NodeList postGeneralCoveredRules = new NodeList();
        
        LogFile logfile = new LogFile(1);
        for (int i = 0; i < nodes.size() -1; i++) {
            RuleNode nodeToTest = nodes.get(i);
            RuleElements testPrecursor = nodeToTest.getPrecursor();
            RuleElements testSuccessor = nodeToTest.getSuccessor();
            
            /*Go through all wildcard positions in the rule to see if a more specific
             *rule covers this one*/
            for (int whichPos = 0; whichPos < testPrecursor.size(); whichPos ++) {
                NodeList coveredByRules = new NodeList();
                NodeList noDatabaseMatchRules = new NodeList();
                if (testPrecursor.get(whichPos).isWildcard()) {
                    //make an boolean array with a position for each value of the fluent
                    //Mark each fluent value off as its discovered
                    boolean [] fluentValueequals;
                    fluentValueequals = new boolean[testPrecursor.get(whichPos).getNumValues()];
                    java.util.Arrays.fill(fluentValueequals,false);
                    
                    /*This is a wildcard so lets see if the rule is covered*/
                    /*Go through rules from this one on*/
                    for (int j = i+1; j < nodes.size(); j++) {
                        RuleNode node = nodes.get(j);
                        //first check that the successors are the same
                        boolean applicableRule = true;
                        if (!node.getSuccessor().isEqualTo(testSuccessor))
                            applicableRule = false;
                        //now this is a generalisation of the rule
                        if (!node.getPrecursor().equals(testPrecursor))
                            applicableRule = false;
                        
                        //now find out if the rule is the same in all positions except
                        //this wildcard
                        if (applicableRule) {
                            for (int position = 0; position < testPrecursor.size(); position ++) {
                                //if it's a different position it must be the same
                                //otherwise it must match (although I think we tested for this earlier so this test
                                //is superflouous)
                                if (position == whichPos) {
                                    if (!testPrecursor.matchAt(position, node.getPrecursor().get(position))) 
                                        applicableRule = false;
                                } else {
                                    if (!testPrecursor.isEqualAt(position, node.getPrecursor().get(position))) 
                                        applicableRule = false;
                                }
                            }
                        }
                        
                        if (applicableRule) {
                            //OK they match and differ just in the wildcard, so mark this one off
                            //from the fluent list
                            int fluentVal = node.getPrecursor().get(whichPos).getValue();
                            fluentValueequals[fluentVal] = true;
                            coveredByRules.add(node);
                        }
                    }
                    
                    //now test to see if this rule can be removed from the database
                    boolean covered = true;
                    for (int bob = 0; bob < testPrecursor.get(whichPos).getNumValues(); bob ++) {
                        if (fluentValueequals[bob] == false) {
                            covered = false; break;}
                       
                    }
                    
                    if (!covered) {
                       /*if we haven't quite covered the rule, go through the database 
                        *to see if the missing rules never occur in the database
                        */
                        RuleNode cloneRuleToTest = (RuleNode)nodeToTest.clone();
                        RuleElements rulePrecursor = (RuleElements)cloneRuleToTest.getPrecursor();
                        for (int fluentVal = 0; fluentVal < rulePrecursor.get(whichPos).getNumValues(); fluentVal ++) {
                            //if not already covered this fluent
                            if (!fluentValueequals[fluentVal]) {
                                //Make a rule of this one with the wildcards filled in
                                rulePrecursor.get(whichPos).setByValue(fluentVal);
                                cloneRuleToTest.setDatabaseOccurrencesCounted(false);
                                //see if it equals anythng in the database
                                int numequals = countDatabaseOccurrences(cloneRuleToTest);
                                //if equals == 0 then mark covered as true
                                if (numequals == 0) {
                                    fluentValueequals[fluentVal] = true;
                                    RuleNode recordClone = (RuleNode)cloneRuleToTest.clone();
                                    recordClone.setDatabaseOccurrences(cloneRuleToTest.getDatabaseOccurrences());
                                    recordClone.setPrecursorequals(cloneRuleToTest.getPrecursorequals());
                                    noDatabaseMatchRules.add((RuleNode)cloneRuleToTest.clone());
                                }
                                else {
                                    //This isn't covered so break out of the loop
                                    fluentVal = rulePrecursor.get(whichPos).getNumValues();
                                }
                            }
                        }
                        
                        /*now test again to see if we've covered the rule*/
                        covered = true;
                        for (int bob = 0; bob < testPrecursor.get(whichPos).getNumValues(); bob ++) {
                        if (fluentValueequals[bob] == false)
                            covered = false;
                        }
                    }
                    
                    if (covered) {
                        boolean stop = true;
                        //don't add this rule to the final rule database                      
                        //and finish going through the wildcards (break out of loop)
                        whichPos = testPrecursor.size();
                        
                        //This line seems to be removing the wrong rules
                        
                        logfile.print("\nRemoved " + nodeToTest.toString()+ ". Covered by more specific rules.");
                        nodes.remove(nodeToTest);
                        logfile.print("\nCovered by the following rules: \n");
                        for (int covering = 0; covering < coveredByRules.size(); covering++) {
                            logfile.print(coveredByRules.get(covering).toString() + "\n");
                        }
                        logfile.print("\nThese missing rules didn't match the database: ");
                        for (int covering = 0; covering < noDatabaseMatchRules.size(); covering++) {
                            logfile.print(noDatabaseMatchRules.get(covering).toString() + "\n");
                        }
                        
                        //go back one because we've removed the i'th node
                        i--;
                        
                        //Now remove any other rules with the same precursor and different
                        //conclusions for the same varibale, or we'll be left with dangling
                        //missing rules
                        RuleElements successor = nodeToTest.getSuccessor();
                      
                        int originalValue = successor.getNonWildcardElement(1).getValue();
                        for (int sucValues = 0; sucValues < successor.getNonWildcardElement(1).getNumValues(); sucValues ++) {
                            if (sucValues != originalValue) {
                                successor.getNonWildcardElement(1).setByValue(sucValues); 
                                for (int clearupCount = 0; clearupCount < nodes.size(); clearupCount++) {
                                    RuleNode node = nodes.get(clearupCount);
                                    //first check that the successors are the same
                                    boolean applicableRule = true;
                                    if (nodeToTest.getSuccessor().isEqualTo(node.getSuccessor())) {
                                        if (nodeToTest.getPrecursor().isEqualTo(node.getPrecursor())) {
                                            nodes.remove(clearupCount);
                                            logfile.print("\nAlso removed " + nodeToTest.toString()+ ". because it is for the same variable and has the same preconditions.");
                                            if (clearupCount <= i) {
                                                //we've removed a node that we were about to look
                                                //at so go back one.
                                                i--;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    
                    } 
                    
                } //end of "if wildcard"
            } //end of loop through wildcard positions
        } //and of loop through nodes
        logfile.close();
    }
    
 
   
    //The removal of rules causes some to be removed for one successor fluent but not another
    //This means we don't get rules which add up to one.
    //Next step is to create all succesor values for each precursor and fluent
    //and check that they are either in the rule base of don't match with the database
    //Check each rule to make sure every fluent value is represented
    public void addMissingRules(NodeList nodes) {   
        LogFile logfile = new LogFile(1);
        for (int nodeLoop = 0; nodeLoop < nodes.size(); nodeLoop ++) {
            RuleNode nodeToTest = (RuleNode)nodes.get(nodeLoop);
            RuleElements successor = nodeToTest.getSuccessor();
            NodeList newNodes = new NodeList();
            int fluentPosition = successor.getFirstNonWildcardPosition();
            for (int fluentLoop = 0; fluentLoop < successor.get(fluentPosition).getNumValues(); fluentLoop ++) {
                if (fluentLoop == successor.get(fluentPosition).getValue())
                    continue;
                RuleNode newNode = (RuleNode)nodeToTest.clone();
                newNode.setDatabaseOccurrencesCounted(false);
                //commented because surely these are not changed?
                newNode.setPrecursorMatchEvaluated(true);
                newNode.setPrecursorequals(nodeToTest.getPrecursorequals());
                newNode.getSuccessor().get(fluentPosition).setByValue(fluentLoop);
                if (!nodes.ruleAlreadyExists(newNode)) {
                    if (countDatabaseOccurrences(newNode) != 0) {
                        //this is now copied from the node to test
                        //countPrecursorequals(newNode);
                        nodes.addAt(0, newNode);

                        logfile.print("\n***Added missing rule...\n===========\n");
                        logfile.print(newNode.toString());
                        logfile.print("\n===========\n");
                        nodeLoop ++;
                    }
                }
            }
        }
        logfile.close();
    }
    
    //The removal of rules causes some to be removed for one successor fluent but not another
    //This means we don't get rules which add up to one.
    //Next step is to create all succesor values for each precursor and fluent
    //and check that they are either in the rule base of don't match with the database
    //Check each rule to make sure every fluent value is represented
    public void generateRuleSets(NodeList nodes) {
        
        nodes.sortNonIncreasingGenerality();
        LogFile logfile = new LogFile(1);
        
        int currentSpecificity = -1;
        ArrayList ruleSets = null;
        
        for (int nodeLoop = 0; nodeLoop < nodes.size(); nodeLoop ++) {
            RuleNode nodeToTest = (RuleNode)nodes.get(nodeLoop);
            RuleElements successor = nodeToTest.getSuccessor();
            
            //we've just changed specificity so save time by starting a
            //new set of rule sets.
            if (nodeToTest.getPrecursor().countNonWildcards() != currentSpecificity) {
                
                if (ruleSets != null) {
                    //we're starting a new rule set level so output the last one
                    for (int setCount = 0; setCount < ruleSets.size(); setCount ++) {
                        RuleSet ruleSet = (RuleSet)ruleSets.get(setCount);
                        logfile.print("\nRule Set:" + ruleSet.toString());
                    }
                }
                
                currentSpecificity = nodeToTest.getPrecursor().countNonWildcards();
                ruleSets = new ArrayList();
                 
                //we also know that we'll be creating a new rule set for this rule
                RuleSet ruleSet = new RuleSet();
                ruleSet.add(nodeToTest);
                nodeToTest.setRuleSet(ruleSet);
                ruleSets.add(ruleSet);
            } else {
                //first check that we haven't already generated this rule set
                // if we have then just add the rule to it
                boolean alreadyPresent = false;
                for (int i = 0; i < ruleSets.size(); i ++) {
                    RuleSet testSet = (RuleSet)ruleSets.get(i);
                    int outFluentPosition = successor.getFirstNonWildcardPosition();
                    if (testSet.getOutputVariblePos() == outFluentPosition) {
                        if (testSet.getPrecursor().isEqualTo(nodeToTest.getPrecursor())) {
                            alreadyPresent = true;
                            testSet.add(nodeToTest);
                            nodeToTest.setRuleSet(testSet);
                        }
                    }
                    if (alreadyPresent)
                        i = ruleSets.size();
                }
                
                if (!alreadyPresent) {
                    //we also know that we'll be creating a new rule set for this rule
                    RuleSet ruleSet = new RuleSet();
                    ruleSet.add(nodeToTest);
                    nodeToTest.setRuleSet(ruleSet);
                    ruleSets.add(ruleSet);
                }
            }
        }    
         
        logfile.close();
    }
  
    /*The filter algorithm takes the list of returned dependencies and removes from it
     *any that are not interesting. Interesting dependencies tell us something about the
     *environment which we're working in*/
    
    /*The int n bit is a little arbitrary. What if we did 2000 tests instead of 200. Also depends
     *on the complexity of the world we're investigating.
     */
    
    protected NodeList filter(NodeList nodes, int n, float g) {
        //Step 1: Remove all dependencies that have low frequency of occurence or only wildcards
        //          in the successor
        //Step 2: sort
        //Step 4: process operators in order of generality, removing subsumed opperators
        
        //Remove from D all dependencies d such that n(d) < n or e(d) contains only wildcards
        int currentNode = nodes.size() - 1;
        
        LogFile logfile = null;
        if (LogFile.OUTPUT_LOG0)
            logfile = new LogFile(1);
        while (currentNode >= 0) {
            if (nodes.get(currentNode).getSuccessor().isAllWildcardsFrom(1)) {
                if (LogFile.OUTPUT_LOG0) {
                    logfile.print("\nNode filtered because successor all wildcards\n");
                    logfile.print(nodes.get(currentNode).toString());
                }
                nodes.remove(currentNode);
            }
            else if (countDatabaseOccurrences(nodes.get(currentNode)) < n) {
                if (LogFile.OUTPUT_LOG0) {
                    logfile.print("\nRuleNode filtered because less than n occurrences\n");
                    logfile.print(nodes.get(currentNode).toString());
                }
                nodes.remove(currentNode);
            }
            currentNode --;
        }

        if (LogFile.OUTPUT_LOG0)
            logfile.print("\nThe set of rules after , n filtering is...\n===========\n===========\n");
        for (int i = 0; i < nodes.size(); i++) {
            if (LogFile.OUTPUT_LOG0) {
                logfile.print(nodes.get(i).toString());
                logfile.print("\n");
            }
        }
        if (LogFile.OUTPUT_LOG0)
            logfile.print("\n===========\n===========");
        
        //Sort D in non-increasing order of generality
        nodes.sortNonIncreasingGenerality();

        if (LogFile.OUTPUT_LOG0)
            logfile.print("\nThe set of rules after sorting...\n===========\n===========\n");
        for (int i = 0; i < nodes.size(); i++) {
            if (LogFile.OUTPUT_LOG0) {
                logfile.print(nodes.get(i).toString());
                logfile.print("\n");
            }
        }
        if (LogFile.OUTPUT_LOG0)
            logfile.print("\n===========\n===========");
        
        NodeList S = new NodeList();
        
        Date startFilter = new Date();
        long startTime = startFilter.getTime();
        
        //while not empty (S)
        while (nodes.size() > 0) {
            //s = POP(D)
            RuleNode s = nodes.get(0);
            //PUSH(s, S)
            S.add(s);
            nodes.remove(0);
            
            for (int i = 0; i < nodes.size(); i++) {
                RuleNode d = nodes.get(i);
                if (subsumes(s,d))  {
                    float gStat = Gstatistic(s,d);
                    if (LogFile.OUTPUT_LOG0)
                        logfile.print("\n Trying to filter " + d.toString()+ " generalised by " + s.toString() + " GStat: " + gStat);
                    if (Gstatistic(s, d) < g) {
                        if (LogFile.OUTPUT_LOG0)
                            logfile.print("\n filtered " + d.toString()+ " generalised by " + s.toString());
                        nodes.remove(i);
                        i--;
                    }
                }
            }
        }
        
        Date finishFilter = new Date();
        long elapsedTime = finishFilter.getTime() - startTime;

        LogFile logfile1 = new LogFile(2);
        logfile1.print("\n ACTUAL FILTER IN PROCESS (not add missing): " + elapsedTime + " MILLISECONDS.");
        System.out.print("\n ACTUAL FILTER PROCESS (not add missing): " + elapsedTime + " MILLISECONDS.");
        logfile1.close();

        if (LogFile.OUTPUT_LOG0)
            logfile = new LogFile(1);
        //The filtering of rules causes some to be removed for one successor fluent but not another
        //This means we don't get rules which add up to one.
        //Next step is to create all succesor values for each precursor and fluent
        //and check that they are either in the rule base of don't match with the database
        //Check each rule to make sure every fluent value is represented
        
        addMissingRules(S);
        
        
        //Now remove rules which are general but completely covered by more specific
        //rules
        if (LogFile.OUTPUT_LOG0)
            logfile.print("\n WARNING: NOT REMOVING GENERAL RULES COVERED BY MORE SPECIFIC");
        
        //There was something clever here about adding missing rules first so we didn't
        //filter leaving the odd rule by the wayside.
        //removeGeneralRulesCoveredBySpecific(S);
        if (LogFile.OUTPUT_LOG0)
            logfile.close();
        //There's another step here to remove non-significant stuff
        generateRuleSets(S);
    
        supremecyRules(S);
        
        return S;
            
    }
    
    /*Filter Specific node with the the more general nodes in nodes
     */
    protected boolean filterSpecific(RuleNode node, NodeList nodes, float g) {   
      
        LogFile logfile = null;
        if (LogFile.OUTPUT_LOG0)
            logfile = new LogFile(1);
        for (int i = 0; i < nodes.size(); i++) {
            RuleNode s = nodes.get(i);
            if (subsumes(s, node))  {
                float gStat = Gstatistic(s, node);
                if (LogFile.OUTPUT_LOG0)
                    logfile.print("\n Trying to filter " + node.toString()+ " generalised by " + s.toString() + " " + gStat);
                
                if (Gstatistic(s, node) < g) {
                    if (LogFile.OUTPUT_LOG0) {
                        logfile.print("\n Can filter " + node.toString()+ " generalised by " + s.toString());
                        logfile.close();
                    }
                    return true;
                }
            }
        }
        if (LogFile.OUTPUT_LOG0)
            logfile.close();
        return false;
    }
}
