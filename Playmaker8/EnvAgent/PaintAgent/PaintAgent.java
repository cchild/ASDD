package EnvAgent.PaintAgent;

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
import EnvModel.PaintModel.*;
import EnvAgent.*;
import Logging.*;
import ReinforcementLearner.*;
import EnvAgent.RuleLearner.*;
import EnvAgent.ClauseLearner.*;

//an agent has state which is internal to itself and can perform actions

public class PaintAgent extends Agent {
  
    public PaintAgent(PaintAgentBody body, int role) {
        super();
        this.body = body;
        
        USE_SAVED_PERCEP_ACTION_RECORD = false;
        PERCEP_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\paintPercepRecord.sav";        
        ACTION_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\paintActionRecord.sav";

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

    
        if (!PaintAgentBody.useNotCleanWithPickup) {
            VALUE_MAP_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedPaintAprioriValues.sav";
            VALUE_MAP_MSDD_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedPaintMSDDValues.sav";
            VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedPaintStateActionValues.sav";
            STATE_ACTION_VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedPaintStateActionStateActionValues.sav";
            VALUE_MAP_CLAUSE_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedPaintClauseAprioriValues.sav";
            MSDD_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedPaintMSDDRules.sav";
            APRIORI_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedPaintAprioriRules.sav";
            STATE_ACTION_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\paintStateActionMap.sav";
            STATE_ACTION_LOTS_FILE_NAME= "c:\\JAVA Projects\\playmaker8\\paintStateActionMap.sav";
            APRIORI_CLAUSES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedPaintAprioriClauses"  +  aprioriClaueseFileExt + ".sav" ;
        }
        else
        {
            VALUE_MAP_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintAprioriValues.sav";
            VALUE_MAP_MSDD_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintMSDDValues.sav"; 
            VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintStateActionValues.sav";
            STATE_ACTION_VALUE_MAP_STATE_ACTION_FILENAME= "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintStateActionStateActionValues.sav";
            VALUE_MAP_CLAUSE_APRIORI_FILENAME = "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintClauseAprioriValues.sav";
            MSDD_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintMSDDRules.sav";   
            APRIORI_RULES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintAprioriRules.sav";  
            STATE_ACTION_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\cleanPaintStateActionMap.sav";
            STATE_ACTION_LOTS_FILE_NAME= "c:\\JAVA Projects\\playmaker8\\cleanPaintStateActionMap.sav";
            APRIORI_CLAUSES_FILE_NAME = "c:\\JAVA Projects\\playmaker8\\learnedCleanPaintAprioriClauses"  +  aprioriClaueseFileExt + ".sav" ;
        }

      
      
        USE_SAVED_PERCEP_ACTION_RECORD = false;
        USE_REINFORCEMENT_POLICY = false;
        USE_BELLMAN = false;
        USE_STATE_ACTION_LOTS = false;
        USE_CLAUSE_VALUES = false;
        USE_CLAUSE_APRIORI_VALUES = false;
        USE_RULE_APRIORI_VALUES = false;

        //IMPORTANT!!!
        //set useNotCleanWithPickup to FALSE to change the environement
        
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
        LEARN_RULES_APRIORI = false;
        LEARN_CLAUSES_ASDD = true;
        //set USE_SET_BASED_APRIORI_COUNTING in ClauseLearnerASDD
        
        action = new PaintAction();
        action.setDefault();
        
        initialise(new PaintAgentPercep(), new PaintAction());
     
        reinforcementLearner = new ReinforcementLearner();
        if (USE_REINFORCEMENT_POLICY) {  
            //This will read in the saved rules and value map
           if (LEARN_RULES_MSDD || LEARN_RULES_APRIORI || !USE_SAVED_STATE_ACTION_MAP)
                learnRules();
           //if (LEARN_CLAUSES_ASDD || LEARN_RULES_MSDD || LEARN_RULES_APRIORI) //added MSDD and APRIORI so we can access RL
                learnClauses();
        }
    } 
    
    
    public PaintAgentBody getPaintAgentBody() {
        return (PaintAgentBody)body;
    }

    public Action defaultAction() {
        Action anAction = new PaintAction();
        anAction.setDefault();
        return anAction;
    }
   
    
 
}
