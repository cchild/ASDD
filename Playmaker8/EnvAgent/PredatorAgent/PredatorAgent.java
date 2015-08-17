package EnvAgent.PredatorAgent;

/*
 * EnvironmentObject.java
 *
 * Created on 26 February 2001, 15:18
 */
  
//package Environment;

/**
 *
 * @author  Chris Child
 * @version 
 */

import java.util.*;

import EnvModel.*;
import EnvModel.PredatorModel.*;
import EnvAgent.*;
import Logging.*;
import StateGenerator.*;
import ReinforcementLearner.*;
import EnvAgent.RuleLearner.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.StateActionMap.*;

import java.io.*;

//an agent has state which is internal to itself and can perform actions

public class PredatorAgent extends Agent {
  
    int role;
       
    public static final int
	PREDATOR = 0, PREY = 1;
    
    public PredatorAgent(PredatorAgentBody body, int role) {
        super();
        this.body = body;
        this.role = role;

        USE_SAVED_PERCEP_ACTION_RECORD = false;
        USE_REINFORCEMENT_POLICY = true;//false; 
        USE_BELLMAN = false;//true;
        USE_STATE_ACTION_LOTS = true;//false;
        USE_CLAUSE_VALUES = false;
        USE_CLAUSE_APRIORI_VALUES = false;
        USE_RULE_APRIORI_VALUES = false;

          //Note: VALUE MAPS ARE GENERATED IN_LINE WHEN THE REINFORCEMENT POLICY IS USED
        //Note: LEARN_CLAUSES_ASDD always needs to be true to access any other RL relarning
        //Note: Set USE_CLAUSE_APRIORI_VALUES to TRUE to learn value - should be set tro
        //true when leaning if needed because it is not done in-line

        //USE CLAUSE VALUES Shoult be set to FALSE unless using RVRL

        //the clauses as you can re-do it in-line for the different RVRL settings.
        //Only set it to FALSE for lerning the very big state-action maps

        //USE_CLAUSE_VALUES means use RVRL and overrides everythig else
        //USE_BELLMAN - means use the state action map
            //!USE_CLAUSE_APRIORI_VALUES && !USE_RULE_APRIORI_VALUES)
                //USE_STATE_ACTION_LOTS - means use the big one
            //USE_CLAUSE_APRIORI_VALUES && USE _BELLMAN means use the Aoriori rule generator and generate a Dyna-Q style value map using bellman
                //USE_RULE_APRIORI_VALUES && LEARN_RULES_MSDD -  for MSDD with the value map
                //USE_RULE_APRIORI_VALUES && !LEARN_RULES_MSDD - for ASDD with the value map
        //!USE BELLMAN - means use a q-learning state action vlaue map
            //USE_STATE_ACTION_LOTS - means use the big one
            //CURRENTLY NO Q-LEARNING VERSION OF THIS??

        USE_SAVED_APRIORI_VALUES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_MSDD_VALUES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_STATE_ACTION_VALUES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_STATE_ACTION_STATE_ACTION_VALUES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_CLAUSE_APRIORI_VALUES = USE_REINFORCEMENT_POLICY;

        /********SAVED RULES*/
        USE_SAVED_MSDD_RULES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_APRIORI_RULES = USE_REINFORCEMENT_POLICY;
        USE_SAVED_STATE_ACTION_MAP = USE_REINFORCEMENT_POLICY;
        USE_SAVED_APRIORI_CLAUSES = USE_REINFORCEMENT_POLICY; 

        LEARN_RULES_MSDD = false;
        LEARN_RULES_APRIORI = false;//true;
        LEARN_CLAUSES_ASDD = false;
        //set USE_SET_BASED_APRIORI_COUNTING in ClauseLearnerASDD

           String aprioriClaueseFileExt = "";
        if (ClauseList.USE_WEIGHTED_RULE_VALUES)
            aprioriClaueseFileExt = "WRV";
        if (ClauseList.USE_AVG_RULE_VALUES)
            aprioriClaueseFileExt = "ARV";
        if (ClauseList.USE_MOST_SPECIFIC_RULE_VALUES)
            aprioriClaueseFileExt = "MSRV";
        if (ClauseList.USE_VARIANCE_RULE_WEIGHTS)
            aprioriClaueseFileExt = "VRV";
        if (ClauseList.USE_NONFRAME_RULES_ONLY)
            aprioriClaueseFileExt += "NFRV";
        if (ClauseList.USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY)
            aprioriClaueseFileExt += "ACO";
        if (ClauseList.USE_WEIGHTING_OF_ONE)
            aprioriClaueseFileExt = "WOO";
        
        PERCEP_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\percepRecord.sav";        
        ACTION_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\actionRecord.sav";        
        VALUE_MAP_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedAprioriValues.sav";
        VALUE_MAP_MSDD_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedMSDDValues.sav";
        VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedStateActionValues.sav";
        STATE_ACTION_VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedStateActionStateActionValues.sav";
        VALUE_MAP_CLAUSE_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedClauseAprioriValues.sav";
        MSDD_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedMSDDRules.sav";        
        APRIORI_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedAprioriRules.sav";        
        STATE_ACTION_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\stateActionMap.sav";        
        STATE_ACTION_LOTS_FILE_NAME= "c:\\JAVA Projects\\playmaker8\\stateActionMapBig.sav";
        APRIORI_CLAUSES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedAprioriClauses"  +  aprioriClaueseFileExt + ".sav" ;
  
        initialise(new PredatorAgentPercep(), new PredatorAction());
       
        action = new PredatorAction();
        action.setDefault();
        
        if (getRole() == PREDATOR) {
            realThinker = true;
            reinforcementLearner = new ReinforcementLearner();
            if (USE_REINFORCEMENT_POLICY) {  
                //This will read in the saved rules and value map
                if (LEARN_RULES_MSDD || LEARN_RULES_APRIORI || !USE_SAVED_STATE_ACTION_MAP)
                    learnRules();
                //if (LEARN_CLAUSES_ASDD || LEARN_RULES_MSDD || LEARN_RULES_APRIORI)
                        learnClauses();
            }
        } else {
            realThinker = false;
        }
    }
    
   
    
  
 
    
    public int getRole() {
        return role;
    }
    
    public void setRole(int role) {
        this.role = role;
    }
    
  
  

    
    public Action defaultAction() {
        Action anAction = new PredatorAction();
        anAction.setDefault();
        return anAction;
    }
}
