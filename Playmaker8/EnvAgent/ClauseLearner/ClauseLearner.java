/*
 * RuleLearner.java
 *
 * Created on July 18, 2002, 5:27 PM
 */

package EnvAgent.ClauseLearner;

import EnvAgent.RuleLearner.*;

import EnvModel.*;
import Logging.*;

import java.util.*;
import EnvAgent.*;

/**
 *
 * @author  eu779
 * @version 
 */



public abstract class ClauseLearner {
     
    protected PercepRecord percepRecord;
    protected ActionRecord actionRecord;
    protected ClausePercepRecord clausePercepRecord;
    
      //GSTATISTIC
    protected static final float FINAL_G = 3.841f; //threshold which G(d1,d2,H) must exceed before d1 and d2 are considered
                       //to be different dependencies. 
                       //3.841 gives 5% statistical significance
                       //2.706 gives 10%
                       //0.455 gives 50%
    
    protected ClauseList level1Clauses;
    
    /** Creates new RuleLearner */
    public ClauseLearner(PercepRecord percepRecord, ActionRecord actionRecord) {
        this.percepRecord = percepRecord;
        this.actionRecord = actionRecord;
        this.level1Clauses = null;
        this.clausePercepRecord = null;
    }
    
    /*This chooses the appropriate learning algorithm for the domain area*/
    /*In the simplest case we can just learn entire state transitions*/
    /*MSDD method is implemented, based on:
     *  "Learning Planning Operators with Conditional and Probabilistic Rules"
     *     Tim Oates & Paul R. Cohen
     */
    public abstract ClauseList learnClauses();
   
    public ClauseElements convertDatabaseEntryToClauseElements(int whichEntry, boolean precursor, ClauseList level1Clauses) {
        //Wildcard fluent, which can also match actions
       
        Percep p;
        Action a = null;
        if (precursor == true) {
            a = actionRecord.getAction(whichEntry);
            p = percepRecord.getPercep(whichEntry);
        }
        else
        {
            p = percepRecord.getPercep(whichEntry+1);
        }
      
        
        return new ClauseElements(p, a, precursor, level1Clauses);
    }
    
    //count the number of times this ClauseNode equals the database
    public int countDatabaseOccurrences(ClauseNode n, ClauseList level1Clauses) {
        
        if (n.getDatabaseOccurrencesCounted())
            return (int)n.getDatabaseOccurrences();   
        
        int counter = 0;
        int dnMatch = 0;
      
        int precursorequals = 0;
        int precursorDNMatch = 0;
        
        RuleElements precFromDatabase;
        RuleElements postFromDatabase;   
        
        for (int i = 0; i < percepRecord.size() -1; i++) {
            //If it's all wildcards it'll match everything
            //so we don't need to do the matching
            boolean match = false;
            boolean precursorMatch = false;
            Term possibleHead = (Term)n.getClauseHead();
            if (n.getClauseElements().getBodySize() == 0)
                precursorMatch = true;
            else {
                //ClauseElements precursorFromDatabase = convertDatabaseEntryToClauseElements(i, true, level1Clauses);
                //Level1Clauses just seems to slow it down for the moment
                ClauseElements precursorFromDatabase = convertDatabaseEntryToClauseElements(i, true, null);
                precursorMatch = n.bodyequals(precursorFromDatabase);
                //System.out.print("\nWARNING Clause Learner: after body equals need to make a substitution for the head\n Also needs lots of substitutions\n");
                if (precursorMatch)
                    possibleHead = n.possibleHeadForBodyMatch(precursorFromDatabase);
            }
            
            if (precursorMatch) {
                if (n.getClauseHead() == null)
                    match = true;
                else {
                    ClauseElements postconditionsFromDatabase = convertDatabaseEntryToClauseElements(i, false, level1Clauses);
                    if (possibleHead == null) {
                        int a = 1;
                    }
                    
                    if (postconditionsFromDatabase.contains(possibleHead))
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
        
        //make sure we don't do this again for the same ClauseNode
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
    
    public void supremecyClauses(ClauseList clauses) {
        /*We check the rules by the usual state gen process of rule elimination
         *then when we discover conflicting rules test them against the
         *intersection of rules for which they work to see which one should be used
         *when they conflict.*/
        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
          
        
        if (outputLog) {
            Singleton logfile = Singleton.getInstance();
        }
            
        ClauseSetMap clauseSetMap = clauses.buildClauseSetMap();
        
        ClauseList level1Clauses = clauses.findLevel1Clauses();
        
       
        System.out.print("\n Starting supremecy clauses");
        System.out.print("\n Total perceps " + percepRecord.size());
        
        buildClausePercepRecord();
        
        System.out.print("\n Currently NOT doing a memory clean after each percep!");
        
        for (int i = 0; i < clausePercepRecord.size() - 1; i++) {
            
            
            //System.runFinalization();
            //System.gc();
            
            ClauseElements testClause = clausePercepRecord.getPercep(i);
        
            System.out.print("\nWorking in percep " + i);
            System.out.flush();
            //find all the clauses whos precursors match this state
            ClauseList matchingClauses = clauses.matchingNodes(testClause);
            
            //Find all the rule sets present which match this state
            //(although the outputs may not be the same)
            ClauseSetMap clauseSets = matchingClauses.buildClauseSetMap();
            
            //Now compair each rule set in turn to find which wins in situations where they
            //are both applicable
            for (int clauseSetCount1 = 0; clauseSetCount1 < clauseSets.size(); clauseSetCount1 ++) {
                for (int clauseSetCount2 = 0; clauseSetCount2 < clauseSets.size(); clauseSetCount2 ++) {
                    ClauseSet set1 = (ClauseSet)clauseSets.get(clauseSetCount1);
                    ClauseSet set2 = (ClauseSet)clauseSets.get(clauseSetCount2);

                    //only test for precedence if they have the same output variable. Otherwise
                    //they are just different rule sets.
                    if (set1 != set2) {
                        //System.out.print("\nWarning We should say output variable names in Supremecy Clauses");
                        //System.out.flush();
                        //if (set1.getOutputVaribleName().equals(set2.getOutputVaribleName())) {
                        if (set1.getClauseHead().sameOutVariable(set2.getClauseHead())) {



                            if (!set1.precedenceSet(set2)) {
                                /*add code here to set presedence*/
                                //System.out.print("\n About to enter first clause set superior " + i);
                                //System.out.flush();
                                if (firstClauseSetSuperior(set1, set2)) {
                                    set1.setPrecedenceOver(set2);
                                    set2.setDefersTo(set1);
                                }
                                else {
                                    set1.setDefersTo(set2);
                                    set2.setPrecedenceOver(set1);
                                }
                                //System.out.print("\n Finished first clause set superior " + i);
                                //System.out.flush();
                            }
                        }
                    }
                }
            }
          
            
        }
        
        ArrayList clauseSetsList = clauseSetMap.getArrayList();
        
        if (outputLog) {
            for (int i = 0; i < clauseSetsList.size(); i++) {
                Singleton logfile = Singleton.getInstance();
                logfile.print("\n\n\n TheClauseSet: \n",1);
                ClauseSet clauseSet = (ClauseSet)clauseSetsList.get(i);
                logfile.print(clauseSet.toString(),1);
                logfile.print("\n Defers to:",1);
                for (int def = 0; def < clauseSet.getDefersToList().size(); def ++) {
                    for (int b = 0; b < clauseSetsList.size(); b++) {
                        if (((ClauseSet)clauseSetsList.get(b)).getRef() == clauseSet.getDefersToList().get(def))
                            logfile.print("\n" + ((ClauseSet)clauseSetsList.get(b)).toString(),1);
                    }
                }

                logfile.print("\n Precedence over: \n",1);
                for (int def = 0; def < clauseSet.getPrecedenceOverList().size(); def ++) {
                    for (int b = 0; b < clauseSetsList.size(); b++) {
                        if (((ClauseSet)clauseSetsList.get(b)).getRef() == clauseSet.getPrecedenceOverList().get(def))
                            logfile.print("\n" + ((ClauseSet)clauseSetsList.get(b)).toString(),1);
                    }
                }
            }

            
        }
        
    }


    public void  addEnvironmentSets(ClauseList clauses) {
        /*We check the rules by the usual state gen process of rule elimination
         *then when we discover conflicting rules test them against the
         *intersection of rules for which they work to see which one should be used
         *when they conflict.*/
        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
         

        if (outputLog) {
            Singleton logfile = Singleton.getInstance();
        }

        ClauseSetMap clauseSetMap = clauses.buildClauseSetMap();

        for (int clauseSetCount1 = 0; clauseSetCount1 < clauseSetMap.size(); clauseSetCount1 ++) {
            ClauseSet set1 = (ClauseSet)clauseSetMap.get(clauseSetCount1);
            if (set1.isFrameRule())
            {
                ClauseSet envtSet = null;
                if (!set1.containsEnvironmentOperatorSet())
                {
                    //we haven't already got an environment operator
                    //for this frame rule, so make one
                    set1.makeEnvirnonmentOperatorSet();
                    for (int clauseSetCount2 = clauseSetCount1; clauseSetCount2 < clauseSetMap.size(); clauseSetCount2 ++) {
                        ClauseSet set2 = (ClauseSet) clauseSetMap.get(clauseSetCount2);
                        if (set2.isFrameRule()) {
                            if (!set2.containsEnvironmentOperatorSet()) {
                                //check it referes to the same output variable
                                Clause clauseHead2 = (Clause)set2.get(0).getClauseHead();
                                if (clauseHead2.sameOutVariable(set1.getClauseHead())) {
                                    if (clauseHead2.isEqual(set1.getClauseHead())) {
                                        //if it does add it to the set1 envt set
                                        set1.addEnvirnonmentOperator(set2.getRef());
                                        //set the envt set to be the frame set
                                        set2.setEnvirnonmentOperator(set1.getRef());

                                        set2.setDefersTo(set1);
                                        set1.setPrecedenceOver(set2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList clauseSetsList = clauseSetMap.getArrayList();

        if (outputLog) {
            for (int i = 0; i < clauseSetsList.size(); i++) {
                ClauseSet clauseSet = (ClauseSet)clauseSetsList.get(i);
                
                if (clauseSet.containsEnvironmentOperatorSet())
                {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\n\n\n The Environment ClauseSet: \n",1);
                    logfile.print(clauseSet.toString(),1);
                    logfile.print("\n Defers to:",1);
                    for (int def = 0; def < clauseSet.getDefersToList().size(); def ++) {
                        for (int b = 0; b < clauseSetsList.size(); b++) {
                            if (((ClauseSet)clauseSetsList.get(b)).getRef() == clauseSet.getDefersToList().get(def))
                                if (((ClauseSet)clauseSetsList.get(b)).containsEnvironmentOperatorSet())
                                    logfile.print("\n" + ((ClauseSet)clauseSetsList.get(b)).toString(),1);
                        }
                    }

                    logfile.print("\n Precedence over: \n",1);
                    for (int def = 0; def < clauseSet.getPrecedenceOverList().size(); def ++) {
                        for (int b = 0; b < clauseSetsList.size(); b++) {
                            if (((ClauseSet)clauseSetsList.get(b)).getRef() == clauseSet.getPrecedenceOverList().get(def))
                                if (((ClauseSet)clauseSetsList.get(b)).containsEnvironmentOperatorSet())
                                    logfile.print("\n" + ((ClauseSet)clauseSetsList.get(b)).toString(),1);
                        }
                    }
                }
            }

            
        }

    }
    
    /*We compair rule sets by first combining their precursors and then 
     *comparing the performance of each rule situations which match the
     *combined precursors.
     
     *We could do this much more quickly with a tid list approach.
     *
     *We could also do this in-line if we compair every situation in
     *which the two conflict in-line
     *
     *Problem is that with varibale the same rule can apply to several outputs
     *and have several different bindings. It can only have one probability however
     */ 
    public boolean firstClauseSetSuperior(ClauseSet set1, ClauseSet set2) {



        /*add code here to make sure presedence has not already been set*/
        if (set1.precedenceSet(set2))
            return set1.hasPrecedenceOver(set2);


        
        //System.out.print("\nWarning: This will only work when no variables in rules");
        //However, when we do it properly we'll need to take the intersection
        //of the two rule's tid lists and see which one performs best. Hmmmmm
        //Also which one performs best on WHICH VARIBALE???
        
        ClauseElements combinedPrecursor = new ClauseElements();
        combinedPrecursor = combinedPrecursor.getCombinedBodies(set1.getBody(), set2.getBody());
        combinedPrecursor.orderByUniqueID(level1Clauses);
        
        //Go through percep record counting the actual values for output Variables
        ArrayList outputVariables = new ArrayList();
        for (int i =0; i < set1.size(); i ++) {
            outputVariables.add(set1.get(i).getClauseElements().getHead());
        }

        for (int i =0; i < set2.size(); i ++) {
            boolean alreadyPresent = false;
            Term set2OutputVariable = set2.get(i).getClauseElements().getHead();
            for (int outputCount = 0; outputCount < outputVariables.size(); outputCount ++) { 
                Clause outputVar = (Clause)outputVariables.get(outputCount);
                if (set2OutputVariable.equals(outputVar)) {
                    alreadyPresent = true; break;
                }
            }

            if (!alreadyPresent) {
                outputVariables.add(set2OutputVariable);
            }
        }

        int outputCounts[] = new int[outputVariables.size()];
        for (int i =0; i < outputVariables.size(); i++) {
            outputCounts[i] = 0;
        }

        buildClausePercepRecord();
        
        //int outputVariableNo = set1.get(0).getSuccessor().getFirstNonWildcardPosition();
        int notMatched = 0;
        for (int i = 0; i < clausePercepRecord.size()-1; i++) {
            if (combinedPrecursor.bodyequals(clausePercepRecord.getPercep(i))) {
                
                ClauseElements clausePostFromDB = convertDatabaseEntryToClauseElements(i, false, null);
                
                for (int j = 0; j < outputVariables.size(); j++) {
                    if (clausePostFromDB.contains((Clause)outputVariables.get(j))) {
                        outputCounts[j] ++;
                    }
                }
            } else {
                notMatched ++;
            }
        }
        
        if (notMatched == (percepRecord.size()-1)) {
            int whatever = 1;
            System.out.print("Something wrong in firstClauseSetSuperior. Things being compaired that never meet");
        }
        
        int totalCount = 0;
        for (int i = 0; i < outputCounts.length; i++) {
            totalCount += outputCounts[i];
        }
        
        double outputProbabilities[] = new double[outputVariables.size()];
        
        for (int i = 0; i < outputProbabilities.length; i++) {
            outputProbabilities[i] = (double)outputCounts[i]/(double)totalCount;
        }
 
        double errorForSet1 = clauseSetErrorMeasure(set1, outputVariables, outputProbabilities);
        double errorForSet2 = clauseSetErrorMeasure(set2, outputVariables, outputProbabilities);

        //This is to identify rules where the output varibale is contained in the body
        //of the rule so that we can identify FRAME variables
        if ((set1.get(0).getProbability() == 1) || (set2.get(0).getProbability() == 1))
        {
            if (set1.isFrameRule())
                return true;
            else
                if (set2.isFrameRule())
                    return false;
        }

        //check if they cover the same examples, but one has fewer conditions
        if ((set1.get(0).getProbability() == 1) && (set2.get(0).getProbability() == 1))
        {
            if (set1.get(0).getPrecursorequals() >= set2.get(0).getPrecursorequals())
                if (set1.get(0).clauseSize() <= set2.get(0).clauseSize())
                    return true;

            if (set2.get(0).getPrecursorequals() >= set1.get(0).getPrecursorequals())
               if (set2.get(0).clauseSize() <= set1.get(0).clauseSize())
                    return false;
        }



        if (errorForSet1 < errorForSet2)
        {
            //used to be
            //return true;

            //NOW: return true unless
            //they have different numbers of outputs
            //the errors are in close range)
            //&& (set2 is more general)
            //&& (the rusult of the rules is not statiscically significantly different

            //NOW:
            //Before we resolve the significance using the output variables,
            //let's check to see if they are significantly different. If they
            //are not then use the most general rule
            // *1st must subsume 2nd
            //Probably need to use Chi Squared instead here

            if (set1.size() != set2.size()) {
                //not the same number of output variables so keep the precedence
                return true;
            }

            if (Math.abs(errorForSet1 - errorForSet2) > 1.0f) {
                //not close in error value so keep the precedence
                return true;
            }

            if (set1.getBody().containsAnAction() && !set2.getBody().containsAnAction())
                return true;


            //was using size for generality - commented below
            //was this wrong anyway?
            //if (set1.get(0).clauseSize() <= set2.get(0).clauseSize())
            //    return false;

             //now using precursor matches within a range (Rew(FALSE) etc.)
            //if set 1 matches more examples then we're good to go (as the error is better as well)
            if (set1.get(0).getPrecursorequals() >= (set2.get(0).getPrecursorequals()))
            {
                int s1 = set1.get(0).getPrecursorequals();
                int s2 = set2.get(0).getPrecursorequals();

                if (set1.get(0).clauseSize() <= set2.get(0).clauseSize())
                {
                    //matches more and simpler rule so go with it
                    return true;
                }
                else if (set1.get(0).getPrecursorequals() >= (set2.get(0).getPrecursorequals() *1.1f))
                {
                    //not a simpler rule, but matches a decent number more examples so go with it.
                    return true;
                }
            }

           if (set1.get(0).getPrecursorequals() >= (set2.get(0).getPrecursorequals() *0.9f))
           {
               //we match slightly less exmples
               if (set1.get(0).clauseSize() <= set2.get(0).clauseSize())
                {
                   //but this is a simpler rule, so go with it.
                    //not a simpler rule, but matches a decent number more examples so go with it.
                    return true;
                }
           }
           
            //if set one matches less examples, then make sure they are singificantly different
            //don't need this line as we can only get here if the bit above was false
            //if (set1.get(0).getPrecursorequals() < (set2.get(0).getPrecursorequals()))
            {
                for (int i = 0; i < set1.size(); i++) {
                    if (!(Gstatistic(set1.get(i), set2.get(i), level1Clauses) < FINAL_G)) {
                        //first rule set is significantly different so keep precedence
                        return true;
                    }
                }
            }

            boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
             

            if (outputLog) {
                Singleton logfile = Singleton.getInstance();
                logfile.print("\n\n\n TheClauseSet: \n",1);
                logfile.print(set1.toString() + " precursor Equ1: " + set1.get(0).getPrecursorequals() + " precursor Equ2: " +set2.get(0).getPrecursorequals(),1);
                logfile.print("\n would be superior but not significant diff to:",1);
                logfile.print(set2.toString(),1);
                
            }



            return false;
        }
        else
        {
             if (set1.size() != set2.size()) {
                //not the same number of output variables so keep the precedence
                return false;
            }

            if (Math.abs(errorForSet1 - errorForSet2) > 1.0f)
                return false;

            if (set2.getBody().containsAnAction() && !set1.getBody().containsAnAction())
                return false;

            //was using size for generality - commented below
            //if (set2.get(0).clauseSize() <= set1.get(0).clauseSize())
            //    return false;

            if (set2.get(0).getPrecursorequals() >= (set1.get(0).getPrecursorequals()))
            {
                int s1 = set1.get(0).getPrecursorequals();
                int s2 = set2.get(0).getPrecursorequals();

                if (set2.get(0).clauseSize() <= set1.get(0).clauseSize())
                {
                    //matches more and simpler rule so go with it
                    return false;
                }
                else if (set2.get(0).getPrecursorequals() >= (set1.get(0).getPrecursorequals() *1.1f))
                {
                    //not a simpler rule, but matches a decent number more examples so go with it.
                    return false;
                }
            }

           if (set2.get(0).getPrecursorequals() >= (set1.get(0).getPrecursorequals() *0.9f))
           {
               //we match slightly less exmples
               if (set2.get(0).clauseSize() <= set1.get(0).clauseSize())
                {
                   //but this is a simpler rule, so go with it.
                    //not a simpler rule, but matches a decent number more examples so go with it.
                    return false;
                }
           }



            for (int i = 0; i < set1.size(); i++) {
                if (!(Gstatistic(set2.get(i), set1.get(i), level1Clauses) < FINAL_G))
                    return false;
            }

            boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
             

            if (outputLog) {
                Singleton logfile = Singleton.getInstance();
                logfile.print("\n\n\n TheClauseSet: \n",1);
                logfile.print(set2.toString() + " precursor Equ1: " + set1.get(0).getPrecursorequals() + " precursor Equ2: " +set2.get(0).getPrecursorequals(),1);
                logfile.print("\n would be superior but not significant diff to:",1);
                logfile.print(set1.toString(),1);
                
            }

            return true;
        }
    }
    
    
    /*Not quite sure how this deals with a sitaution where there is an output Variable
     *not present in the outputVariables. This should be a big error, but might get overshaddowed
     *by an error like real values of 0.5, 0.5 vs rule values of 06., 0.4
     *because the extra Variable may have a very low probability like 0.001
     **/
    public double clauseSetErrorMeasure(ClauseSet clauseSet, ArrayList outputVariables, double outputProbabilities[]) {
        
        double totalError = 0.0f;
        for (int outputCount = 0; outputCount < outputVariables.size(); outputCount ++) {
        
            //System.out.print("Clause set error measure does not take variables into account");
            
            Term outputVariable = (Term)outputVariables.get(outputCount);
            boolean presentInRuleSetVariables = false;
            double error = 0.0f;
            for (int clauseSetCount = 0; clauseSetCount < clauseSet.size(); clauseSetCount ++) {
                if (outputVariable.isEqual(clauseSet.get(clauseSetCount).getClauseHead())) {
                    presentInRuleSetVariables = true;
                    error = clauseSet.get(clauseSetCount).getProbability() - outputProbabilities[outputCount];
                    if (error < 0) /*this shuld be Mod (or is it Abs) but not sure of suntax*/
                        error = 0 - error; 
                    clauseSetCount = clauseSet.size(); //break;
                }
            }
            
            //For the moment I'm adding an error of 1,0 for every incorrect Variable
            //this is very arbitrary. The "real" error is the probability assigned to the
            //"wrong" Variable
            if (!presentInRuleSetVariables && (outputProbabilities[outputCount] != 0.0f)) {
                error = 1.0f;
            }
            totalError += error;
        }
        
        return totalError;
    }
    
    //count the number of times this Ruleclauses precursor equals the database
  public int countPrecursorequals(ClauseNode n, ClauseList level1Clauses) {
        
        if (n.getPrecursorMatchEvaluated())
            return (int)n.getPrecursorequals();
        
        //First check: if it's all wildcards then it'll match everything
        if (n.getBodySize() == 0) {
            n.setPrecursorMatchEvaluated(true);
            n.setPrecursorequals(percepRecord.size()-1);
        
            return percepRecord.size()-1;
        }
        
        int counter = 0;
        
        for (int i = 0; i < percepRecord.size() -1; i++) {
            
            //ClauseElements precursorFromDatabase = convertDatabaseEntryToClauseElements(i, true, level1Clauses);
            //Level1Clauses just seems to slow it down
            ClauseElements precursorFromDatabase = convertDatabaseEntryToClauseElements(i, true, null);
            if (n.bodyequals(precursorFromDatabase)) {
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
     *MSDDRuleclauses precursor and successor, requiring a complete
     *pass over H (the perception/action data set).
    */
    
    public float heuristicEvaluationFunction(ClauseNode n, ClauseList level1Clauses) {
        //Go through preconditions and postconditions matching against
        //percep record and action record
        //The heuristic used in this instance simply counts co-occurances of
        //the precursor and successor
        
        return countDatabaseOccurrences(n, level1Clauses);
    }
  
  
    
    public float GStatistic(long n1, long n2, long n3, long n4) {
        long r1 = n1 + n2;
        long r2 = n3 + n4;
        long c1 = n1 + n3;
        long c2 = n2 + n4;
        long t = r1 + r2;
        
        float returnVal = 2.0f * (float)(
            (double)n1*Math.log((double)(n1*t)/(double)(r1*c1)) +
            (double)n2*Math.log((double)(n2*t)/(double)(r1*c2)) +
            (double)n3*Math.log((double)(n3*t)/(double)(r2*c1)) +
            (double)n4*Math.log((double)(n4*t)/(double)(r2*c2)));

        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
         

        if (outputLog) {
            Singleton logfile = Singleton.getInstance();
        }
        if (returnVal < 0) {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\n(double)n1*Math.log((double)(n1*t)/(double)(r1*c1)) " + (double)n1*Math.log((double)(n1*t)/(double)(r1*c1)),1);
            logfile.print("\n(double)n2*Math.log((double)(n2*t)/(double)(r1*c2)) " + (double)n2*Math.log((double)(n2*t)/(double)(r1*c2)),1);
            logfile.print("\n((double)n3*Math.log((double)(n3*t)/(double)(r2*c1)) " + (double)n3*Math.log((double)(n3*t)/(double)(r2*c1)),1);
            logfile.print("\n(double)n4*Math.log((double)(n4*t)/(double)(r2*c2))) " + (double)n4*Math.log((double)(n4*t)/(double)(r2*c2)),1);
            logfile.print("\n (double)(n1*t)" + (double)(n1*t),1);
            logfile.print("\n (double)(r1*c1)" + (double)(r1*c1),1);
            logfile.print("\n (double)(n2*t)" + (double)(n2*t),1);
            logfile.print("\n (double)(r1*c2)" + (double)(r1*c2),1);
            logfile.print("\n (double)(n3*t)" + (double)(n3*t),1);
            logfile.print("\n (double)(r2*c1))" + (double)(r2*c1),1);
            logfile.print("\n (double)(n4*t)" + (double)(n4*t),1);
            logfile.print("\n (double)(r2*c2))" + (double)(r2*c2),1);
        }

        

        return returnVal;
    }

    
    /*The g statistic measures whether the conditional probability of d2's successor
     *given its predecessor is different from the conditional probability of d2's 
     *successor given it's predecessor.
     *d1 must subsume d2
     */
    public float Gstatistic(ClauseNode d1, ClauseNode d2, ClauseList level1Clauses) {
        //1.count d1 predecessor equals with the database
        long d1Precursorequals = countPrecursorequals(d1, level1Clauses);
        //2. count d1 full equals with the database
        long d1Fullequals = countDatabaseOccurrences(d1, level1Clauses);
        
        //3. count d2 predecessor full equals with the database
        long d2Precursorequals = countPrecursorequals(d2, level1Clauses);
        //4. count d2 successor full equals with the database
        long d2Fullequals = countDatabaseOccurrences(d2, level1Clauses);
        
        //calculate numbers for the Gstatistic
        long n1 = d1Fullequals;
        long n2 = d1Precursorequals - d1Fullequals;
        long n3 = d2Fullequals;
        long n4 = d2Precursorequals -d2Fullequals;

        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
          

        if (outputLog) {
            Singleton logfile = Singleton.getInstance();

            if (n2 < 0) {
                logfile.print("\n n2= d1Precursorequals - d1Fullequals:  " + n2,1);
                logfile.print("\n d1Fullequals: " + d1Fullequals,1);
                logfile.print("\n d1Precursorequals: " + d1Precursorequals,1);
            }

            if (n4 < 0) {
                 logfile.print("\n n4 = d2Precursorequals -d2Fullequals:  " + n4,1);
                 logfile.print("\n d2Fullequals: " + d1Fullequals,1);
                 logfile.print("\n d2Precursorequals: " + d1Precursorequals,1);
            }
        }


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
     *rule set. If not every Variable value is covered by more specific examples
     *check against the database to ensure that this Variable value ever comes 
     *up in the given context
     */
    public void removeGeneralRulesCoveredBySpecific(ClauseList clauses) {
        //Sort clauses in non-increasing order of generality
        clauses.sortNonIncreasingGenerality();
        
        /* go through clauses from most general to least eliminating rules
         * which are covered by more specific examples. 
         * We look for rules which differ in one wildcard position from
         *the rule being tested.
         */
        ClauseList postGeneralCoveredRules = new ClauseList();
        
        System.out.print("\n removeGeneralRulesCoveredBySpecific not implemented");
    }
    

   public void findFrameRules(ClauseList clauses, ClauseList level1Clauses) {

         
        boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
        if (outputLog) {
            Singleton logfile = Singleton.getInstance();
        }

       for (int clauseLoop = 0; clauseLoop < clauses.size(); clauseLoop ++) {
           ClauseNode clauseToTest = (ClauseNode)clauses.get(clauseLoop);
           if (clauseToTest.isActionUnchangedOutput()) {

                Clause head = (Clause)clauseToTest.getClauseHead();

                //For the moment make this a frame rule until we
                //show that it isn't
                clauseToTest.setFrameRule(true);

                NodeList newNodes = new NodeList();

                int lastPredicate = head.getNoPredicates() -1;
                //logfile.print("\ntesting for missing: " + clauseToTest.toString());

                //Note we assume last predicate is the varibale!!!
                //This is testing the "strong" version of the frame rule,
                //in that the variable must be uncahnged for the action
                //under any circumstances (this would also be an envt opp
                for (int headLoop = 0; headLoop < head.getPredicate(lastPredicate).getNumValues(); headLoop ++) {
                    boolean presentInClauseList = false;
                    for (int clauseLoop2 = 0; clauseLoop2 < clauses.size(); clauseLoop2 ++) {
                        ClauseNode clauseToTest2 = (ClauseNode)clauses.get(clauseLoop2);
                        Clause clauseHead2 =  (Clause)clauseToTest2.getClauseHead();
                        if (clauseHead2.sameOutVariable(head))
                        {
                            if (headLoop == clauseHead2.getPredicate(lastPredicate).getValue())
                            {
                                if (clauseToTest2.isActionUnchangedOutput())
                                {
                                    if (clauseToTest2.getClauseElements().hasSameAction(clauseToTest.getClauseElements()))
                                    {
                                        presentInClauseList = true;
                                    }
                                }
                            }
                        }
                    }
                    if (!presentInClauseList)
                    {
                        headLoop = head.getPredicate(lastPredicate).getNumValues();
                        clauseToTest.setFrameRule(false);
                    }
               }

               if (outputLog) {
                    if (clauseToTest.getFrameRule()) {
                        Singleton logfile = Singleton.getInstance();
                        logfile.print("\n***Found a frame rule...\n===========\n",1);
                        logfile.print(clauseToTest.toString(),1);
                        logfile.print("\n===========\n",1);
                    }
               }
           }
       }

   }
   
    //The removal of rules causes some to be removed for one successor Variable but not another
    //This means we don't get rules which add up to one.
    //Next step is to create all succesor values for each precursor and Variable
    //and check that they are either in the rule base of don't match with the database
    //Check each rule to make sure every Variable value is represented
    public void addMissingClauses(ClauseList clauses, ClauseList level1Clauses) {
            
        Singleton logfile = Singleton.getInstance();
        ClauseSetMap clauseSets = clauses.buildClauseSetMap();
        for (int clauseLoop = 0; clauseLoop < clauses.size(); clauseLoop ++) {
            ClauseNode clauseToTest = (ClauseNode)clauses.get(clauseLoop);
            if (clauseToTest.getClauseSet().getTotalProb() < 0.999f) {

                Clause head = (Clause)clauseToTest.getClauseHead();

                NodeList newNodes = new NodeList();

                int lastPredicate = head.getNoPredicates() -1;
                //logfile.print("\ntesting for missing: " + clauseToTest.toString());

                //Note we assume last predicate is the varibale!!!
                for (int headLoop = 0; headLoop < head.getPredicate(lastPredicate).getNumValues(); headLoop ++) {
                    boolean presentInClauseSet = false;
                    for (int clauseSetLoop = 0; clauseSetLoop < clauseToTest.getClauseSet().size(); clauseSetLoop ++) {
                        Term theHead = ((ClauseNode)clauseToTest.getClauseSet().get(clauseSetLoop)).getClauseElements().getHead();
                        if (headLoop == head.getPredicate(lastPredicate).getValue())
                            presentInClauseSet = true;
                    }
                    
                    if (!presentInClauseSet) {

                        ClauseNode newNode = (ClauseNode)clauseToTest.clone();
                        newNode.setDatabaseOccurrencesCounted(false);
                        //This will be the same as the parent node
                        newNode.setPrecursorMatchEvaluated(true);
                        newNode.setPrecursorequals(clauseToTest.getPrecursorequals());
                        ((Variable)((Clause)newNode.getClauseHead()).getPredicate(lastPredicate)).setValue(headLoop);
                        //logfile.print("\ntrying: " + newNode.toString());


                        //re-set clause head unique ID to 0 for new ID;
                        ((Clause)newNode.getClauseHead()).setID(0);
                        //re-set preordered to false
                        newNode.getClauseElements().setOrdered(false);
                        //check that the new clause head exists in level 1 clauses
                        newNode.getClauseElements().orderByUniqueID(level1Clauses);
                        if (newNode.getClauseHead().getUniqueID() == 0){
                            int notOn = 1;
                            //if we get here then its not in the database
                        }


                        if (!clauses.contains(newNode.getClauseElements())) {
                            if (countDatabaseOccurrences(newNode, level1Clauses) != 0) {
                                //I think this has to have been done just done this above.
                                //countPrecursorequals(newNode, level1Clauses);
                                clauses.addAt(0, newNode);

                                logfile.print("\n***Added missing clause...\n===========\n",1);
                                logfile.print(newNode.toString(),1);
                                logfile.print("\n===========\n",1);
                                clauseLoop ++;
                            }
                        }
                    }
                }
            }
        }
        clauses.nullClauseSetMap();
        
    }
    
    //The removal of rules causes some to be removed for one successor Variable but not another
    //This means we don't get rules which add up to one.
    //Next step is to create all succesor values for each precursor and Variable
    //and check that they are either in the rule base of don't match with the database
    //Check each rule to make sure every Variable value is represented
    /*public void generateClauseSets(ClauseList nodes) throws java.lang.OutOfMemoryError {
   
       //This is the bit that has to be written next. First of all we have to get over the problem
       //of how to define a varibale. It should be able to read this in from a file somehow?
       
        nodes.sortNonIncreasingGenerality();
        Singleton logfile = Singleton.getInstance();
        
        int currentSpecificity = -1;
        ArrayList clauseSets = null;
        
        System.out.print("\nNodes size: " + nodes.size());
        for (int nodeLoop = 0; nodeLoop < nodes.size(); nodeLoop ++) {
            ClauseNode nodeToTest = (ClauseNode)nodes.get(nodeLoop);
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
                        logfile.print("\nClause Set:" + clauseSet.toString());
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
                logfile.print("\nClause Set:" + clauseSet.toString());
                
                if (clauseSet.getTotalProb() < 0.999f)
                    logfile.print("WARNING Total prob:" + clauseSet.getTotalProb());
            }
        }
       System.out.print("\nFinished priting final sets");
         
         
    }*/
  
    /*The filter algorithm takes the list of returned dependencies and removes from it
     *any that are not interesting. Interesting dependencies tell us something about the
     *environment which we're working in*/
    
    /*The int n bit is a little arbitrary. What if we did 2000 tests instead of 200. Also depends
     *on the complexity of the world we're investigating.
     */
    
    protected ClauseList filter(ClauseList clauses, int n, float g, ClauseList level1Clauses) {
        //Step 1: Remove all dependencies that have low frequency of occurence or only wildcards
        //          in the successor
        //Step 2: sort
        //Step 4: process operators in order of generality, removing subsumed opperators
        
        //Remove from D all dependencies d such that n(d) < n or e(d) contains only wildcards
        int currentClause = clauses.size() - 1;

          
        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
        }
        while (currentClause >= 0) {
            if (clauses.get(currentClause).getClauseHead() == null) {
                if (LogFile.OUTPUT_LOG0) {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\nClause filtered because it has no head\n",1);
                    logfile.print(clauses.get(currentClause).toString(),1);
                }
                clauses.remove(currentClause);
            }
            else if (countDatabaseOccurrences(clauses.get(currentClause), level1Clauses) < n) {
                if (LogFile.OUTPUT_LOG0) {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\nClauseNode filtered because less than n occurrences\n",1);
                    logfile.print(clauses.get(currentClause).toString(),1);
                }
                clauses.remove(currentClause);
            }
            currentClause --;
        }

        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\nThe set of rules after , n filtering is...\n===========\n===========\n",1);
            
        }
            
        for (int i = 0; i < clauses.size(); i++) {
            if (LogFile.OUTPUT_LOG0) {
                Singleton logfile = Singleton.getInstance();
                logfile.println(clauses.get(i).toString(),1);
                
            }
        }
        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\n===========\n===========",1);
        }
        
        //Sort D in non-increasing order of generality
        clauses.sortNonIncreasingGenerality();

        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\nThe set of rules after sorting...\n===========\n===========\n",1);
        }
        for (int i = 0; i < clauses.size(); i++) {
            if (LogFile.OUTPUT_LOG0) {
                Singleton logfile = Singleton.getInstance();
                logfile.print(clauses.get(i).toString(),1);
                logfile.print("\n",1);
            }
        }
        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\n===========\n===========",1);
        }
            
        
        ClauseList S = new ClauseList();
        
        Date startFilter = new Date();
        long startTime = startFilter.getTime();
        
        //while not empty (S)
        while (clauses.size() > 0) {
            //s = POP(D)
            ClauseNode s = clauses.get(0);
            //PUSH(s, S)
            S.add(s);
            clauses.remove(0);
            
            for (int i = 0; i < clauses.size(); i++) {
                ClauseNode d = clauses.get(i);
                if (!d.isActionUnchangedOutput()) {
                    //don't filter out the single action unchanged output rules
                    //thes might turn ouyt to be frame rules. We need them to make the RVRL#
                    //system work properly
                    if (s.subsumes(d))  {
                        float gStat = Gstatistic(s,d, level1Clauses);
                        //logfile.print("\n Trying to filter " + d.toString()+ " generalised by " + s.toString() + " GStat: " + gStat);
                        if (Gstatistic(s, d, level1Clauses) < g) {
                            if (s.getClauseElements().containsAnAction() || !d.getClauseElements().containsAnAction()) {
                                //only filer at low level if the generalising rule has an action
                                //unless the other rule also has no action
                                if (LogFile.OUTPUT_LOG0)
                                {
                                    Singleton logfile = Singleton.getInstance();
                                
                                    logfile.print("\n Filtered " + d.toString()+ " generalised by " + s.toString() + "G: " + Gstatistic(s, d, level1Clauses),1);
                                    
                                if (Math.abs(d.getProbability() - s.getProbability()) > 0.1f) {
                                   if (LogFile.OUTPUT_LOG0)
                                   {
                                       
                                       logfile.print("\n GENERALISED RULE WITH QUITE LARGE PROB DIFFERENCE",1);
                                   }
                                   float anotherStat = Gstatistic(s, d, level1Clauses);
                                   if (anotherStat < 0)
                                   {
                                       if (LogFile.OUTPUT_LOG0)
                                            logfile.print("G: " + Gstatistic(s, d, level1Clauses),1);
                                   }
                                }
                                clauses.remove(i);
                                i--;
                            } else {
                                if (Gstatistic(s, d, level1Clauses) < 0.1f) {
                                    if (LogFile.OUTPUT_LOG0) {
                                        Singleton logfile = Singleton.getInstance();
                                    
                                        logfile.print("\n Filtered " + d.toString()+ " generalised by " + s.toString(),1);
                                        logfile.print("\n Even though generaliser has no action (high significance) " + "G: " + Gstatistic(s, d, level1Clauses),1);
                                    }

                                    if (Math.abs(d.getProbability() - s.getProbability()) > 0.1f) {
                                        if (LogFile.OUTPUT_LOG0)
                                        {
                                            Singleton logfile = Singleton.getInstance();
                                            logfile.print("\n GENERALISED RULE WITH QUITE LARGE PROB DIFFERENCE",1);
                                        }
                                        float anotherStat = Gstatistic(s, d, level1Clauses);
                                       if (anotherStat < 0)
                                       {
                                            if (LogFile.OUTPUT_LOG0)
                                            {
                                                Singleton logfile = Singleton.getInstance();
                                                logfile.print("G: " + Gstatistic(s, d, level1Clauses),1);
                                            }
                                       }
                                    }
                                    clauses.remove(i);
                                    i--;
                                } else {
                                    if (LogFile.OUTPUT_LOG0) {
                                        Singleton logfile = Singleton.getInstance();
                                    
                                         logfile.print("\n Would have filtered " + d.toString()+ " generalised by " + s.toString() + "G: " + Gstatistic(s, d, level1Clauses),1);
                                         logfile.print("\n But generaliser has no action ",1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Date finishFilter = new Date();
        long elapsedTime = finishFilter.getTime() - startTime;
        
        Singleton logfile2 = Singleton.getInstance();
        
        logfile2.print("\n ACTUAL FILTER IN PROCESS: " + elapsedTime + " MILLISECONDS.",2);
        
        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\n ACTUAL FILTER IN PROCESS: " + elapsedTime + " MILLISECONDS.",1);
        }
        System.out.print("\n ACTUAL FILTER PROCESS: " + elapsedTime + " MILLISECONDS.");
        
        //The filtering of rules causes some to be removed for one successor Variable but not another
        //This means we don't get rules which add up to one.
        //Next step is to create all succesor values for each precursor and Variable
        //and check that they are either in the rule base of don't match with the database
        //Check each rule to make sure every Variable value is represented
        Date startAddMissing = new Date();
        long startAddMissingTime = startFilter.getTime();
        
        try {
            S.generateClauseSets();
        } catch (java.lang.OutOfMemoryError error) {
            // this shouldn't happen because Stack is Cloneable
            System.out.print(error.getMessage());
        }
        
        System.out.print("\n Before add missing\n");
        System.out.flush();
        addMissingClauses(S, level1Clauses);
        Date finishAddMissing = new Date();
        long elapsedMissing = finishAddMissing.getTime() - startAddMissingTime;
        
        
        
        logfile2.print("\n Add Missing in: " + elapsedMissing + " MILLISECONDS.",2);
        System.out.print("\n Add Missing in: " + elapsedMissing + " MILLISECONDS.");
        System.out.flush();
        

        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\nThe set of rules after filtering...\n===========\n===========\n",1);
        }
        for (int i = 0; i < S.size(); i++) {
            if (LogFile.OUTPUT_LOG0) {
                Singleton logfile = Singleton.getInstance();
            
                logfile.println(S.get(i).toString(),1);
                
            }
        }
        if (LogFile.OUTPUT_LOG0) {
            Singleton logfile = Singleton.getInstance();
        
            logfile.print("\n===========\n===========",1);
            logfile.flush(1);
        }
        //Now remove rules which are general but completely covered by more specific
        //rules
        if (LogFile.OUTPUT_LOG0)
        {
            Singleton logfile = Singleton.getInstance();
            logfile.print("\n WARNING: NOT REMOVING GENERAL RULES COVERED BY MORE SPECIFIC",1);
        }
        
        //There was something clever here about adding missing rules first so we didn't
        //filter leaving the odd rule by the wayside.
        //removeGeneralRulesCoveredBySpecific(S);
        
        //There's another step here to remove non-significant stuff
        
        System.out.print("\n Before clause sets.");
        System.out.flush();
        
        try {
            S.generateClauseSets();
        } catch (java.lang.OutOfMemoryError error) {
            // this shouldn't happen because Stack is Cloneable
            System.out.print(error.getMessage());
        }
        
    
        System.out.print("\n After clause sets.");
        System.out.flush();

        findFrameRules(S, level1Clauses);
        
        //Need to build a new ClauseSetMap here
        //And then go through all the frame rules and find
        //the other corresponding frame rule
        //Then filter these out into environment rules
        //Every rule needs to have a reference to it's environment rule version
        //If the rule hasn't been created yet, then create it and reference it. 
        //If it has, then just reference it.

        supremecyClauses(S);
        
        System.out.print("\n After supremency clauses.");
        System.out.flush();

        addEnvironmentSets(S);
        
        return S;
            
    }
    return null;
    }
    
    /*Filter Specific node with the the more general clauses in clauses
     */
    
protected boolean filterSpecific(ClauseNode node, ClauseList clauses, float g, ClauseList level1Clauses) {   
      
        Singleton logfile = Singleton.getInstance();
        for (int i = 0; i < clauses.size(); i++) {
            ClauseNode s = clauses.get(i);
            if (s.subsumes(node)) {
                float gStat = Gstatistic(s, node, level1Clauses);
                //logfile.print("\nAPRIORI Trying to filter " + node.toString()+ " generalised by " + s.toString() + " " + gStat);
                
                if (gStat < g) {
                    
                
                    logfile.print("\nAPRIORI Can filter " + node.toString()+ " generalised by " + s.toString()  + " " + gStat,1);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public ClauseList getLevel1Clauses() {
        return level1Clauses;
    }
    
    public void setLevel1Clauses(ClauseList level1Clauses) {
        this.level1Clauses = level1Clauses;
    }

    public void buildClausePercepRecord() {
        
        if (clausePercepRecord == null) {
            clausePercepRecord = new ClausePercepRecord();
            for (int i = 0; i < percepRecord.size()-1; i++) {
                
                ClauseElements clausePrecFromDB = convertDatabaseEntryToClauseElements(i, true, getLevel1Clauses());
                clausePercepRecord.addPercep(clausePrecFromDB);
            }
        }
    }
}
