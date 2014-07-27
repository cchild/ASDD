/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_ReinforcementLearner;

import V_StateGenerator.*;
import V_RuleLearner.*;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class ReinforcementLearner {
    
    
    public ReinforcementLearner () {
        
    }
    
    
    public StateTable createTable (int limit, StateGenerator sGen, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, int won) {
        
        StateTable sTable = new StateTable () ;
        ArrayList container;
        Token action;
        Token root = new Token(currentState.tokenMap);    
           
        sTable.addSensor(currentState, root, 0.0);
        int lastaction = 0;
        
        for (int i = 1; i < limit; i++) {
            //System.err.println(i + " / 1001");
            //ArrayList <StateList> stateLists = stateGenerator.generateStates (rootState, sList, sMap, rsList, impossibleList, false);
            
            
            container = sGen.generateState (currentState, sList, sMap, rsList, impossibleList);
        
            
            currentState = (Sensor) container.get(0);
            action = (Token) container.get(1);
            
            //System.err.println("NEW STATE : " + currentState + "CHOSEN ACTION : " + action);
            
            //if (!sTable.containsStateAndAction(currentState, action)) {
            sTable.setAction(lastaction, action);
            
            sTable.addSensor(currentState, root, 0.0);
                
            lastaction = sTable.size()-1;
                            
            //}
            
        }
        
        
        return sTable;
    }
    
    
    
    
    public StateTable cleanTable (StateTable sTable) {
        
        ArrayList haha = new ArrayList () ;
        int a = sTable.size();
        
        for (int i = 0; i < sTable.size()-1; i++) {
            
            for (int j = 1; j < sTable.size(); j++ ) {
                
                if ( (sTable.getSensor(i).sensorMatch_exact(sTable.getSensor(j))) ) {
                    if (!haha.contains(i) && !haha.contains(j))
                        haha.add(j);
                }
            }
        }
        
        System.out.println("Size : " + haha.size());
        System.out.println("Haha : " + haha);
        
        
        
        
        
        
        return sTable;
    }
    
}
