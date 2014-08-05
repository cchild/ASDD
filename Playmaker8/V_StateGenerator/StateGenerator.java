/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_ReinforcementLearner.StateMap;
import V_ReinforcementLearner.StateTable;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public abstract class StateGenerator {
    

    
    
    public abstract ArrayList <ArrayList> generateStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist);
    
    public abstract ArrayList generateState_random (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap);
    
    public abstract ArrayList generateState_picking (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateTable sTable, StateMap stMap);
    
    public abstract Sensor generateState_bestAction (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, StateTable sTable, StateMap stMap);
    
    public abstract Token generateActionFromTable (Sensor s1, StateTable sTable);
    
    public abstract Token generateRandomAction (Sensor s1, ArrayList <Token> actions);
    
    //public abstract ArrayList generateState2 (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList);
    
    //public abstract Sensor generateStateWithCheck (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList);
    
}
