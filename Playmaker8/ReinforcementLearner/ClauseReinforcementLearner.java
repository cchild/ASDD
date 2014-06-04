/*
 * ClauseReinforcementLearner.java
 *
 * Created on 22 September 2005, 16:07
 */

package ReinforcementLearner;

import java.util.*;

import EnvModel.*;
import EnvModel.PredatorModel.*;
import EnvAgent.*;
import EnvAgent.RuleLearner.*;
import EnvAgent.ClauseLearner.*;
import Model.*;

import Logging.*;

import StateGenerator.*;

/**
 *
 * @author Chris Child
 */
public class ClauseReinforcementLearner extends ReinforcementLearner {
    
    /** Creates a new instance of ClauseReinforcementLearner */
    public ClauseReinforcementLearner() {
        learningType = CLAUSE_LEARNING;
        valueMap = null;
        stateActionValueMap = null;
        stateGenerator = null;
        simModel = null;
        useStateGenerator = false;
        useSimModel = false;
    }
    
     public void DynaQClauseLearningRefineValue(Percep agentPercep, Action action, ArrayList nextStatesAndProbs, ClauseList clauseList, double ALPHA, double GAMMA) {
        
         //first element of rule elements is the action
  
        
        double totalMaxStateActionValue = 0;
        double newStateActionValue = 0;
        double reward = 0;
        for (int i = 0; i < nextStatesAndProbs.size(); i++) {     
            //double thisReward = clauseList.getWinningMatchingClausesReward(((StateAndProb)nextStatesAndProbs.get(i)).getPercep());
            double thisReward = ((StateAndProb)nextStatesAndProbs.get(i)).getPercep().reward();
            reward += thisReward * ((StateAndProb)nextStatesAndProbs.get(i)).getProbability();
              
            double probStateActionValue = 0;
            Action maxAction = clauseList.getBestAction(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), action);
           
            double maxActionValue = clauseList.getWinningMatchingClausesValue(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), maxAction); 
   
            probStateActionValue = maxActionValue * ((StateAndProb)nextStatesAndProbs.get(i)).getProbability();
                
            totalMaxStateActionValue += probStateActionValue;
        }
        
        if (reward > 0) {
            boolean stoper = true;
            stoper = stoper;
        }
        
        clauseList.updateMatchingClausesValue(agentPercep, action, reward, totalMaxStateActionValue, ALPHA, GAMMA);
     }
     
   
    
}
