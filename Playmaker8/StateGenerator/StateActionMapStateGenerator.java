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
    
    static final boolean outputLog = Logging.LogFile.OUTPUT_LOG0;
    
    /** Creates new StateGenerator */
    public StateActionMapStateGenerator(StateActionMap stateActionMap){
        this.stateActionMap = stateActionMap;
    }
    
    public ArrayList generateNextStates(  Percep percep,   Action action) {
       
        LogFile logfile;
        
        ArrayList statesAndCounts = stateActionMap.getStatesAndCounts(percep, action);
        
        if (statesAndCounts == null) {
            if (outputLog) {
                logfile = new LogFile(1);
                logfile.print("\nWARNING: generate next states produced a NULL result");
                logfile.close();
            }
            return null;
        }
        
        ArrayList statesAndProbs = convertStatesAndCountsToProbabilities(statesAndCounts);
        
        if (outputLog) {
            logfile = new LogFile(1);
            if (statesAndCounts != null) {
                
                logfile.print("\nThe states and probabilities from stateActionMap are...\n===========\n===========\n");
                  
                //Print out the states after we filter
                double totalProb = 0.0f;
                for (int i = 0; i < statesAndProbs.size(); i++) {
                    logfile.print(((StateAndProb)statesAndProbs.get(i)).getPercep().getString());
                    logfile.print(" " + ((StateAndProb)statesAndProbs.get(i)).getProbability());
                    totalProb += ((StateAndProb)statesAndProbs.get(i)).getProbability();
                    logfile.print("\n");
                }
                logfile.print("\n Total prob: " + totalProb + "\n");
                if (totalProb < 1.0f) {
                    logfile.print("WARNING. TOTAL PROB IS " + totalProb + "\n");
                }

                logfile.print("\n===========\n===========\n=FINISHED==");
            } else {
                logfile.print("\n===========\nNO MATCHING ACTION FOR THIS STATE\n=========");
            }
            
            logfile.close();
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
