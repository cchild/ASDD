package EnvAgent.RuleLearner;

import EnvModel.*;
import Logging.*;

import java.util.*;
import java.sql.Time;
import EnvAgent.*;


public class RuleLearnerMSDD extends RuleLearner {
    
  
    
    /** Creates new RuleLearner */
    public RuleLearnerMSDD(Percep agentPercep, Action agentAction, PercepRecord percepRecord, ActionRecord actionRecord) {
        super(percepRecord, actionRecord);
    }
    
    public NodeList learnRules()
    {
        Date start = new Date();
        long startTime = start.getTime();
       
        NodeList learnedRules;
        
        learnedRules = learnRulesMSDD();
          
        Date finish = new Date();
        long elapsedTime = finish.getTime() - startTime;
        
        LogFile logfile1 = new LogFile(1);
        logfile1.print("\n MSDD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.");
        System.out.print("\n MSDD LEARNED RULES IN: " + elapsedTime + " MILLISECONDS.");
        logfile1.close();
         
        LogFile logfile = new LogFile();
        logfile.print("\nThe pre-filtered set of rules is...\n===========\n===========\n");
        logfile.print(learnedRules.toString());  
        logfile.print("\n===========\n===========");
        logfile.close();
        //Now filter the dependencies
        //This is called filter(D, H, n, g) in the paper
        int n = 1; //parameter indicating degree to which dependencies with low frequency of 
                   //co-occurance should be removed. In this case just min number of occurences
        
         
        Date startFilter = new Date();
        startTime = startFilter.getTime();
        
        learnedRules = filter(learnedRules, n, FINAL_G);
        
        Date finishFilter = new Date();
        elapsedTime = finishFilter.getTime() - startTime;
        
        logfile1 = new LogFile(1);
        logfile1.print("\n MSDD FILTER IN: " + elapsedTime + " MILLISECONDS.");
        System.out.print("\n MSDD FILTER IN: " + elapsedTime + " MILLISECONDS.");
        logfile1.close();
        
        for (int i = 0; i < learnedRules.size(); i++) {
            //Not sure we should have to do this, but just in case the values are not set for some reason
            countPrecursorequals(learnedRules.get(i));
            countDatabaseOccurrences(learnedRules.get(i));
        }
        
        logfile.print("\nThe post-filtered sorted set of rules is...\n===========\n===========\n");
        logfile.print(learnedRules.toString());  
        logfile.print("\n===========\n===========\n=FINISHED==");
        logfile.close();
        
        return learnedRules;
    }
    
      /*Learna set of rules and return them as an array list of MSDDRuleNodes*/
    public NodeList learnRulesMSDD() {
        
        int maxnodes = 900000;
        int expanded = 0;
        
        /*Create the root RuleNode*/
        NodeList nodes = new NodeList();
        NodeList finishedRules = new NodeList(); //This stops us removing finished rules with no children
        
      
        RuleElements precursorFromDatabase = (RuleElements)convertDatabaseEntryToRuleElements(0, true).clone();
        RuleElements postconditionsFromDatabase = (RuleElements)convertDatabaseEntryToRuleElements(0, false).clone();
        
        for (int i = 0; i < precursorFromDatabase.size(); i++) {
            precursorFromDatabase.get(i).setWildcard();
            postconditionsFromDatabase.get(i).setWildcard();
        }
        
        RuleNode emptyNode = new RuleNode(precursorFromDatabase, postconditionsFromDatabase);
        
        nodes.add(emptyNode);
        
        LogFile logfile = new LogFile();
        logfile.print(nodes.get(0).toString() + "\n");
        //Main MSDD loop
        //keep looping until there are no nodes left to expand
        //or we've reached the maximum number
        while ((nodes.size() > 0) && (expanded < maxnodes)) {
            if (expanded == maxnodes -1) {
                System.out.print("\nMAXNODES REACHED\n");
            }
            //a. remove from nodes the node n that maximuxes f(H, n)
            int expandNode = 0;
            if (nodes.size() == 1)
                expandNode = 0;
            else {
                float maxVal = 0; //maximum node value
                for (int n = 0; n < nodes.size(); n++) {
                    float fVal = heuristicEvaluationFunction(nodes.get(n));
                    if (nodes.get(n).getSuccessor().hasANonWildcard() && nodes.get(n).getProbability() == 1)
                        fVal = 0;
                    if (fVal > maxVal && fVal > 0) {
                        fVal = maxVal;
                        expandNode = n;
                    }
                }
            }
            
            //b. EXPAND(n), adding its children to nodes
           
            RuleNode expand = nodes.get(expandNode);
             //now add the node to finished nodes if it has no children
            //This is a new bit from me
            finishedRules.add(expand);
            
            //remove it from the nodes to be checked
            nodes.remove(expandNode);
            NodeList children = new NodeList();
            expand(expand, children);
            
            /*This is some debug code to make sure we'return not cyclicly creating rules*/
            if (false) {//check for repeated node
                for (int i = children.size()-1; i >= 0; i--) {
                    boolean removed = false;
                    RuleNode child = children.get(i);
                    child.setParent(expand);
                    for (int j = 0; j < finishedRules.size(); j ++) {
                        RuleNode finished = finishedRules.get(j);
                        if (finished.isEqualTo(child)) {
                            logfile.print("\n repeated node " + children.get(i).toString());
                            logfile.print("\n parent was " + expand.toString());
                            logfile.print("\n previous node parent was " + finished.getParent().toString());
                            children.remove(i);
                            removed = true;
                            boolean stop = true;
                            j = finishedRules.size();
                        }
                    }
                    if (!removed) {
                        for (int j = 0; j < nodes.size(); j ++) {
                            if (nodes.get(j).isEqualTo
                                    (children.get(i))) {
                                logfile.print("/n repeated node " + children.get(i).toString());
                                logfile.print("/n parent was " + expand.toString());
                                children.remove(i);
                                boolean stop = true;
                                j = nodes.size();
                            }
                        }
                    }
                }
            }
            
            nodes.addAll(children);
           
            //c. increment "expanded" by the number of children generated in (b)
            expanded += children.size();
        }    
        
        logfile.close();
        nodes.addAll(finishedRules);
        return nodes;
    }

    private void expandPrecSucc(RuleNode n, boolean precursor, NodeList children) {
        RuleElements precSucc;
        
        LogFile logfile = null;
        if(LogFile.OUTPUT_LOG0)
        {
            logfile = new LogFile();
            logfile.print("\n");
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

               
                    if (!prune(child)) {
                        if(LogFile.OUTPUT_LOG0)
                            logfile.print("\n added " + child.toString());
                        children.add(child);
                    }
                    else{
                        if(LogFile.OUTPUT_LOG0)
                           logfile.print("\n pruned " + child.toString());
                    }
                   
                }
            }
        }
        if(LogFile.OUTPUT_LOG0)
            logfile.close();
        return;
    }
    
    /*Expand a node generating all it's children*/
    private void expand(RuleNode n, NodeList children) {
        if (!n.getSuccessor().isAllWildcardsFrom(0))
            return;
        expandPrecSucc(n, true, children);
        //if (children.size() == 0) {
            expandPrecSucc(n,false, children);
            
            /*//we're not generatng children with wildcards in the last element
            //so also create a clone with last element set to wildcard and expand those
            if (n.getSuccessor().countWildcards() == n.getSuccessor().size()) {
                RuleNode wildcardNode = (RuleNode)n.clone();
            
                RuleObject e = wildcardNode.getPrecursor().get(wildcardNode.getPrecursor().size() -1);
                e.setWildcard();
                NodeList moreChildren = new NodeList();
                expandPrecSucc(wildcardNode, false, moreChildren);
                children.addAll(moreChildren);
            }*/
        //}
    }
    
    /* This function tells us whether the given node should be pruned because it
     *doesn't fit in with environmental factors.*/
    private boolean prune(RuleNode n) {
        /*first get rid of rules with no action. These don't tell us anything about 
        *the effects of actions on the world*/
        /*if (n.getPrecursor().hasNoAction()) {
            if (n.getPrecursor().isAllWildcardsFrom(1) && (n.getSuccessor().isAllWildcardsFrom(0)))
                return false; //it's all wildcards so OK.
            else
                return true;
        }*/
        
        /*prune any actions that aren't valid*/
        if (n.getPrecursor().actionIsInvalid())
            return true;
        
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
        
}
        

