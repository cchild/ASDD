/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_ReinforcementLearner.StateActionValueTable;
import V_Sensors.StateMap;
import V_ReinforcementLearner.StateValueTable;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public abstract class StateGenerator {
    

    
    
    public abstract ArrayList  generateAllPossibleStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist, StateMap stMap);
    
    public abstract ArrayList generateRandomActionAndPseudoRandomState (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap);
    
    public abstract Token generateActionFromStateActionValueTable (Sensor s1, StateActionValueTable sTable);
    
    public abstract Token generateActionFromStateValueTable (Sensor s1, StateMap stMap, StateValueTable sValTab);
            
    public abstract Token generateRandomAction (ArrayList <Token> actions);
    
   
}
