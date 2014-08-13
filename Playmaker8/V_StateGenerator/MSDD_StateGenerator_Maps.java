/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_ReinforcementLearner.StateActionValueTable;
import V_ReinforcementLearner.StateMap;
import V_ReinforcementLearner.StateValueTable;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class MSDD_StateGenerator_Maps extends StateGenerator {
    
        public MSDD_StateGenerator_Maps () {
    
    }
        
     // RETURNS AN ARRAYLIST OF ALL THE POSSIBLE STATES
     //    
     // INDEX 0 : ARRAYLIST CONTAINING THE STATELISTS
     // INDEX 1 : ARRAYLIST CONTAINING THE ACTIONS (TOKENS)
     //   
     // OF COURSE, STATELIST 1 CORRESPONDS TO ACTION 1   
     @Override
     public ArrayList  generateStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist, StateMap stMap) {
         

         ArrayList stateLists = new ArrayList ();
         ArrayList actionsList = new ArrayList ();
         
         
         ArrayList res = new ArrayList ();
         
         
         
         Token actions = s1.getToken(s1.size()-1).copy();
         
         
         // Index of State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         
         for (int action = 0; action < s1.tokenMap.getTokenList(s1.size()-1).size(); action ++) {
        
             
             StateList possible_states = new StateList ();
             
             int index = stMap.findSensor(s1);

            //System.out.println("Action rand : " + action);
            //System.out.println("Action : " + stMap.getActions(index).get(action));

            ArrayList <ArrayList> possibleStates = (ArrayList) stMap.getReferencies(index).get(action);

            actions = stMap.getActions(index).get(action).copy();

             // Getting occ. to compute probs
             int total_occurencies_of_state_and_action = (int) stMap.getOccurencies(index_of_state_in_map).get(action);

             double toostaa = (double) total_occurencies_of_state_and_action;

             // Going through the above list, and adding each state with its prob
             for (int i = 0; i < possibleStates.size(); i++) {


                 ArrayList couple = (ArrayList) possibleStates.get(i);

                 int occurencies_of_action_and_state_i = (int) couple.get(1);

                 double prob = (double) occurencies_of_action_and_state_i;

                 prob = prob / toostaa;

                 int new_state_index = (int) couple.get(0);

                 Sensor state = stMap.getSensor(new_state_index);


                 possible_states.addSensor(state, prob);
                 

             }

             //possible_states.printList("Adding");
             stateLists.add(possible_states);
             actionsList.add(actions);
             
             
         }
         
         res.add(stateLists);
         res.add(actionsList);
         
         
         return res;
     
            
            
     }
  
     
     
 // RETURNS AN ARRAYLIST OF ALL THE POSSIBLE STATES
     //    
     // INDEX 0 : ARRAYLIST CONTAINING THE STATELISTS
     // INDEX 1 : ARRAYLIST CONTAINING THE ACTIONS (TOKENS)
     //   
     // OF COURSE, STATELIST 1 CORRESPONDS TO ACTION 1   
     
     public ArrayList  generateStates_maps (Sensor s1, StateMap stMap) {
         

         ArrayList stateLists = new ArrayList ();
         ArrayList actionsList = new ArrayList ();
         
         
         ArrayList res = new ArrayList ();
         
         
         
         Token actions = s1.getToken(s1.size()-1).copy();
         
         
         // Index of State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         
         for (int action = 0; action < s1.tokenMap.getTokenList(s1.size()-1).size(); action ++) {
        
             
             StateList possible_states = new StateList ();
             
             int index = stMap.findSensor(s1);

            //System.out.println("Action rand : " + action);
            //System.out.println("Action : " + stMap.getActions(index).get(action));

            ArrayList <ArrayList> possibleStates = (ArrayList) stMap.getReferencies(index).get(action);

            actions = stMap.getActions(index).get(action).copy();

             // Getting occ. to compute probs
             int total_occurencies_of_state_and_action = (int) stMap.getOccurencies(index_of_state_in_map).get(action);

             double toostaa = (double) total_occurencies_of_state_and_action;

             // Going through the above list, and adding each state with its prob
             for (int i = 0; i < possibleStates.size(); i++) {


                 ArrayList couple = (ArrayList) possibleStates.get(i);

                 int occurencies_of_action_and_state_i = (int) couple.get(1);

                 double prob = (double) occurencies_of_action_and_state_i;

                 prob = prob / toostaa;

                 int new_state_index = (int) couple.get(0);

                 Sensor state = stMap.getSensor(new_state_index);


                 possible_states.addSensor(state, prob);
                 

             }

             //possible_states.printList("Adding");
             stateLists.add(possible_states);
             actionsList.add(actions);
             
             
         }
         
         res.add(stateLists);
         res.add(actionsList);
         
         return res;
     
            
            
     }    
     
   
     // PICKS A RANDOM ACTION, GENERATES ALL THE CORRESPONDING STATES
     // RETURNS ARRAYLIST WITH : 
     // INDEX 0 : STATELIST (POSSIBLE STATES)
     // INDEX 1 : THE RANDOM CHOSEN ACTION
     @Override
     public ArrayList generateStates2 (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap, SensorList sList, SensorMap sMap) {
         
         ArrayList container = new ArrayList ();
         
         StateList possible_states = new StateList ();
         
         // Index of State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         
         // Random action
         // ACTION BETWEEN 0 AND 3
         double rand = Math.random();
         rand = rand * 3.99;
         int action = (int) rand;
        
         int index = stMap.findSensor(s1);
        
        //System.out.println("State : " + s1);
        //System.out.println("Action : " + stMap.getActions(index).get(action));
        
        ArrayList <ArrayList> possibleStates = (ArrayList) stMap.getReferencies(index).get(action);
         
        //for (int i = 0; i < possibleStates.size(); i++) {
            
            //System.out.println(" possible : "  + stMap.getSensor((int) possibleStates.get(i).get(0)));
        //}
         // Index of Action
         //int index_of_action_in_map = stMap.findActionIndex(index_of_state_in_map, random_action);
         
         
         // Possible States with their occurencies
         //ArrayList possible_states_with_occurencies = (ArrayList) stMap.getReferencies(index_of_state_in_map).get(index_of_action_in_map);
         
         // Getting occ. to compute probs
         int total_occurencies_of_state_and_action = (int) stMap.getOccurencies(index_of_state_in_map).get(action);
         
         double toostaa = (double) total_occurencies_of_state_and_action;
         
         // Going through the above list, and adding each state with its prob
         for (int i = 0; i < possibleStates.size(); i++) {
             
             
             ArrayList couple = (ArrayList) possibleStates.get(i);
             
             int occurencies_of_action_and_state_i = (int) couple.get(1);
             
             double prob = (double) occurencies_of_action_and_state_i;
             
             prob = prob / toostaa;
             
             int new_state_index = (int) couple.get(0);
             
             Sensor state = stMap.getSensor(new_state_index);
             
             
             possible_states.addSensor(state, prob);
             
         }
         
         //possible_states.printList();
         container.add(possible_states);
         
         container.add(stMap.getActions(index_of_state_in_map).get(action));
         
         
         return container;
     }
     
    // CHOOSES A RANDOM ACTION, AND RETURNS AN ARRAYLIST WITH :
    // INDEX 0 : GENERATED STATE (SENSOR)
    // INDEX 1 : CHOSEN ACTION (TOKEN)     
    @Override
    public ArrayList generateState_random (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap) {
         
         
       
        ArrayList a = new ArrayList ();

        
        SensorList numbers = new SensorList (); 

        Sensor res = new Sensor (s1.tokenMap);
        
        
        Token t;
        
        
        

        
        
        double rand = Math.random();
        
        double rand2 = Math.random();
        
        double d = 0.0;
        
        
        // ACTION BETWEEN 0 AND 3
        int action = (int) (rand * 3.99);
        
        int index = stMap.findSensor(s1);
        
        //System.out.println("Index : " + index);
        ArrayList possibleStates = (ArrayList) stMap.getReferencies(index).get(action);
        
        //System.out.println("States : " + possibleStates);
        
        
        int occurrencies = (int) stMap.getOccurencies(index).get(action);
        
        double occ = (double) occurrencies;
        
        for (int y = 0; y < possibleStates.size(); y++) {
            
            ArrayList temp = (ArrayList) possibleStates.get(y);
            
            //System.out.println("List : " + index);
            int temp2 = (int) temp.get(1);
            
            double temp3 = (double) temp2;
            
            d = temp3/occ + d;
            //System.out.println("d : " + d);
            
            if (d > rand2) {
                a.add(stMap.getSensor((int) temp.get(0)));
                a.add(stMap.getActions(index).get(action));
                break;
            }
        }
        
        
        //Token actionFromInt = new Token (s1.tokenMap);
        
        
        return a;
     }
    
    
    

    
    
    // CHOOSES AN ACTION (BEST POSSIBLE ACTION) OR RAND (23%), AND RETURNS AN ARRAYLIST WITH :
    // INDEX 0 : GENERATED STATE (SENSOR)
    // INDEX 1 : CHOSEN ACTION (TOKEN)    
    @Override
    public ArrayList generateState_picking (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateActionValueTable sTable, StateMap stMap) {
         
        
        ArrayList a = new ArrayList ();

        
        SensorList numbers = new SensorList (); 

        Sensor res = new Sensor (s1.tokenMap);
        
        
        Token t;
        
        // ACTION BETWEEN 0 AND 3
        
        double rand = Math.random();
                
        int chosenAction = (int) (rand * 3.99);

        
        Token chosenActionToken = s1.getToken(s1.size()-1);
        
        int forceRuleSet = -1;
        

        ArrayList sourceTokens = s1.tokenMap.getTokenList(s1.size()-1);
        
        ArrayList availableToken = new ArrayList ();
        
        for (int i = 0; i < sourceTokens.size(); i++) {
            
            Token tok = new Token ((String) sourceTokens.get(i), s1.size()-1, s1.tokenMap); 
            
            availableToken.add(tok);
        }

        //System.out.println("Available : " + availableToken);
        
        
        
        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            

        int hello = numbers.size();
        
        //double hello = (double) hello2 - 0.001;
          
        //System.out.println("Hello : " + hello);
        //numbers.printList("STUDY LIST");
        

        int index = sTable.findSensor(s1);
        
            if (numbers.size() > 3) {
                
                
            
                //System.out.println("Index of Sensor : " + index);
                
                if (index != -1) {
                    
                    if (sTable.getAction(index).size() == 4) {
                        
                        double rand2 = Math.random();
                        
                        if (rand2 > 0.5) {
                            int stock = sTable.findMaxValueIndex(index);

                            //System.out.println("Actions : " + sTable.getAction(index) + " Values : " + sTable.getValue(index));
                            //System.out.println("Max Value Index : " + stock);
                            chosenActionToken = sTable.getAction(index).get(stock);
                            //System.out.println("Action Token : " + chosenActionToken);


                            for (int i = 0; i < numbers.size(); i++) {

                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
                                    chosenAction = i;
                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
                                }
                            }
                        }
                        else {
                            
                            //return this.generateState_random(s1, rulesetlist, impossibleList, stMap);
//                            //System.out.println("RANDOM ACTION 4");
//                            //double rand = Math.random();
//                            //chosenAction = (int) Math.round(rand * hello) -1;
//                            //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
//                            
//                            chosenActionToken = generateRandomAction (s1, availableToken);
//                            
//                            for (int i = 0; i < numbers.size(); i++) {
//
//                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
//                                    chosenAction = i;
//                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
//                                }
                            }
                            
                        
                    }
                    
                    else {
                        
                        //return this.generateState_random(s1, rulesetlist, impossibleList, stMap);
//                        //System.out.println("RANDOM ACTION 3");
//                        //double rand = Math.random();
//                        //chosenAction = (int) Math.round(rand * hello) -1;
//                        //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
//                        
//                            
//                            chosenActionToken = generateRandomAction (s1, availableToken);
//                            
//                            for (int i = 0; i < numbers.size(); i++) {
//
//                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
//                                    chosenAction = i;
//                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
//                                }
//                            }
                    }
                }
               
                else {
                    
                    //return this.generateState_random(s1, rulesetlist, impossibleList, stMap);
                    
//                        //System.out.println("RANDOM ACTION");
//                        double rand = Math.random();
//                        chosenAction = (int) Math.round(rand * hello) -1;
//                        //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                    
                            
                            
                }   
               
                

            }
            
            else {
                
                
                //return this.generateState_random(s1, rulesetlist, impossibleList, stMap);
//                System.out.println("RANDOM ACTION 2");
//                double rand = Math.random();
//                chosenAction = (int) Math.round(rand * hello) -1;
//                System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                
                                            
//                chosenActionToken = generateRandomAction (s1, availableToken);
//                
//                for (int i = 0; i < numbers.size(); i++) {
//
//                    if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
//                        chosenAction = i;
//                        //System.out.println("Selecting : " + numbers.getSensor(i+1));
//                    }
//                }
                            
                
//                if (numbers.size() == 1)
//                    chosenAction = 0;
            }
            //System.out.println("Chosen Action : " + chosenAction + " > " + chosenActionToken);
            
            // BUG FIX 
//            if (chosenAction == -1)
//                chosenAction = 0;
//            
//            // WILDCARD FIX
//            if (chosenActionToken == s1.getToken(s1.size()-1)) {
//                
//                chosenActionToken = numbers.getSensor(chosenAction+1).getToken(s1.size()-1);
//            }
            //System.out.println("RAND IS : " + chosenAction);
            
            //for (int i = 0; i < numbers.size(); i++) {

//////                    System.out.print("    GENERATING STATE FOR " + numbers.getSensor((int) chosenAction) + "...");

            
            
         
        int row = stMap.findSensor(s1);
        
        
            
        //System.out.println("Index : " + index + " Row : " + row + " Action : " + chosenAction + " Action Token : " + chosenActionToken);
                    
        
       
        
        
        ArrayList possibleStates = (ArrayList) stMap.getReferencies(row).get(chosenAction);
        
        //System.out.println("States : " + possibleStates);
        
        int occurrencies = (int) stMap.getOccurencies(row).get(chosenAction);
        
        int indexOfAction = stMap.findActionIndex(row, chosenActionToken);
        
        if (indexOfAction == -1)
            indexOfAction = chosenAction;
        //System.out.println("Looking for " + chosenActionToken + " in " + stMap.getActions(row) + " result : " + indexOfAction);
        
        double occ = (double) occurrencies;
        
        double d = 0.0;
        
        double rand2 = Math.random();
        
        
        for (int y = 0; y < possibleStates.size(); y++) {
            
            ArrayList temp = (ArrayList) possibleStates.get(y);
            
            //System.out.println("List : " + index);
            int temp2 = (int) temp.get(1);
            
            double temp3 = (double) temp2;
            
            d = temp3/occ + d;
            //System.out.println("d : " + d);
            
            if (d > rand2) {
                a.add(stMap.getSensor((int) temp.get(0)));
                a.add(stMap.getActions(row).get(indexOfAction));
                //System.out.println("Action verif : " + stMap.getActions(row).get(indexOfAction));
                break;
            }
        }
        
        
        //Token actionFromInt = new Token (s1.tokenMap);
        
        
        return a;

     }    
    
    
 
    
    
    
    // GENERATES A STATE BY PICKING THE BEST ACTION (STATEMAP) 
    //
    // ONCE THE ACTION HAS BEEN CHOSEN, GENERATES A STATE USING THE STATEMAP, 
    // ACCORDING TO THE STATEMAP PROBABILITIES 
    @Override
    public Sensor generateState_bestAction (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, StateActionValueTable sTable, StateMap stMap) {
    
        

        int state_index = sTable.findSensor(s1);
        
        //System.out.println("State : " + s1);
        
        int max_value_index = sTable.findMaxValueIndex(state_index);
        
        
        Token best_action = sTable.getAction(state_index).get(max_value_index);
        
        //System.out.println("Best Action : " + best_action);
        
        int state_index_in_map = stMap.findSensor(s1);
        
        //System.out.println("state_index_in_map : " + state_index_in_map);
        
        
        int action_index_in_map = stMap.findActionIndex(state_index_in_map, best_action);
        
        //System.out.println("action_index_in_map : " + action_index_in_map);

        ArrayList possible_states = (ArrayList) stMap.getReferencies(state_index_in_map).get(action_index_in_map);
        
        //System.out.println("Possible States : " + possible_states);
        
        int occurencies = (int) stMap.getOccurencies(state_index_in_map).get(action_index_in_map);
        
        double occ = (double) occurencies;
        //System.out.println("Size : " + possible_states_size);
        
        double rand = Math.random();
       
        
        rand = rand * occ;
        
        int pick = (int) rand;
        
        //System.out.println("Pick is : " + pick);

        int d = 0;
        
        int sensor_to_pick = 0;
        
        for (int i = 0; i < possible_states.size(); i++) {
            
            ArrayList couple = (ArrayList) possible_states.get(i);
            
            int occ_of_couple = (int) couple.get(1);
            
            d = d + occ_of_couple;
            
            if ( d > pick) {
                
                sensor_to_pick = (int) couple.get(0);
                break;
            }
                
        }

        //System.out.println("Sensor is : " + sensor_to_pick);
        
        Sensor new_state = stMap.getSensor(sensor_to_pick);

        
        //System.out.println("Pick : " + sensor_to_pick);
        
        //System.out.println(s1 + " led to " + new_state);
        
        return new_state;

     }     
    
    
    
    // CHOOSES AN ACTION (BEST POSSIBLE ACTION FROM STATETABLE)   
    @Override
    public Token generateActionFromStateActionValueTable (Sensor s1, StateActionValueTable sTable) {
         
        
        ArrayList a = new ArrayList ();
        
        int chosenAction = 0;
        
        Token action = s1.getToken(s1.size()-1);

        int index = sTable.findSensor(s1);
       
        if (index != -1) {
                    
            int stock = sTable.findMaxValueIndex(index);

            if (stock != -1) 
                action = sTable.getAction(index).get(stock);
                            //System.out.println("Action Token : " + chosenActionToken);


            else {
                        // RANDOM
                        action = this.generateRandomAction(s1, sTable.getAction(index));
                }   

        }
            
        
     return action;

     }    
    
    
        // CHOOSES AN ACTION (BEST POSSIBLE ACTION FROM STATETABLE)   

        @Override
    public Token generateActionFromStateValueTable (Sensor s1, StateMap stMap, StateValueTable sValTab) {
         
        
        ArrayList a = new ArrayList ();
        
        int chosenAction = 0;
        
        Token action = s1.getToken(s1.size()-1);

        int index_of_current_state = sValTab.findSensor(s1);
       
        ArrayList container = new ArrayList ();
        
        container = this.generateStates_maps(s1, stMap);
        
        ArrayList ps = (ArrayList) container.get(0);
        
        double max_value = 0.0;
        
        
        
        for (int i = 0; i < ps.size(); i++) {
            
            double score = 0.0;
            
            StateList b = (StateList) ps.get(i);
            
            for (int j = 0; j < b.size(); j++) {
                
                int index_of_state = sValTab.findSensor(b.getSensor(j));
                
                score = score + b.getProb(j) * sValTab.getValue(index_of_state);
            }
            
            ArrayList ps2 = (ArrayList) container.get(1);
                
            Token saved_action = (Token) ps2.get(i);
            
            //System.out.println("State " + s1 + " going " + saved_action + " score : " + score);
            
            if (score > max_value) {
                
                max_value = score;
                
                
                action = (Token) ps2.get(i);
            }
        }
        
        
            
        
     return action;

     } 
    

    
    
    
    @Override
    public Token generateRandomAction (Sensor s1, ArrayList actions) {
        
        int size = actions.size();
        
        
        double rand = Math.random();
        
        double size2 = (double) size;
        
        rand = rand * size2;
        
        int res = (int) rand;
        
        return (Token) actions.get(res);
    }

 
   
}
