package EnvAgent.RuleLearner;

import EnvModel.*;
import Logging.*;

import java.util.*;
import EnvAgent.*;

public class RuleLearnerApriori extends RuleLearner {
    
    //protected static int LARGE_ITEMSET_MINIMUM = 1;
    public static final int MINSUP = 1;
   
    protected static final float APRIORI_G = 0.445f; //threshold which G(d1,d2,H) must exceed before d1 and d2 are considered
                                                    //to be different dependencies.
                                                    //3.84 gives 5% statistical significance
                                                    //0.445 gives 50% statistical significance
    
    /** Creates new RuleLearner */
    public RuleLearnerApriori(Percep agentPercep, Action agentAction, PercepRecord percepRecord, ActionRecord actionRecord) {
        super(percepRecord, actionRecord);
       
        if (percepRecord.size() > 20000) {
            if (APRIORI_G <= 0.445f)
                System.out.print("\n*****STOP******STOP\nAPRIORI SET FOR < 20k EVIDENCE at 0.445");
        } else {
            if (APRIORI_G > 0.445f)
                System.out.print("\n*****STOP******STOP\nAPRIORI SET FOR > 20k EVIDENCE at > 0.445");
        }
    }
    
    public NodeList learnRules() {
        
        NodeList learnedRules;
        
        Date start = new Date();
        long startTime = start.getTime();
         
        learnedRules = learnRulesApriori();
          
        Date finish = new Date();
        long elapsedTime = finish.getTime() - startTime;
        
      
        
        LogFiles logfile2 = LogFiles.getInstance();
        logfile2.print("\n APRIORI DD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.",2);
        System.out.print("\n APRIORI DD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.");
        
        
        LogFiles logfile = LogFiles.getInstance();
        logfile.print("\nThe pre-filtered set of rules is...\n===========\n===========\n",1);
        logfile.print(learnedRules.toString(),1);
        logfile.print("\n===========\n===========",1);
        //Now filter the dependencies
        //This is called filter(D, H, n, g) in the paper
        int n = 1; //parameter indicating degree to which dependencies with low frequency of 
                   //co-occurance should be removed. In this case just min number of occurences
        
        Date startFilter = new Date();
        startTime = startFilter.getTime();
        
        learnedRules = filter(learnedRules, n, FINAL_G);
        
        Date finishFilter = new Date();
        elapsedTime = finishFilter.getTime() - startTime;
        
        
        logfile2.print("\n APRIORI DD FILTER IN: " + elapsedTime + " MILLISECONDS.",2);
        System.out.print("\n APRIORI DD FILTER IN: " + elapsedTime + " MILLISECONDS.");
         

        
        for (int i = 0; i < learnedRules.size(); i++) {
            //Not sure we should have to do this, but just in case the values are not set for some reason
            //It won't take any time if they'returnalready set anyway
            countPrecursorequals(learnedRules.get(i));
            countDatabaseOccurrences(learnedRules.get(i));
        }
        
        logfile.print("\nThe post-filtered sorted set of rules is...\n===========\n===========\n",1);
        logfile.print(learnedRules.toString(),1);
        logfile.print("\n===========\n===========\n=FINISHED==",1);
         
        
        return learnedRules;
    }
    
    public NodeList learnRulesApriori(){
        
        ArrayList L = new ArrayList();
        /*Learning rules based on apriori method takes the following steps
         *From: Fast Algorithms for mining association rules
         *1) L0 = {large 1-itemsets}
         *2) for (k=1; Lk-1 != 0; k++) do begin
         *3) Ck = apriori-gen(Lk-1); //New candidates
         *4)    forall transactions t in D do begin
         *5)        Ct = subset(Ck, t); //Candidates contained in t
         *6)        forall candidates c in Ct do
         *7)            c.count++;
         *8)    end
         *9)    Lk = {c in Ck | c.count >= minsup}
         *10)end
         *11)Answer = unionk Lk;
         */          
        
        //1) //VERY CAREFUL L1 is L1 not L0!!!!
        NodeList LDummy = new NodeList();  
        L.add(LDummy); //Just add a dummy node list in L0 to keep terms simple
    
        
        //2)
        for (int k=1; ((NodeList)L.get(k-1)).size() != 0 || k == 1; k++) {
            NodeList candidatesK;
            if (k ==1) {
                candidatesK = oneItemSets();
            } else {
                candidatesK = aprioriGen((NodeList)L.get(k-1), k);
            }
            
            for (int i = 0; i < percepRecord.size() -1; i++) {
                final RuleElements precursorFromDatabase = convertDatabaseEntryToRuleElements(i, true);
                final RuleElements postconditionsFromDatabase = convertDatabaseEntryToRuleElements(i, false);
                NodeList candidatesSubset  = subset(candidatesK, precursorFromDatabase, postconditionsFromDatabase);
                for (int candidCount = 0; candidCount < candidatesSubset.size(); candidCount ++) {
                    candidatesSubset.get(candidCount).incrementOccurrences();
                }
            }

              
            
            for (int candidCount = 0; candidCount < candidatesK.size(); candidCount ++) {
                if (candidatesK.get(candidCount).getDatabaseOccurrences() < MINSUP) {
                    if(LogFiles.OUTPUT_LOG0)
                    {
                        LogFiles logfile = LogFiles.getInstance();
                        logfile.print("\nPruned candidate: " + candidatesK.get(candidCount).toString(),1);
                    }
                    candidatesK.remove(candidCount);
                    candidCount --;
                }
                else {
                    //We've counted the occurrences of this in the database, so set the
                    //flag saying we have
                    candidatesK.get(candidCount).setDatabaseOccurrencesCounted(true);
                    if (!candidatesK.get(candidCount).getSuccessor().hasANonWildcard()) {
                        //This rule element has non-instantiated successor so
                        //set precursor equals to database occurences
                        candidatesK.get(candidCount).setPrecursorequals(candidatesK.get(candidCount).getDatabaseOccurrences());
                    }
                }
            }
            
                 
            
            /*Not sure if we should do this at level 2 as well, but let's see what it does
            (This is becuase the precursor will have no non-wildcards at level 2
            *not quite sure of this).
            */
          
            //This is the setting of the gStatistic for apriori filter;
            //It should be more sensitive than the usuual gStatistic as there
            //may be rules at greater depth which have more significance
            //GSTATISTIC
            
            if (k > 3) {
               
                candidatesK = aprioriFilter(candidatesK, (NodeList)L.get(k-3), APRIORI_G);
            
                //Need to add afunction here to remove level k-1 rules which
                //are entirelyy covered by k level rules
                //but we also need to check that the rules are not subsumed
                //at the final g level!!!
                aprioriRemoveGeneralCoveredBySpecific(candidatesK, (NodeList)L.get(k-1), (NodeList)L.get(2), FINAL_G);  
            }
            
            L.add(candidatesK);
        }
        
        //Now put all the candidates together
        NodeList learnedRules = new NodeList();
        for (int itemsLoop = 0; itemsLoop < L.size(); itemsLoop++) {
            NodeList lNodes = (NodeList)L.get(itemsLoop);
            for (int nodesLoop = 0; nodesLoop < lNodes.size(); nodesLoop ++) {
                learnedRules.add((RuleNode)lNodes.get(nodesLoop));
            }
        }
        
        return learnedRules;
    }
    
    /*Take a set of level L-1 candidates and return level L candidates*/
    protected NodeList aprioriGen(NodeList Lminus1Candidates, int lDepth) {
        
        NodeList candidatesK = new NodeList();

          
        
        //First: the join step
        for (int xCount = 0; xCount < Lminus1Candidates.size(); xCount ++) {
            for (int yCount = 0; yCount < Lminus1Candidates.size(); yCount++) {
                if (xCount != yCount) {
                    RuleElements xPrecursor = Lminus1Candidates.get(xCount).getPrecursor();
                    RuleElements yPrecursor = Lminus1Candidates.get(yCount).getPrecursor();
                    boolean generateCandidate = true;
                    for (int lCount = 0; lCount < lDepth-2; lCount++) {
                        int elementPosition = xPrecursor.getNonWildcardPosition(lCount+1);
                        
                        //If we've run out of non-wildcards in the precursor there's no candidates to generate
                        //as we only want one non-wildcard in the successor
                        if (elementPosition == xPrecursor.size())
                            generateCandidate = false;
                        if (generateCandidate) {
                            if (!xPrecursor.isEqualAt(elementPosition,  yPrecursor.get(elementPosition))) {
                                generateCandidate = false;
                                lCount = lDepth -1;
                            }
                        }
                    }
                    
                    //don't generate because one of the parents is already proabbility 1
                    if (Lminus1Candidates.get(xCount).getSuccessor().hasANonWildcard() && Lminus1Candidates.get(xCount).getProbability() == 1)
                         generateCandidate = false;

                    if (Lminus1Candidates.get(yCount).getSuccessor().hasANonWildcard() && Lminus1Candidates.get(yCount).getProbability() == 1)
                         generateCandidate = false;
                    
                    if (generateCandidate) {
                        //This is similar to the less than in the apriori-gen, but we're using the non-wildcard position instead of an ID
                        if (xPrecursor.getNonWildcardPosition(lDepth-1) < yPrecursor.getNonWildcardPosition(lDepth-1)) {
                            //generate a new rule node from one of the old ones
                            RuleElements newPrecursor = (RuleElements)xPrecursor.clone();
                            RuleElements newSuccessor = (RuleElements)Lminus1Candidates.get(xCount).getSuccessor().clone();
                            //add a new element at lDepth to the value of the yNode lDepth-1 element because we're generating a new node for these elements
                            boolean addNewNode = true;
                            if (yPrecursor.getNonWildcardPosition(lDepth-1) == yPrecursor.size()) {
                                //There aren't enough non-wildcards in the precursor, so it must be in the successor
                                if (newSuccessor.hasANonWildcard()) {
                                    //We only want to generate rules which have one non-wildcard in the consequence
                                    addNewNode = false;
                                }
                                else {
                                    RuleElements ySuccessor = (RuleElements)Lminus1Candidates.get(yCount).getSuccessor();
                                    newSuccessor.set(ySuccessor.getNonWildcardPosition(1), ySuccessor.getNonWildcardElement(1));
                                }
                            } else {
                                newPrecursor.set(yPrecursor.getNonWildcardPosition(lDepth-1), yPrecursor.getNonWildcardElement(lDepth-1));
                            }
                            
                            
                            //add a new element at lDepth to the value of the yNode lDepth-1 element because we're generating a new node for these elements
                            RuleNode newCandidate = new RuleNode(newPrecursor,  newSuccessor);
                            
                            //If the new sucessor has a consequence then one of the generating rules will be the precursor, so we can
                            //set the precursor equals from the generating rule
                            if (newPrecursor.isEqualTo(xPrecursor)) {
                                if (Lminus1Candidates.get(xCount).getPrecursorMatchEvaluated())
                                    newCandidate.setPrecursorequals(Lminus1Candidates.get(xCount).getPrecursorequals());
                            } else if (newPrecursor.isEqualTo(yPrecursor)) {
                                if (Lminus1Candidates.get(yCount).getPrecursorMatchEvaluated())
                                    newCandidate.setPrecursorequals(Lminus1Candidates.get(yCount).getPrecursorequals());
                            }
                            
                            candidatesK.add(newCandidate);
                            if(LogFiles.OUTPUT_LOG0)
                            {
                                LogFiles logfile = LogFiles.getInstance();
                                logfile.print("\nGenerated candidate is " + newCandidate.toString(),1);
                            }
                        }
                    }
                }
            }
        }
        if(LogFiles.OUTPUT_LOG0)
             
        
        /*lDepth > 2 because apriori prune will have no effect on depth 2
         *candidates and apriori filter should probably be left until level 3
         */
      
        if (lDepth > 2) {
            //Second: the prune step
            candidatesK = aprioriPrune(candidatesK, Lminus1Candidates);

          
        }

        
  
        return candidatesK;
    }
    
    protected NodeList aprioriPrune(NodeList candidatesK, NodeList Lminus1Candidates) {
        
          
        
        
        for (int candidateCount = 0; candidateCount < candidatesK.size(); candidateCount ++) {
            //subsets of rules of this form are just the rule with a wildcard in each
            //of the instntiated non-wildcard positions
            NodeList candidateSubsets = subsetsOf(candidatesK.get(candidateCount));
            boolean validRule = true;
            for (int subsetCount = 0; subsetCount < candidateSubsets.size(); subsetCount ++) {
                boolean contained = false;
                for (int Lminus1CandidatesCount = 0; Lminus1CandidatesCount < Lminus1Candidates.size(); Lminus1CandidatesCount ++) {
                    if (Lminus1Candidates.get(Lminus1CandidatesCount).isEqualTo(candidateSubsets.get(subsetCount))) {
                        contained = true;
                        //break out of the Lminus1candidates count
                        Lminus1CandidatesCount = Lminus1Candidates.size();
                    }
                }
                if (!contained) {
                    validRule = false;
                    //break out of the subsets loop;
                    subsetCount = candidateSubsets.size();
                }
            }
            if (!validRule) {
                if (LogFiles.OUTPUT_LOG0)
                {
                    LogFiles logfile = LogFiles.getInstance();
                    logfile.print("\nRemoved by Apriori prune: " + candidatesK.get(candidateCount).toString(),1);
                }
                candidatesK.remove(candidateCount);
                candidateCount --;
            }
        }

        
             
        return candidatesK;
    }
    
    
    
    protected NodeList aprioriFilter(NodeList candidatesK, NodeList Lminus1Candidates, float g) {
        
        /*Filter Specific node with the the more general nodes in nodes
        */
        
        //We can check a whole list at one
        //We must also check that both rules have the same consequence
        //SUBSUMES: filtering in general we should check the effects match first to save a lot of matching
        
        NodeList Lminus1CandidatesWithSuccessor = new NodeList();
        
        for (int loop = 0; loop < Lminus1Candidates.size(); loop ++) {
            if (Lminus1Candidates.get(loop).getSuccessor().hasANonWildcard()) {
                Lminus1CandidatesWithSuccessor.add(Lminus1Candidates.get(loop));
            }
        }
        
        for (int candidateLoop = (candidatesK.size() -1); candidateLoop >= 0; candidateLoop --) {
            RuleNode node = candidatesK.get(candidateLoop);
            if (node.getSuccessor().hasANonWildcard()) {
                if  (filterSpecific(node, Lminus1CandidatesWithSuccessor, g)) {
                    candidatesK.remove(candidateLoop);
                }
            }
        }
        
        return candidatesK;
    }
    
    /*This function removes rules general rules at level k-1 which are covered by rules
     *at level k. the level 2 candidates are required to test whether all possible 
     *values of an input are covered*/
    void aprioriRemoveGeneralCoveredBySpecific(NodeList candidatesK, NodeList kMinus1Nodes, NodeList level2Dependencies, float gStatistic) {
        
    }
    
    protected NodeList subsetsOf(RuleNode node) {
        NodeList subsets = new NodeList();
        for (int precursorLoop = 0; precursorLoop < 2; precursorLoop ++) {
            RuleElements precSucc;
            if (precursorLoop == 0)
                precSucc = node.getPrecursor();
            else
                precSucc = node.getSuccessor();
                
            for (int positionsLoop = 0; positionsLoop < precSucc.size(); positionsLoop ++) {
                if (!precSucc.get(positionsLoop).isWildcard()) {
                    RuleElements precursor = (RuleElements)node.getPrecursor().clone();
                    RuleElements successor = (RuleElements)node.getSuccessor().clone();
                    if (precursorLoop == 0) {//it's the precursor
                        precursor.get(positionsLoop).setWildcard();
                    } else {
                        successor.get(positionsLoop).setWildcard();
                    }
                    subsets.add(new RuleNode(precursor, successor));
                }
            }
        }
        
        return subsets;
    }
    
    protected NodeList oneItemSets() {
            
        NodeList candidatesK = new NodeList();
        //We should do this by matching with the database, but can
        //probably achieve more quickly using expand() for the moment.
        
        RuleElements precursorFromDatabase = (RuleElements)convertDatabaseEntryToRuleElements(0, true).clone();
        RuleElements postconditionsFromDatabase = (RuleElements)convertDatabaseEntryToRuleElements(0, false).clone();
        
        for (int i = 0; i < precursorFromDatabase.size(); i++) {
            precursorFromDatabase.get(i).setWildcard();
            postconditionsFromDatabase.get(i).setWildcard();
        }
        
        RuleNode emptyNode = new RuleNode(precursorFromDatabase, postconditionsFromDatabase);
        
        LogFiles logfile = LogFiles.getInstance();
        logfile.print("\n Created (but not added) Empty Root Node: " + emptyNode.toString() + "\n",1);
      
        expand(emptyNode, candidatesK);
            
         
        return candidatesK;
    }
    
    private void expandPrecSucc(RuleNode n, boolean precursor, NodeList children) {
        RuleElements precSucc;
        
          
        if (LogFiles.OUTPUT_LOG0) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\n",1);
        }
     
        if (precursor)
            precSucc = n.getPrecursor();
        else
            precSucc = n.getSuccessor();
            
        for (int i = precSucc.size() -1; i >= 0; i--) {
            //a. if n.precursor[i] != "*" then
            if (!precSucc.get(i).isWildcard()){
                //return children
                //System.out.flush();
                return; //it says return children but don't think that works
            }

            if (!precursor) { //We're dealing with the successor
                /*we only want to generate children with one output set
                * so if one part is already non-wildcard then don't bother any more*/

                for (int j = 0; j < precSucc.size(); j++) {
                    if (!precSucc.get(j).isWildcard()) {
                        //System.out.flush();
                        return;
                    }
                }
            }

       
            RuleObject t = precSucc.get(i);
            int numValues = t.getNumValues();

            for (int values = 0; values < numValues; values ++) {
                boolean avoid = false;

                if ((!precursor) && (i == 0)) {
                    avoid = true;
                }

                RuleNode child = (RuleNode)n.clone();
                if (!avoid) {
                    RuleObject e;
                    if (precursor) {
                        e = child.getPrecursor().get(i);
                    }
                    else {
                        e = child.getSuccessor().get(i);
                    }
                    e.setByValue(values);

                    if (LogFiles.OUTPUT_LOG0)
                    {
                        LogFiles logfile = LogFiles.getInstance();
                        logfile.print("\n added " + child.toString(),1);
                    }
                    children.add(child);
                }
            }
        }
        if (LogFiles.OUTPUT_LOG0)
             
        return;
    }
    
    /*Expand a node generating all it's children*/
    private void expand(RuleNode n, NodeList children) {
        expandPrecSucc(n, true, children);
        expandPrecSucc(n, false, children);

    }
    
    /* This function tells us whether the given node should be pruned because it
     *doesn't fit in with environmental factors.*/
    private boolean prune(RuleNode n) {
        /*first get rid of rules with no action. These don't tell us anything about 
        *the effects of actions on the world*/
        if (n.getPrecursor().hasNoAction()) {
            if (n.getPrecursor().isAllWildcardsFrom(1) && (n.getSuccessor().isAllWildcardsFrom(0)))
                return false; //it's all wildcards so OK.
            else
                return true;
        }
        
        /*prune any actions that aren't valid*/
        //if (n.getPrecursor().actionIsInvalid())
        //    return true;
        
        /*prune any nodes which have fluents set in the successor that are not set in the
         *precursor. */
        //if (n.hasFluentWildcardInPrecursorOnly())
        //    return true;
        
        /*if the rule doesn't match with anything in the data then remove it*/
        if (heuristicEvaluationFunction(n) == 0) {
            return true;
        }
        
        return false;
    }
    
    /*Return the subset of the candidates contained in the current transaction.
     *Note: this is a node list of references to the original rule nodes
     */
    protected NodeList subset(NodeList candidates, RuleElements precursor, RuleElements postconditions) {
        NodeList subsetCandidates = new NodeList();
        for (int i = 0; i < candidates.size(); i++) {
            RuleNode node = candidates.get(i);
            if (precursor.equals(node.getPrecursor())) {
                if (postconditions.equals(node.getSuccessor())) {
                    subsetCandidates.add(node);
                }
            }
        }
        return subsetCandidates;
    }
    
}
        
