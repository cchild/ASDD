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
    
    
    
    
    public StateValueTable createStateValueTable (int limit, StateGenerator sGen, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
        StateValueTable sTable = new StateValueTable ();
        sTable = sTable.fromStateMap(stMap);
        
        ArrayList container;
        
        
        Sensor old_state = currentState.copy();
        
        Sensor new_state = currentState.copy();
        
        
       
        
////        double learning_rate = 0.03;
////        double reward = 1;
////        double discount_factor = 0.95;
        
        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.95;
        double actual_reward = 0; 
        
        StateList possible_states;
        
        int shouldprint = limit / 25;
        
        int numberOfRewards = 0;
        
        
        
        
        // PRINTING EVERY 10%
        for (int i = 1; i < limit; i++) {

            if (i == 1)
                System.out.println("");
            
            
            if (i % shouldprint == 0) {
                double b = (double) i;
                double c = (double) limit;
                double a = b / c;
                
                System.out.println(a * 100 + " % ");
            }
        

            //System.out.println(i);
            // GENERATING POSSIBLE STATES FOR EVERY ACTION
            
            //System.out.print("GENERATING STATELISTS");
            container = sGen.generateAllPossibleStates(new_state, sList, sMap, rsList, impossibleList, false, stMap);
            //System.out.println(" _____ OK");
            double max_factor = 0.0;

            
            
            
            
            double rand = Math.random();
            
            double rand2 = Math.random();
            double c = 3.99;
            
            rand2 = rand2 * c ;
            
            
            int pick = (int) rand2;
            
            ArrayList ps = (ArrayList) container.get(0);
            
            
            // CONSIDERING ALL ACTIONS
            for (int l = 0; l < ps.size(); l++) {
                

                possible_states = (StateList) ps.get(l);



                //FOR EVERY POTENTIAL NEW STATE, FEEDING BACK ITS VALUE 
                // ACCORDING TO THE PROB GIVEN IN THE STATEMAP

                double factor2 = 0.0;
   
                for (int j = 0; j < possible_states.size(); j++) {


                    int potential_state_index =  sTable.findSensor(possible_states.getSensor(j));



                    actual_reward = 0.0;
                    
                    
                    if (possible_states.getSensor(j).isRewarded()) {

                        actual_reward = reward;
                        numberOfRewards++;
                    }


                    factor2 += possible_states.getProb(j) * (actual_reward + discount_factor * sTable.getValue(potential_state_index));              
                     
                }

                

                
                // IF BEST ACTION YET
                if (factor2 > max_factor) {
                
                    
                    

                    max_factor = factor2;

                    
                }



                }
            
            
            ArrayList ps3 = (ArrayList) container.get(0);
            possible_states = (StateList) ps3.get(pick);
            
            // RANDOM PICK FROM POSSIBLE STATES LIST
            double rand3 = Math.random();

            int rand_limit = possible_states.size();
            double rand_limit2 = (double) rand_limit;
            rand3 = rand3 * rand_limit2;

            int state_to_pick = (int) rand3;
            new_state = possible_states.getSensor(state_to_pick);
            

            
            // INDEXES OF OLD AND NEW STATE
                int old_state_index = sTable.findSensor(old_state);
                
        
                double increase_factor = learning_rate * (max_factor - sTable.getValue(old_state_index));
                        
                sTable.increaseValue(old_state_index, increase_factor);
                

                old_state = new_state.copy();
        }
        

        
        
        sTable = sTable.sort();
        return sTable;
        
    }
    
    
    
    
    
    
    public StateActionValueTable createStateActionValueTable (int limit, StateGenerator sGen, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
        StateActionValueTable sTable = new StateActionValueTable () ;
        ArrayList container = new ArrayList ();
        Token old_action = new Token (currentState.tokenMap);
        
        // IF NOT ADDING, FINDING BACK PREVIOUS STATE
        
       
        //Sensor last_state = currentState;
        Sensor old_state = currentState.copy();
        
        Sensor new_state = currentState.copy();
        
        
        Token root = new Token(currentState.tokenMap);    
          
        // FIRST ENTRY WITHOUT THE ACTION
        //sTable.addSensor(currentState, root, 0.0);
        
        
////        double learning_rate = 0.03;
////        double reward = 1;
////        double discount_factor = 0.95;
        
        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.95;
        
        
        // INDEX OF THE LAST ACTION
        int lastaction = 0;
        
        int count = 0;
        boolean swap = false;
        
        int shouldprint = limit / 25;
        
        int numberOfRewards = 0;
        // INIT 
        //ArrayList init = sGen.generateRandomActionAndPseudoRandomState (currentState, sList, sMap, rsList, impossibleList);
        
        for (int i = 1; i < limit; i++) {

            if (i == 1)
                System.out.println("");
            
            
            if (i % shouldprint == 0) {
                double b = (double) i;
                double c = (double) limit;
                double a = b / c;
                
                System.out.println(a * 100 + " % ");
            }
            // GENERATING NEXT STATE
            

            container = sGen.generateRandomActionAndPseudoRandomState (old_state, rsList, impossibleList, stMap);

            
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
    
    
    
    
    
    
    
    
 
    
    
    
    public StateActionValueTable createStateActionValueTable_dynamic (int limit, StateGenerator sGen, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
        StateActionValueTable s22 = new StateActionValueTable ();
        
        s22 = s22.fromStateMap(stMap);

        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.9;
        
        int shouldprint = limit / 25;
        
        
        Sensor source_state = currentState;

            
        for (int i = 1; i < limit; i++) {
                    
        
            if (i == 1) {
                
                System.out.println("");
            }

            if (i % shouldprint == 0) {
                double b = (double) i;
                double c = (double) limit;
                double a = b / c;
                
                System.out.println(a * 100 + " % ");
            }
            
            ArrayList container = sGen.generateAllPossibleStates(source_state, sList, sMap, rsList, impossibleList, false, stMap);

            // StateLists for different Actions
            ArrayList ps = (ArrayList) container.get(0);
            
            // The different Actions
            ArrayList ps2 = (ArrayList) container.get(1);
            
            // Picks a random StateList (and a random action at the same time)

            
            
            int source_state_index = s22.findSensor(source_state);;
                
                
                
            for (int j = 0; j < ps.size(); j++) {
                
                // Picking StateList & action j
                StateList possible_states = (StateList) ps.get(j);
                Token action = (Token) ps2.get(j);
                
                double factor = 0.0;
                
                int action_index_for_source = s22.findActionIndex(source_state_index, action);
                        
                //Exploring StateList & action j
                for (int h = 0; h < possible_states.size(); h++) {
             
                    int possible_state_index = s22.findSensor(possible_states.getSensor(h));
                    
                    //int action_index = s22.findActionIndex(possible_state_index, action);
 
                    double actual_reward = 0.0;
                    
                    if (possible_states.getSensor(h).isRewarded()) {
                        actual_reward = reward;
                    }
                    
                    factor = factor + (possible_states.getProb(h) * learning_rate * (actual_reward + (discount_factor * s22.findMaxActionValue(possible_state_index) - s22.getValue(source_state_index).get(action_index_for_source))));
//                     
                }
                
                //System.out.println(source_state + " action " + action + " rewarded " + factor+ " " + j + " out of size " + ps.size());
                s22.increaseValue(source_state_index, action_index_for_source, factor);
                // Updating source_state randomly
                
                

            }
            
                    double random_statelist = Math.random() * 3.99;
                    int random_statelist_int = (int) random_statelist;
            
                    StateList possible_states = (StateList) ps.get(random_statelist_int);
                    
                    int size = possible_states.size();
                    double size_double = (double) size;
                    
                    double random_state = Math.random() * size_double;
                    
                    int random_state_int = (int) random_state;
                    
                    source_state = possible_states.getSensor(random_state_int);
                    
                    //System.out.println("Picking");
                
        }
        
        
        return s22;
    }
    

    
}
