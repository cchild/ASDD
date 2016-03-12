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
import EnvAgent.StateActionMap.*;
import EnvAgent.RuleLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class StateActionMapStateGenerator extends StateGenerator {

    StateActionMap stateActionMap;
    
    static final boolean outputLog = Logging.LogFiles.OUTPUT_LOG0;
    
    /** Creates new StateGenerator */
    public StateActionMapStateGenerator(StateActionMap stateActionMap){
        this.stateActionMap = stateActionMap;
    }
    
    public ArrayList generateNextStates(  Percep percep,   Action action) {
        ArrayList statesAndCounts = stateActionMap.getStatesAndCounts(percep, action); 
        if (statesAndCounts == null) {
            if (outputLog) {
                LogFiles logfile = LogFiles.getInstance();
                logfile.print("\nWARNING: generate next states produced a NULL result",1);
            }
            return null;
        }
        ArrayList statesAndProbs = convertStatesAndCountsToProbabilities(statesAndCounts);
        
        if (outputLog) {
            LogFiles logfile = LogFiles.getInstance();
            if (statesAndCounts != null) {
                
                logfile.print("\nThe states and probabilities from stateActionMap are...\n===========\n===========\n",1);
                  
                //Print out the states after we filter
                double totalProb = 0.0f;
                for (int i = 0; i < statesAndProbs.size(); i++) {
                    logfile.print(((StateAndProb)statesAndProbs.get(i)).getPercep().toString(),1);
                    logfile.println(" " + ((StateAndProb)statesAndProbs.get(i)).getProbability(),1);
                    totalProb += ((StateAndProb)statesAndProbs.get(i)).getProbability();
                    
                }
                logfile.print("\n Total prob: " + totalProb + "\n",1);
                if (totalProb < 1.0f) {
                    logfile.print("WARNING. TOTAL PROB IS " + totalProb + "\n",1);
                }

                logfile.print("\n===========\n===========\n=FINISHED==",1);
            } else {
                logfile.print("\n===========\nNO MATCHING ACTION FOR THIS STATE\n=========",1);
            }
            
             
        }
        
        return statesAndProbs;
    }
    
    public ArrayList convertStatesAndCountsToProbabilities(ArrayList statesAndCounts) {
        ArrayList statesAndProbs = new ArrayList();
        for (int i = 0; i < statesAndCounts.size(); i++) {
            StateAndCount stateAndCount = (StateAndCount)statesAndCounts.get(i);
            statesAndProbs.add(new StateAndProb(stateAndCount.getPercep(), (float)stateAndCount.getCount()));
        }
        
       normaliseProbabilitiesOfGeneratedStates(statesAndProbs);
       
       return statesAndProbs;
    }
    
    public Percep pickRandomPercep() {
        return stateActionMap.pickRandomPercep();
    }
}
