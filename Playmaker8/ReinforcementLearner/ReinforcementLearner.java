/*
 * ReinforementLearner.java
 *
 * Created on 27 February 2003, 18:00
 */

package ReinforcementLearner;

import java.util.*;

import EnvModel.*;
import EnvModel.PredatorModel.*;
import EnvAgent.*;
import EnvAgent.RuleLearner.*;
import Model.*;

import Logging.*;

import StateGenerator.*;

/**
 *
 * @author  Chris Child
 * @version 
 */

/*pass acts as store for states and utilities.
 *At the moment it will implement value learning only. This means
 *that pass it a state and the utility and reward for the next state.
 *No action is required in value learning.*/

public class ReinforcementLearner extends java.lang.Object {
    
    public int learningType;
    public static final int NOT_SET = 0, VALUE_LEARNING = 1, CLAUSE_LEARNING = 2;
    
    public static final double DISCOUNT_FACTOR = 0.9f;
    /** Creates new ReinforementLearner */
    
    protected StateValueMap valueMap;
    protected StateActionValueMap stateActionValueMap;
    
    protected StateGenerator stateGenerator;
    protected boolean useStateGenerator;
    protected SimModel simModel;
    protected boolean useSimModel;
    
    public ReinforcementLearner() {
        learningType = VALUE_LEARNING;
        valueMap = new StateValueMap();
        stateActionValueMap = new StateActionValueMap();
        stateGenerator = null;
        simModel = null;
        useStateGenerator = false;
        useSimModel = false;
    }
    
    public void setStateGenerator(StateGenerator stateGenerator) {
        this.stateGenerator = stateGenerator;
        useStateGenerator = true;
        useSimModel = false;
    }
    
    public StateGenerator getStateGenerator() {
        return stateGenerator;
    }
    
    public void setSimModel(SimModel simModel) {
        this.simModel = simModel;
        useSimModel = true;
        useStateGenerator = false;
    }
    

    
    public Action getBestQAction(Percep percep) {
        return stateActionValueMap.getBestAction(percep);
    }
    
    public Action getBestBellmanAction(Percep percep, Action anAction) {
       //first element of rule elements is the action
       Action action = (Action)anAction.clone();
       double maxUtility = -100000.0f;
       int maxAction = action.getNumValues();

       
       for (int whichAction = 0; whichAction < action.getNumValues(); whichAction ++) {
           
           //if this is not an action the agent is allowed to take then go to the next action
           action.setByValue(whichAction);
           if (!action.legalAction())
               continue;
                 
           ArrayList nextStatesAndProb = stateGenerator.generateNextStates(percep, action);
            
           if (nextStatesAndProb != null) {
               double actionUtility = 0.0f;
               for (int afterState = 0; afterState < nextStatesAndProb.size(); afterState ++) {
                   StateAndProb stateAndProb = (StateAndProb)nextStatesAndProb.get(afterState);
                   StateValue stateValue = valueMap.getOrCreateStateValue(stateAndProb.getPercep());
                   actionUtility += 
                        stateAndProb.getProbability() * 
                            (stateValue.getValue() * DISCOUNT_FACTOR + stateValue.getReward());
                }

                if (actionUtility > maxUtility) {
                    maxUtility = actionUtility;
                    maxAction = whichAction;
                }
           }

           
        }

       if (maxAction == action.getNumValues()) {
           int stopperit = 1;
           action.randomAction();
        } else {
           action.setByValue(maxAction);
        }
        
        
        if (LogFiles.OUTPUT_LOG0) {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\nFind action for state: ",1);
            logfile.print(percep.toString(),1);
            logfile.print("\nAction:" + action.toString(),1);
            //logfile.print("\n===========\n===========\n=FINISHED==");
             
        }
        
        return action;
    }
    
   public void bellmanRefineValue(Percep percep, Action anAction) { 
       //first element of rule elements is the action
       double maxUtility = 0.0f;
       int maxAction = anAction.getNumValues();
       Action action = (Action)anAction.clone();
       for (int whichAction = 0; whichAction < action.getNumValues(); whichAction ++) {
           
           //if this is not an action the agent is allowed to take then go to the next action
           action.setByValue(whichAction);
           if (!action.legalAction())
               continue;
           
             
           boolean outputLog = Logging.LogFiles.OUTPUT_LOG0;
           
           if (outputLog) {
               LogFiles logfile = LogFiles.getInstance();
               logfile.print("\nThe state to be refined is...\n===========\n===========\n",1);
               logfile.println(percep.toString() + " " + action.toString(),1);
               
                
           }
           
           ArrayList nextStatesAndProb = stateGenerator.generateNextStates(percep, action);
           
           if (nextStatesAndProb == null) {
               if (outputLog) {
                   LogFiles logfile = LogFiles.getInstance();
                   logfile.print("\nThe state generator could not generate and states for: ",1);
                   logfile.println(percep.toString() + " " + action.toString(),1);
                   
                    
               }
               continue;
           }
           
           double actionUtility = 0.0f;
           for (int afterState = 0; afterState < nextStatesAndProb.size(); afterState ++) {
               StateAndProb stateAndProb = (StateAndProb)nextStatesAndProb.get(afterState);
               //System.out.print("\nPercep in nextStates and Prob :" + stateAndProb.getPercep().getString());
               StateValue stateValue = valueMap.getOrCreateStateValue(stateAndProb.getPercep());    
               actionUtility += 
                    stateAndProb.getProbability() * 
                        (stateValue.getValue() * DISCOUNT_FACTOR + stateValue.getReward());
            }
            
            if (actionUtility > maxUtility) {
                maxUtility = actionUtility;
                maxAction = whichAction;
            }
        }
       
        valueMap.setStateValue(percep, maxUtility);
    }
   
  
    //Q-Learning is model free, so we need to know the next (sample) state as well as the current state.
    //It also uses a StateActionValue map, rather than a state vale map.
    //As this is model based learning the updates only happen gradualy.
    public void QLearningRefineValue(Percep stateToRefine, Action action, Percep nextState) {
         //first element of rule elements is the action
        double ALPHA = 0.1f;
        double GAMMA = 0.9f;
        
        double oldStateActionValue = 0;
        if (stateActionValueMap.getStateActionValue(stateToRefine, action) != null)
            oldStateActionValue = stateActionValueMap.getStateActionValue(stateToRefine, action).getValue();
        
        //minor frig because reward is in the state actions rather than observed
        StateActionValue sa = stateActionValueMap.getStateActionValue(nextState, action);
        if (sa == null) {
            //create it, and safe a bit of memory by adding it to the table so we don't keep recreating it
            stateActionValueMap.setStateActionValue(nextState, action , 0);
            sa = stateActionValueMap.getStateActionValue(nextState, action);
        }
        double reward = sa.getReward();
        
        double newStateActionValue = 0;
        Action maxAction = stateActionValueMap.getBestAction(nextState);
        StateActionValue sav = null;
        
        //if (maxAction != null) {
            //now use e-greedy policy
            //note we have to use action rather than max action to avoid screwig up maxAction
        //    if (Math.random() > 0.9f) {
        //        action.setByValue((int)(action.getNumValues() * Math.random()));
        //        while (!action.legalAction())
        //            action.setByValue((int)(action.getNumValues() * Math.random()));
        //        sav = stateActionValueMap.getStateActionValue(nextState, action);
        //    } else {
                sav = stateActionValueMap.getStateActionValue(nextState, maxAction);
        //    }
            
            
        //}
        
        double maxActionValue = 0;
        if (sav != null)
            maxActionValue = sav.getValue();
        
        newStateActionValue = oldStateActionValue + 
                                ALPHA *
                                (reward + GAMMA * maxActionValue - oldStateActionValue);
        
        stateActionValueMap.setStateActionValue(stateToRefine, action , newStateActionValue);
    }
    
    
     //Q-Learning is model free, so we need to know the next (sample) state as well as the current state.
    //It also uses a StateActionValue map, rather than a state vale map.
    //As this is model based learning the updates only happen gradualy.
    public void DynaQLearningRefineValue(Percep stateToRefine, Action action, ArrayList nextStatesAndProbs) {
         //first element of rule elements is the action
        double ALPHA = 1.0f;
        double GAMMA = 0.9f;
        
        double oldStateActionValue = 0;
        if (stateActionValueMap.getStateActionValue(stateToRefine, action) != null)
            oldStateActionValue = stateActionValueMap.getStateActionValue(stateToRefine, action).getValue();
        
      
        
        double totalMaxStateActionValue = 0;
        double newStateActionValue = 0;
        double reward = 0;
        for (int i = 0; i < nextStatesAndProbs.size(); i++) {
               //minor frig because reward is in the state actions rather than observed
            StateActionValue sa = stateActionValueMap.getStateActionValue(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), action);
            if (sa == null) {
                //create it, and safe a bit of memory by adding it to the table so we don't keep recreating it
                stateActionValueMap.setStateActionValue(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), action , 0);
                sa = stateActionValueMap.getStateActionValue(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), action);
            }
            double thisReward = sa.getReward();
            reward += thisReward * ((StateAndProb)nextStatesAndProbs.get(i)).getProbability();
   
              
            double probStateActionValue = 0;
            Action maxAction = stateActionValueMap.getBestAction(((StateAndProb)nextStatesAndProbs.get(i)).getPercep());
           
            StateActionValue sav = stateActionValueMap.getStateActionValue(((StateAndProb)nextStatesAndProbs.get(i)).getPercep(), maxAction); 
   
            double maxActionValue = 0;
            if (sav != null) {
                maxActionValue = sav.getValue();
                probStateActionValue = maxActionValue * ((StateAndProb)nextStatesAndProbs.get(i)).getProbability();
                
            }
            totalMaxStateActionValue += probStateActionValue;
        }
        
        newStateActionValue = oldStateActionValue + 
                                ALPHA *
                                (reward + GAMMA * totalMaxStateActionValue - oldStateActionValue);
        
        stateActionValueMap.setStateActionValue(stateToRefine, action , newStateActionValue);
    }
   
   
    
     public Percep pickRandomState() {
         if (valueMap.size() != 0)
            return valueMap.pickRandomState();
         else
             return stateActionValueMap.pickRandomState();
    }
    
    public void setValueMap(StateValueMap valueMap) {
        this.valueMap = valueMap;
    }
    
    public StateValueMap getValueMap() {
        return valueMap;
    }
    
    public StateActionValueMap getStateActionValueMap() {
        return stateActionValueMap;
    }
    
    public void setStateActionValueMap(StateActionValueMap stateActionValueMap) {
        this.stateActionValueMap = stateActionValueMap;
    }
}
