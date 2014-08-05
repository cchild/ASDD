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
    
    
    public StateTable createTable (int limit, StateGenerator sGen, int method, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
        StateTable sTable = new StateTable () ;
        ArrayList container = new ArrayList ();
        Token old_action = new Token (currentState.tokenMap);
        
        // IF NOT ADDING, FINDING BACK PREVIOUS STATE
        
       
        //Sensor last_state = currentState;
        Sensor old_state = currentState.copy();
        
        Sensor new_state = currentState.copy();
        
        
        Token root = new Token(currentState.tokenMap);    
          
        // FIRST ENTRY WITHOUT THE ACTION
        //sTable.addSensor(currentState, root, 0.0);
        
        
        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.95;
        
        
        // INDEX OF THE LAST ACTION
        int lastaction = 0;
        
        int count = 0;
        boolean swap = false;
        
        int shouldprint = limit / 10;
        
        int numberOfRewards = 0;
        // INIT 
        //ArrayList init = sGen.generateState_random (currentState, sList, sMap, rsList, impossibleList);
        
        for (int i = 1; i < limit; i++) {

            if (i % shouldprint == 0) {
                double b = (double) i;
                double c = (double) limit;
                double a = b / c;
                
                System.out.println(a * 100 + " % ");
            }
            // GENERATING NEXT STATE
            
            // 100% RANDOM
            if (method == 1)
                container = sGen.generateState_random (old_state, sList, sMap, rsList, impossibleList, stMap);
            
            // Q-Learning
            if (method == 2)
                container = sGen.generateState_picking (old_state, rsList, impossibleList, sTable, stMap);
            
            
            if (container.size() > 1) {
            
                new_state = (Sensor) container.get(0);
                old_action = (Token) container.get(1);
            }
            
            
            
            
            //System.out.println("Returned : " + currentState + " & " + old_action);
            
            int old_state_index = 0;
            int old_action_index = 0;
            
            double factor;
            
            // THIS IS SET TO REWARD IF THE NEW STATE IS REWARDED
            double actual_reward = 0;
            
            
            
            // GETS THE INDEXES OF THE OLD STATE & ACTION IN THE TABLE (IF THEY ARE)
            ArrayList <Integer> temp = sTable.containsStateAndAction(old_state, old_action);
            
            
            
            // ADDING STATE & ACTION
            if (temp.isEmpty()) {
                
                
                sTable.addSensor(old_state, old_action, 0.0);
                old_state_index = sTable.size()-1;
                old_action_index = 0;
            }
            
            // ADDING ONLY THE ACTION
            if (temp.size() == 1) {
                
                
                sTable.addActionAndValue(temp.get(0), old_action, 0.0);
                old_state_index = temp.get(0);
                old_action_index = sTable.getAction(temp.get(0)).size()-1;
            }               
               
            
            // BOTH STATE AND ACTION ARE ALREADY IN THE TABLE
            if (temp.size() > 1) {
                
                old_state_index = temp.get(0);
                old_action_index = temp.get(1);
            }
                
                       
            
            // RETURNS THE INDEX OF THE NEW STATE IN THE TABLE
            int new_state_index =  sTable.findSensor(new_state);
            
           
            
            if (new_state.isRewarded()) {
                
                actual_reward = reward;
                numberOfRewards++;
                
                //System.out.println("Reward from : " + old_state + " & " + old_action + " led to : " + new_state);
            }
                
            
            // CURRENT STATE IS IN THE TABLE
            if (new_state_index != -1) {
                
                   
                factor = learning_rate * (actual_reward + (discount_factor * sTable.findMaxActionValue(new_state_index)) - sTable.getValue(old_state_index).get(old_action_index));
//                            
            }
            
            // CURRENT STATE IS NOT IN THE TABLE
            else {
                
                factor = learning_rate * (actual_reward  - sTable.getValue(old_state_index).get(old_action_index));
//             
            }
            
            
            
            
            sTable.increaseValue(old_state_index, old_action_index, factor);
                
                
            
            old_state = new_state.copy();

            
            
            
            
            }
            
            
            
        
        
        //System.out.println("Rewards : " + numberOfRewards);
        
        
        
        //sTable = sTable.sort();
        return sTable;
        
        //return numberOfRewards;
    }
    
    
 
    
//    public Token useTableForAction (StateGenerator sGen, int method, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
//        
//        StateTable sTable = new StateTable () ;
//        ArrayList container = new ArrayList ();
//        Token old_action = new Token (currentState.tokenMap);
//        
//        
//        
//
//            // GENERATING NEXT STATE
//            
//            // 100% RANDOM
//            if (method == 1)
//                container = sGen.generateState_random (currentState, sList, sMap, rsList, impossibleList, stMap);
//            
//            // Q-Learning
//            if (method == 2)
//                container = sGen.generateState_picking (currentState, rsList, impossibleList, sTable, stMap);
//            
//            
//            if (container.size() > 1) {
//            
//                old_action = (Token) container.get(1);
//            }
//            
//            
//
//        return old_action;
//        
//        //return numberOfRewards;
//    }
    
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
