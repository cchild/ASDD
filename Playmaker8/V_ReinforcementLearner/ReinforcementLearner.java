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
        //ArrayList init = sGen.generateState_random (currentState, sList, sMap, rsList, impossibleList);
        
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
            

            container = sGen.generateState_random (old_state, rsList, impossibleList, stMap);

            
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
            container = sGen.generateStates(new_state, sList, sMap, rsList, impossibleList, false, stMap);
            //System.out.println(" _____ OK");
            double max_factor = 0.0;
            int max_factor_action_index = 0;
            
            
            
            
            double rand = Math.random();
            
            double rand2 = Math.random();
            double c = 3.99;
            
            rand2 = rand2 * c ;
            
            
            int pick = (int) rand2;
            
            ArrayList ps = (ArrayList) container.get(0);
            // CONSIDERING ALL ACTIONS
            for (int l = 0; l < ps.size(); l++) {
                
//                if (rand < 0.2)
//                    possible_states = (StateList) container.get(pick);
//
//                else 
                
                possible_states = (StateList) ps.get(l);

                //possible_states.printList("Number " + l);
                int old_state_index;
                int new_state_index;

                

                // THIS IS SET TO REWARD IF THE NEW STATE IS REWARDED
                



               // INDEXES OF OLD AND NEW STATE
                old_state_index = sTable.findSensor(old_state);
                new_state_index = sTable.findSensor(new_state);







                //FOR EVERY POTENTIAL NEW STATE, FEEDING BACK ITS VALUE 
                // ACCORDING TO THE PROB GIVEN IN THE STATEMAP

                double factor2 = 0.0;
                int seen = 0;

                //System.out.println("Container Size : " + container.size());
                
//                ArrayList ps2 = (ArrayList) container.get(0);
//                StateList possible_states = (StateList) ps2.get(l);
                
                //a.printList("" + l);
                for (int j = 0; j < possible_states.size(); j++) {


                    int potential_state_index =  sTable.findSensor(possible_states.getSensor(j));



                    actual_reward = 0.0;
                    
                    
                    if (possible_states.getSensor(j).isRewarded()) {

                        actual_reward = reward;
                        numberOfRewards++;
                    }


                    factor2 += possible_states.getProb(j) * (actual_reward + discount_factor * sTable.getValue(potential_state_index));              
                     
                }

                //System.out.println("Action index : " + l + " factor : " + factor2);
                
                
                seen++;
                
                // IF BEST ACTION YET
                if (factor2 > max_factor) {
                
                    
                    

                    max_factor = factor2;
                    max_factor_action_index = l;
                    
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
            
            
            //System.out.println("CHOSEN INDEX : " + max_factor_action_index + " factor : " + max_factor);
                
            // INDEXES OF OLD AND NEW STATE
                int old_state_index = sTable.findSensor(old_state);
                
                
                //System.out.println("State " + old_state + " Action : " + max_factor_action_index);
                
//                if (new_state.isRewarded()) {
//                    
//                    actual_reward = reward;
//                }
                
//                possible_states = (StateList) container.get(max_factor_action_index);
//                
//                actual_reward = 0.0;
//                
//                for (int y = 0; y < possible_states.size(); y++) {
//                    
//                    if (possible_states.getSensor(y).isRewarded())
//                        actual_reward++;
//                }
                
                
                double increase_factor = learning_rate * (max_factor - sTable.getValue(old_state_index));
//                         
                sTable.increaseValue(old_state_index, increase_factor);
                
                //System.out.println("Increasing old value by : " + max_factor);
                
                //System.out.println("New State : " + new_state);
                
                old_state = new_state.copy();
        }
        
        //System.out.println("Rewards : " + numberOfRewards);
        
        
        
        sTable = sTable.sort();
        return sTable;
        
        //return numberOfRewards;
    }
    
    
    
    public StateActionValueTable createTable_dynamic2 (int limit, StateGenerator sGen, int method, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
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
        
        
////        double learning_rate = 0.0285;
////        double reward = 1;
////        double discount_factor = 0.76;
        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.9;
        
        // INDEX OF THE LAST ACTION
        int lastaction = 0;
        
        int count = 0;
        boolean swap = false;
        
        int shouldprint = limit / 100;
        
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
            
            
            
            // 100% RANDOM
            if (method == 1)
                container = sGen.generateStates (old_state, sList, sMap, rsList, impossibleList, false, stMap);
                //container = sGen.generateStates (old_state, sList, sMap, rsList, impossibleList, false, stMap);
            
            // Q-Learning
            if (method == 2)
                container = sGen.generateState_picking (old_state, rsList, impossibleList, sTable, stMap);
            
            StateList possible_states = new StateList ();
            
            if (container.size() > 1) {
            
                ArrayList ps = (ArrayList) container.get(0);
                ArrayList ps2 = (ArrayList) container.get(1);
                
                
                // GENERATING RANDOM ACTION
                double rand3 = Math.random() * 3.99;
                int picked = (int) rand3;
                possible_states = (StateList) ps.get(picked);
                
                //possible_states.printList("Possible");
                
                double rand = Math.random();
                double r = 0.0;
                
                // RANDOM PICK
                int rand_limit = possible_states.size();
                double rand_limit2 = (double) rand_limit;
                rand = rand * rand_limit2;
            
                int state_to_pick = (int) rand;
                new_state = possible_states.getSensor(state_to_pick);
                
                //System.out.println("picked : " + new_state);

                // PSEUDO RANDOM PICK
//            for (int j = 0; j < possible_states.size(); j++) {
//                
//                    r = r + possible_states.getProb(j);
//                
//                    if (r > rand) {
//                    
//                        new_state = possible_states.getSensor(j);
//                        break;
//                    }
//                }   
                
                
                old_action = (Token) ps2.get(picked);
            }
            
            
            
            
            //System.out.println("Returned : " + new_state + " & " + old_action);
            
            int old_state_index = 0;
            int old_action_index = 0;
            
            double factor = 0.0;
            
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
                
                       
            
//            //FOR EVERY POTENTIAL NEW STATE, FEEDING BACK THE BEST VALUE 
//            // ACCORDING TO THE PROB GIVEN IN THE STATEMAP
//            for (int j = 0; j < possible_states.size(); j++) {
//                
//                
//                int potential_state_index =  sTable.findSensor(possible_states.getSensor(j));
//                
//                double factor2 = 0.0;
//                
//                if (possible_states.getSensor(j).isRewarded()) {
//                
//                    actual_reward = reward;
//                    numberOfRewards++;
//                }
//                            
//                if (potential_state_index != -1) {
//                
//                    factor2 += possible_states.getProb(j) * (learning_rate * (actual_reward + (discount_factor * sTable.findMaxActionValue(potential_state_index)) - sTable.getValue(old_state_index).get(old_action_index)));              
//                
//                }
//                
//                else {
//                    
//                    factor2 += possible_states.getProb(j) * (learning_rate * (actual_reward - sTable.getValue(old_state_index).get(old_action_index)));              
//                
//                }
//                
//                
//                 sTable.increaseValue(old_state_index, old_action_index, factor2);
//            }
            
            
            
            // RETURNS THE INDEX OF THE NEW STATE IN THE TABLE
            int new_state_index =  sTable.findSensor(new_state);
            
           
            
            if (new_state.isRewarded()) {
                
                actual_reward = reward;
                numberOfRewards++;
                
                //System.out.println("Reward from : " + old_state + " & " + old_action + " led to : " + new_state);
            }
            
            
                
            
            // CURRENT STATE IS IN THE TABLE
            if (new_state_index != -1) {
                
                   
                factor += learning_rate * (actual_reward + (discount_factor * sTable.findMaxActionValue(new_state_index)) - sTable.getValue(old_state_index).get(old_action_index));
//                            
            }
            
            // CURRENT STATE IS NOT IN THE TABLE
            else {
                
                factor += learning_rate * (actual_reward  - sTable.getValue(old_state_index).get(old_action_index));
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
            
            ArrayList container = sGen.generateStates(source_state, sList, sMap, rsList, impossibleList, false, stMap);

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
    
    
    
    
    
    
    
public StateActionValueTable createTable_dynamic (int limit, StateGenerator sGen, int method, Sensor currentState, SensorList sList, SensorMap sMap, RuleSetList rsList, SensorList impossibleList, StateMap stMap) {
        
        StateActionValueTable sTable = new StateActionValueTable () ;
        ArrayList container = new ArrayList ();
        Token old_action = new Token (currentState.tokenMap);
        

        Sensor old_state = currentState.copy();
        
        Sensor new_state = currentState.copy();
        
        
        Token root = new Token(currentState.tokenMap);    
          
        StateList possible_states = new StateList ();
        
        // PARAMETERS
        double learning_rate = 0.03;
        double reward = 1;
        double discount_factor = 0.95;
        
        

        
        int shouldprint = limit / 100;
        

        
        
        for (int i = 1; i < limit; i++) {

            // PRINTING SECTION
            if (i % shouldprint == 0) {
                double b = (double) i;
                double c = (double) limit;
                double a = b / c;
                
                System.out.println(a * 100 + " % ");
            }
            
            
            
            // GENERATING NEXT STATES
            ArrayList test = new ArrayList ();
            //System.out.println(i + " / " + limit);
            container = sGen.generateStates2(old_state, rsList, impossibleList, stMap, sList, sMap);
            //container = sGen.generateState_random(old_state, rsList, impossibleList, stMap);
            
            test = sGen.generateState_random(old_state, rsList, impossibleList, stMap);
            
            //System.out.println("Picked Action : " + test.get(1) + " led to : " + test.get(0));
            
            
            // (container.size() > 1) || 
            if ( (test.size() > 1) ){
            
                //possible_states = (StateList) container.get(0);
                new_state = (Sensor) test.get(0);
                old_action = (Token) test.get(1);
                //System.out.println("Action verif : " + old_action);
            }
            
            // RANDOM DOUBLE
            double rand = Math.random();
            double r = 0.0;
            
            // TODO RANDOM CHOICE
            // CHOOSING NEXTSTATE ACCORDING TO PROBABILITIES IN THE STATEMAP
            int rand_limit = possible_states.size();
            double rand_limit2 = (double) rand_limit;
            
            rand = rand * rand_limit2;
            
            int state_to_pick = (int) rand;
            
            //System.out.println("State to pick : " + state_to_pick + " size : " + rand_limit);
            //new_state = possible_states.getSensor(state_to_pick);
            
            //System.out.println("STATE FINALLY PICKED : " + new_state);
            
//            for (int j = 0; j < possible_states.size(); j++) {
//                
//                r = r + possible_states.getProb(j);
//                
//                if (r > rand) {
//                    
//                    new_state = possible_states.getSensor(j);
//                    break;
//                }
//            }
            
            // THIS IS SET TO REWARD IF THE NEW STATE IS REWARDED
            double actual_reward = 0;
            

                        
                        
            // HERE
            // new_state = (Sensor) container.get(0);
            
            //System.out.println("Returned : " + new_state + " & " + old_action);
            
            int old_state_index = 0;
            int old_action_index = 0;
            
            double factor;
            
            
            
            
            
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
                
              
            
//            // FOR EVERY POTENTIAL NEW STATE, FEEDING BACK THE BEST VALUE 
//            // ACCORDING TO THE PROB GIVEN IN THE STATEMAP
//            for (int j = 0; j < possible_states.size(); j++) {
//                
//                
//                int potential_state_index =  sTable.findSensor(possible_states.getSensor(j));
//                
//                double factor2 = 0.0;
//                
//                if (possible_states.getSensor(j).isRewarded()) {
//                
//                    actual_reward = reward;
//                
//                }
//                            
//                if (potential_state_index != -1) {
//                
//                    factor2 += possible_states.getProb(j) * (learning_rate * (actual_reward + (discount_factor * sTable.findMaxActionValue(potential_state_index)) - sTable.getValue(old_state_index).get(old_action_index)));              
//                
//                }
//                
//                else {
//                    
//                    factor2 += possible_states.getProb(j) * (learning_rate * (actual_reward - sTable.getValue(old_state_index).get(old_action_index)));              
//                
//                }
//                
//                
//                 sTable.increaseValue(old_state_index, old_action_index, factor2);
//            }
            
            // RETURNS THE INDEX OF THE NEW STATE IN THE TABLE
            int new_state_index =  sTable.findSensor(new_state);
            
           
            
            if (new_state.isRewarded()) {
                
                actual_reward = reward;
                
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
//        StateActionValueTable sTable = new StateActionValueTable () ;
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
    
    public StateActionValueTable cleanTable (StateActionValueTable sTable) {
        
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
