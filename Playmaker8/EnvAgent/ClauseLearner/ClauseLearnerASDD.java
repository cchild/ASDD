package EnvAgent.ClauseLearner;

import EnvAgent.RuleLearner.*;

import EnvModel.*;
import Logging.*;

import java.util.*;
import EnvAgent.*;

public class ClauseLearnerASDD extends ClauseLearner {
    
    //protected static int LARGE_ITEMSET_MINIMUM = 1;
    protected static int MINSUP = 1;
    
    protected static final float APRIORI_G = 0.445f; //threshold which G(d1,d2,H) must exceed before d1 and d2 are considered
                                                    //to be different dependencies.
                                                    //3.84 gives 5% statistical significance
                                                    //0.445 gives 50% statistical significance
     
    public boolean USE_SET_BASED_APRIORI_COUNTING;
    /** Creates new RuleLearner */
    public ClauseLearnerASDD(Percep agentPercep, Action agentAction, PercepRecord percepRecord, ActionRecord actionRecord) {
        super(percepRecord, actionRecord);

        USE_SET_BASED_APRIORI_COUNTING = true;

        if (percepRecord.size() > 20000) {
            if (FINAL_G <= 2.706f)
                 System.out.print("\n*****STOP******STGOP\nFINAL_G SET FOR < 20k EVIDENCE at 2.706");
             if (APRIORI_G <= 0.445f)
                System.out.print("\n*****STOP******STGOP\nAPRIORI SET FOR < 20k EVIDENCE at 0.445");
        } else {
            if (FINAL_G > 2.706f)
                 System.out.print("\n*****STOP******STGOP\nFINAL_G SET FOR > 20k EVIDENCE at > 2.706");
            if (APRIORI_G > 0.445f)
                System.out.print("\n*****STOP******STGOP\nAPRIORI SET FOR > 20k EVIDENCE at > 0.445");
        }



    }
    
    public ClauseList learnClauses() {
        
        ClauseList learnedClauses;
        
        Date start = new Date();
        long startTime = start.getTime();
         
        learnedClauses = learnClausesApriori();
          
        ClauseList level1Clauses = learnedClauses.findLevel1Clauses();
        
        Date finish = new Date();
        long elapsedTime = finish.getTime() - startTime;
        
      
        
        Singleton logfile2 = Singleton.getInstance();
        logfile2.print("\n PredASDD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.",2);
        System.out.print("\n PredASDD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.");
         
        
        Singleton logfile = Singleton.getInstance();
        logfile.print("\nThe pre-filtered set of rules is...\n===========\n===========\n",1);
        learnedClauses.toLogFile(logfile);
        logfile.print("\n===========\n===========",1);
        //Now filter the dependencies
        //This is called filter(D, H, n, g) in the paper
        int n = 1; //parameter indicating degree to which dependencies with low frequency of 
                   //co-occurance should be removed. In this case just min number of occurences
        
        Date startFilter = new Date();
        startTime = startFilter.getTime();
        
        learnedClauses = filter(learnedClauses, n, FINAL_G, level1Clauses);
        
        Date finishFilter = new Date();
        elapsedTime = finishFilter.getTime() - startTime;
        
        
        logfile.print("\n PredASDD FILTER IN: " + elapsedTime + " MILLISECONDS.",1);
        System.out.print("\n predASDD FILTER IN: " + elapsedTime + " MILLISECONDS.");   
        
        for (int i = 0; i < learnedClauses.size(); i++) {
            //Not sure we should have to do this, but just in case the values are not set for some reason
            //It won't take any time if they'returnalready set anyway
            countPrecursorequals(learnedClauses.get(i), level1Clauses);
            countDatabaseOccurrences(learnedClauses.get(i), level1Clauses);
        }
        
        logfile.print("\nThe post-filtered sorted set of rules is...\n===========\n===========\n",1);
        logfile.print(learnedClauses.toString(),1);
        logfile.print("\n===========\n===========\n=FINISHED==",1);
         
        
        return learnedClauses;
    }
    
    public ClauseList learnClausesApriori(){
        
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
        ClauseList LDummy = new ClauseList();  
        L.add(LDummy); //Just add a dummy node list in L0 to keep terms simple
    
        
        //2)
        for (int k=1; ((ClauseList)L.get(k-1)).size() != 0 || k == 1; k++) {
            System.out.print("\nAriori Clauses Level: " + k + "Running garbage collect");
            if (k==7) {
                int stoppage = 1;
            }
            System.runFinalization();
            System.gc();
            ClauseList candidatesK;
            

            if (k ==1) {
                candidatesK = oneItemSets();
            } else {
                candidatesK = aprioriGen((ClauseList)L.get(k-1), k);
            }
            
            ClauseList level1Candidates;
            if (k ==1) { //first time round level 1 is oneItemSets
                level1Candidates = candidatesK;
                setLevel1Clauses(level1Candidates);
            }
            else
                level1Candidates = (ClauseList)L.get(1);

            //first time we need to see which examples everything occurred in
            if (k==1 || !USE_SET_BASED_APRIORI_COUNTING) {
                for (int i = 0; i < percepRecord.size() -1; i++) {
                    ClauseElements precursorFromDatabase = convertDatabaseEntryToClauseElements(i, true, level1Candidates);
                    ClauseElements postconditionsFromDatabase = convertDatabaseEntryToClauseElements(i, false, level1Candidates);

                    ClauseList candidatesSubset = subset(candidatesK, precursorFromDatabase, postconditionsFromDatabase);

                    for (int candidCount = 0; candidCount < candidatesSubset.size(); candidCount ++) {
                        candidatesSubset.get(candidCount).incrementOccurrences();
                        if (USE_SET_BASED_APRIORI_COUNTING)
                            candidatesSubset.get(candidCount).getMatchingExamples().add(i);
                    }

                    if (Math.IEEEremainder(i, 5000) == 0) {
                        System.out.print("\nAriori Clauses Level: " + k + "Running interim garbage collect 5000");
                        System.runFinalization();
                        System.gc();
                    }
                }
            }

              
            
            for (int candidCount = 0; candidCount < candidatesK.size(); candidCount ++) {
                if (candidatesK.get(candidCount).getDatabaseOccurrences() < MINSUP) {
                    if(LogFile.OUTPUT_LOG0)
                    {
                        Singleton logfile = Singleton.getInstance();
                        logfile.print("\nPruned candidate: " + candidatesK.get(candidCount).toString(),1);
                    }
                    candidatesK.remove(candidCount);
                    candidCount --;
                }
                else {
                    //We've counted the occurrences of this in the database, so set the
                    //flag saying we have
                    candidatesK.get(candidCount).setDatabaseOccurrencesCounted(true);
                    if (candidatesK.get(candidCount).getClauseHead() == null) {
                        //This rule element has non-instantiated successor so
                        //set precursor equals to database occurences
                        candidatesK.get(candidCount).setPrecursorequals(candidatesK.get(candidCount).getDatabaseOccurrences());
                    }
                    
                    if (candidatesK.get(candidCount).getBodySize() == 0)
                        candidatesK.get(candidCount).setPrecursorequals(percepRecord.size()-1);
                }
            }
            if(LogFile.OUTPUT_LOG0)
                 
            
            /*Not sure if we should do this at level 2 as well, but let's see what it does
            (This is becuase the precursor will have no non-wildcards at level 2
            *not quite sure of this).
            */
          
            //This is the setting of the gStatistic for apriori filter;
            //It should be more sensitive than the usuual gStatistic as there
            //may be rules at greater depth which have more significance
            //GSTATISTIC


            if (k > 3) {
               System.out.print("\nJust before aproiori move");
                //candidatesK = aprioriFilter(candidatesK, (ClauseList)L.get(k-3), APRIORI_G, (ClauseList)L.get(1));
            
                //Need to add afunction here to remove level k-1 rules which
                //are entirelyy covered by k level rules
                //but we also need to check that the rules are not subsumed
                //at the final g level!!!
                aprioriRemoveGeneralCoveredBySpecific(candidatesK, (ClauseList)L.get(k-1), (ClauseList)L.get(2), FINAL_G);  
            }

          

            System.out.print("\nCandidates Level: " + k + " size is: " + candidatesK.size());
            L.add(candidatesK);
            System.out.print("\nCandidates Level: " + k + " size is: " + candidatesK.size());

            
            for (int i = 0; i < candidatesK.size(); i ++) {
                if(LogFile.OUTPUT_LOG0)
                {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\n Added L" + k +  " candidate: " +candidatesK.get(i).toString(),1);
                }
            }
            
        }
        
        //Now put all the candidates together
        ClauseList learnedClauses = new ClauseList();
        for (int itemsLoop = 0; itemsLoop < L.size(); itemsLoop++) {
            ClauseList lNodes = (ClauseList)L.get(itemsLoop);
            for (int nodesLoop = 0; nodesLoop < lNodes.size(); nodesLoop ++) {
                learnedClauses.add((ClauseNode)lNodes.get(nodesLoop));
            }
        }



        return learnedClauses;
    }
    
    /*Take a set of level L-1 candidates and return level L candidates*/
    protected ClauseList aprioriGen(ClauseList Lminus1Candidates, int lDepth) {
        
        
        ClauseList candidatesK = new ClauseList();

        Lminus1Candidates.sortIncreasingLastElement();

          
        if(LogFile.OUTPUT_LOG0)
        {
            Singleton logfile1 = Singleton.getInstance();
        }
        //First: the join step
        for (int xCount = 0; xCount < Lminus1Candidates.size(); xCount ++) {

           if (Math.IEEEremainder(xCount, 600) == 0) {
                System.out.print("\nArioriGen XCount rem 200 Cleanup: " + xCount);
                System.runFinalization();
                System.gc();
            }



            HashSet xMatchingExamples = null;
            if (USE_SET_BASED_APRIORI_COUNTING)
                xMatchingExamples = Lminus1Candidates.get(xCount).getMatchingExamples();

            ClauseElements xElements = Lminus1Candidates.get(xCount).getClauseElements();

            //Candidates are now ordered to the min valued last element always comes first
            for (int yCount = xCount+1; yCount < Lminus1Candidates.size(); yCount++) {
                if (xCount != yCount && !(Lminus1Candidates.get(yCount).getClauseElements().getHead() != null && Lminus1Candidates.get(yCount).getProbability() == 1)) {

                /*if (Math.IEEEremainder(yCount, 400) == 0) {
                    System.out.print("\nArioriGen YCount rem 400 Cleanup: " + xCount);
                    System.runFinalization();
                    System.gc();
                }*/
                    
                    ClauseElements yElements = Lminus1Candidates.get(yCount).getClauseElements();

                    boolean generateCandidate = true;
                    
                    //we don't generate a candidate if the ID of the last element in x is greater/equal to y
                    if (xElements.getLastElementID()
                            >= yElements.getLastElementID()) {
                        generateCandidate = false;
                    }

                    //We don't generate successors if the probability of the outcome is already 1
                    //becasue they would just be the pruned later
                    if (xElements.getHead() != null && Lminus1Candidates.get(xCount).getProbability() == 1)
                         generateCandidate = false;
                    
                    if (generateCandidate) {

                        //don't need to do this now as it's handled furter up
                        //xElements.orderByUniqueID(null);
                        //yElements.orderByUniqueID(null);
                        
                        
                        //if both have heads then they can't be combined
                        if (((xElements.getHead() != null) && (yElements.getHead() != null))) {
                            generateCandidate = false;
                        } else {
                            int loopLength = xElements.size();
                            if (loopLength < yElements.size())
                                loopLength = yElements.size();
                            for (int i = 0; i < loopLength - 1; i++) {
                                if (xElements.get(i).getUniqueID() != yElements.get(i).getUniqueID())
                                    generateCandidate = false;
                            }
                        }
 
                        if (generateCandidate) {
                            ClauseElements newCandidate = (ClauseElements)xElements.clone();
                            //add the last element from yElements (either head or last element)
                            if (yElements.getHead() == null)
                                newCandidate.add((Term)yElements.get(yElements.size() -1).clone());
                            else
                                newCandidate.addHead((Term)yElements.getHead().clone());                        

                            newCandidate.orderByUniqueID(null);
                            //If the new sucessor has a consequence then one of the generating rules will be the precursor, so we can
                            //set the precursor equals from the generating rule

                            //for (int s = 0; s < candidatesK.size(); s++) {
                            //    ClauseNode oldCandidate = (ClauseNode)candidatesK.get(s);
                            //    if (oldCandidate.getClauseElements().isEqualTo(newCandidate)) {
        
                          //          System.out.print("arse. generated duplicate candidate");
                           //     }
                           // }
                            
                            ClauseNode newNode = new ClauseNode(newCandidate);
                            candidatesK.add(newNode);
                           

                             newNode.setMother(Lminus1Candidates.get(xCount));
                             newNode.setFather(Lminus1Candidates.get(yCount));

                             if (USE_SET_BASED_APRIORI_COUNTING) { //set to ture if //USE_SET_BASED_APRIORI_COUNTING {
                                 //Lminus1Candidates.get(yCount).setMatchingExamples(Lminus1Candidates.get(yCount).getMatchingExamples());
                                 HashSet intersection = (HashSet)Lminus1Candidates.get(yCount).getMatchingExamples().clone();


                                 intersection.retainAll(xMatchingExamples);

                                 if (intersection.size() > MINSUP) {
                                    newNode.setDatabaseOccurrences(intersection.size());

                                    if (newNode.getClauseHead() != null) {
                                        if (xElements.getHead() == null) {
                                            newNode.setPrecursorequals(Lminus1Candidates.get(xCount).getDatabaseOccurrences());
                                        } else {
                                            newNode.setPrecursorequals(Lminus1Candidates.get(yCount).getDatabaseOccurrences());
                                        }
                                    }
                                 }

                                 //keep the level 2 ones because it'll speed things up
                                 if (lDepth == 2) {
                                    newNode.setMatchingExamples(intersection);
                                 }
                            }
                             if(LogFile.OUTPUT_LOG0)
                             {
                                Singleton logfile = Singleton.getInstance();
                                 logfile.print("\nGenerated candidate is " + newNode.toString(),1);
                             }
                        }
                    }
                 
                }
            }
            //We will not need matching examples from this anymore as the yCount means
            //these will not be compared
            //Remove the xCount example - not needed again.
            if (lDepth > 3) { //but don't remove level 1 candidates
                //actually - keep the level two ones as well
                Lminus1Candidates.get(xCount).setMatchingExamples(null);
            }
        }

      
       
        
        /*lDepth > 2 because apriori prune will have no effect on depth 2
         *candidates and apriori filter should probably be left until level 3
         */
      
        //if (lDepth > 2) {
            //Second: the prune step
        //    candidatesK = aprioriPrune(candidatesK, Lminus1Candidates);

          
        //}

        if (lDepth > 3) {
                //we don't need the matching examples any more so save space by
                //removing
            for (int i = 0; i < Lminus1Candidates.size(); i++)
                Lminus1Candidates.get(i).setMatchingExamples(null);
        }

        
  
        return candidatesK;
    }
    
    protected ClauseList aprioriPrune(ClauseList candidatesK, ClauseList Lminus1Candidates) {
        
          
        
        
        for (int candidateCount = 0; candidateCount < candidatesK.size(); candidateCount ++) {
            //subsets of rules of this form are just the rule with a wildcard in each
            //of the instntiated non-wildcard positions
            ClauseList candidateSubsets = subsetsOf(candidatesK.get(candidateCount));
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
                if (LogFile.OUTPUT_LOG0)
                {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\nRemoved by Apriori prune: " + candidatesK.get(candidateCount).toString(),1);
                }
                candidatesK.remove(candidateCount);
                candidateCount --;
            } else {
                if (LogFile.OUTPUT_LOG0)
                {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\nKEPT after Apriori prune: " + candidatesK.get(candidateCount).toString(),1);
                }
            }
        }

        
             
        return candidatesK;
    }
    
    
    
    protected ClauseList aprioriFilter(ClauseList candidatesK, ClauseList Lminus1Candidates, float g, ClauseList level1Candidates) {
        
        /*Filter Specific node with the the more general nodes in nodes
        */
        
        //We can check a whole list at one
        //We must also check that both rules have the same consequence
        //SUBSUMES: filtering in general we should check the effects match first to save a lot of matching
        
        Date startFilter = new Date();
        long startTime = startFilter.getTime();
        
        ClauseList Lminus1CandidatesWithHead = new ClauseList();
        
        for (int loop = 0; loop < Lminus1Candidates.size(); loop ++) {
            if (Lminus1Candidates.get(loop).getClauseHead() != null) {
                Lminus1CandidatesWithHead.add(Lminus1Candidates.get(loop));
            }
        }
        
        /*for (int candidateLoop = (candidatesK.size() -1); candidateLoop >= 0; candidateLoop --) {
            ClauseNode node = candidatesK.get(candidateLoop);
            if (candidatesK.get(candidateLoop).getClauseHead() != null)
                if  (filterSpecific(node, Lminus1CandidatesWithHead, g, level1Candidates))
                    candidatesK.remove(candidateLoop);
        }*/
        
        
        //Singleton logfile = Singleton.getInstance();
        
        for (int lm1Loop =0; lm1Loop < Lminus1CandidatesWithHead.size(); lm1Loop ++) {

            ClauseNode s = Lminus1CandidatesWithHead.get(lm1Loop);
            
            for (int i = 0; i < candidatesK.size(); i++) {
                ClauseNode d = candidatesK.get(i);
                if (d.getClauseHead() != null) {
                    if (s.subsumes(d))  {
                        float gStat = Gstatistic(s,d, level1Candidates);
                        //logfile.print("\n Trying to  APRIORI filter " + d.toString()+ " generalised by " + s.toString() + " GStat: " + gStat);
                        if (gStat < g) {
                            //logfile.print("\n APRIORI filtered " + d.toString()+ " generalised by " + s.toString());
                            if (!d.isActionUnchangedOutput()) //don't filter potential fram rules
                            {
                                candidatesK.remove(i);
                                i--;
                            } else {
                                System.out.print("/nDidn't remove fram rule " + d.toString());
                            }
                        }
                    }
                }
            }
        }
        
        // 
        
        Date finishFilter = new Date();
        long elapsedTime = finishFilter.getTime() - startTime;
        
        Singleton logfile2 = Singleton.getInstance();
        
        logfile2.print("\n FILTER SPECIFIC IN PROCESS: " + elapsedTime + " MILLISECONDS.",2);
        
        
        return candidatesK;
    }
    
    /*This function removes rules general rules at level k-1 which are covered by rules
     *at level k. the level 2 candidates are required to test whether all possible 
     *values of an input are covered*/
    void aprioriRemoveGeneralCoveredBySpecific(ClauseList candidatesK, ClauseList kMinus1Nodes, ClauseList level2Dependencies, float gStatistic) {
        
    }
    
    protected ClauseList subsetsOf(ClauseNode node) {
        ClauseList subsets = new ClauseList();
        for (int precursorLoop = 0; precursorLoop < 2; precursorLoop ++) {
            
            //first deal with all the body elements
            if (precursorLoop == 0) {
                for (int positionsLoop = 0; positionsLoop < node.getClauseElements().size(); positionsLoop ++) {
                     ClauseNode newClause = (ClauseNode)node.clone();
                     newClause.getClauseElements().remove(positionsLoop);
                     subsets.add(newClause);
                }
            } else {
                if (node.getClauseHead() != null) {
                    //otherwise leave the body and just add or remove the head
                    ClauseNode newClause = (ClauseNode)node.clone();
                    newClause.getClauseElements().removeHead();
                    subsets.add(newClause);
                }
            }
            
        }
        
        return subsets;
    }
    
    protected ClauseList oneItemSets() {
            
        System.out.print("\nWe have to do something here to define unique IDs for new one item sets\n");
        
        ClauseList candidatesK = new ClauseList();
        //We should do this by matching with the database, but can
        //probably achieve more quickly using expand() for the moment.
        
        for (int i = 0; i < percepRecord.size() -1; i++) {   
            
            //Debug code
            //Percep p = actionRecord.getAction(i);
            //Percep p2 = percepRecord.getPercep(i);
            //Action a = percepRecord.getPercep(+1);    
            //if (a.toString().equals("[KILL]")) {
            //    int whatever =1;
            //}
            
            ClauseElements precursorClause = convertDatabaseEntryToClauseElements(i, true, null);
            ClauseElements successorClause = convertDatabaseEntryToClauseElements(i, false, null);
            
            //more debug
            //String action = precursorClause.toString().substring(0, 15);
            //if (action.equals("Act(KillAct(K))")) {
            //    int stopystop = 1;
            //}
            
            //more debug
            //precursorClause = convertDatabaseEntryToClauseElements(i, true, null);
            //successorClause = convertDatabaseEntryToClauseElements(i, false, null);
            //precursorClause = convertDatabaseEntryToClauseElements(i, true, null);
            //successorClause = convertDatabaseEntryToClauseElements(i, false, null);
            
            //System.out.print("\n Precursor: " + precursorClause.toString());
            //System.out.print("\n Successor: " + successorClause.toString());
            
            for (int precLoop = 0; precLoop < precursorClause.size(); precLoop ++) {
                ClauseElements newClause = new ClauseElements();
                newClause.add((Term)precursorClause.get(precLoop).clone());
       
                if (!candidatesK.contains(newClause)) {
                    newClause.get(0).setUniqueID();
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\n Added L1 candidate: " + newClause.toString(),1);
                     
                    candidatesK.add(new ClauseNode(newClause));
                }
            }
            
            for (int succLoop = 0; succLoop < successorClause.size(); succLoop ++) {
                ClauseElements newClause = new ClauseElements();
                newClause.addHead((Term)successorClause.get(succLoop).clone());
                if (!candidatesK.contains(newClause)) {
                    newClause.getHead().setUniqueID();
                    Singleton logfile = Singleton.getInstance();
                    logfile.print("\n Added L1 candidate: " + newClause.toString(),1);
                     
                    candidatesK.add(new ClauseNode(newClause));
                }
            }
        }
        return candidatesK;
    }
    
   
    
 
    /*Return the subset of the candidates contained in the current transaction.
     *Note: this is a node list of references to the original rule nodes
     */
    protected ClauseList subset(ClauseList candidates, ClauseElements precBody, ClauseElements postBody) {
        ClauseList subsetCandidates = new ClauseList();
        for (int i = 0; i < candidates.size(); i++) {
            ClauseNode node = candidates.get(i);
            if (node.bodyequals(precBody)) {
                if ((node.getClauseHead() == null) || postBody.contains(node.getClauseHead())) {
                    subsetCandidates.add(node);
                }
            }
        }
        return subsetCandidates;
    }
    
}
        
