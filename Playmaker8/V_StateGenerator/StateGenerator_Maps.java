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
public class StateGenerator_Maps extends StateGenerator {
    
        public StateGenerator_Maps () {
    
    }
        
     // RETURNS AN ARRAYLIST OF ALL THE POSSIBLE STATES
     //    
     // INDEX 0 : ARRAYLIST CONTAINING THE STATELISTS
     // INDEX 1 : ARRAYLIST CONTAINING THE ACTIONS (TOKENS)
     //   
     // OF COURSE, STATELIST 1 CORRESPONDS TO ACTION 1   
     @Override
     public ArrayList  generateAllPossibleStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist, StateMap stMap) {
         

         ArrayList stateLists = new ArrayList ();
         ArrayList actionsList = new ArrayList ();
         
         
         ArrayList container = new ArrayList ();
         
         
         
         Token actions;
         
         
         // Index of Current State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         if (index_of_state_in_map != -1) {
             // Generating one StateList of possible Sensors for every possible best_action
             for (int action = 0; action < stMap.getReferencies(index_of_state_in_map).size(); action ++) {


                StateList possible_states = new StateList ();



                // Getting possible States referencies and the best_action in progress
                ArrayList <ArrayList> possibleStates = (ArrayList) stMap.getReferencies(index_of_state_in_map).get(action);

                actions = stMap.getActions(index_of_state_in_map).get(action).copy();


                // Getting occurrencies_double. to compute probs
                int total_occurencies_of_state_and_action = (int) stMap.getOccurencies(index_of_state_in_map).get(action);

                double total_occurencies_of_state_and_action_double = (double) total_occurencies_of_state_and_action;



                // Going through the above list, and adding each state with its prob
                for (int i = 0; i < possibleStates.size(); i++) {

                    // Couple is like (ref, occurrencies_double), so couple.get(0) is reference, 
                    // and couple.get(1) is occurrencies
                    ArrayList couple = (ArrayList) possibleStates.get(i);

                    // Prob part
                    int occurencies_of_action_and_state_i = (int) couple.get(1);

                    double prob = (double) occurencies_of_action_and_state_i;

                    prob = prob / total_occurencies_of_state_and_action_double;

                    // State Part
                    int new_state_index = (int) couple.get(0);

                    Sensor state = stMap.getSensor(new_state_index);

                    // Adding
                    possible_states.addSensor(state, prob);


                 }


                // Adding container complete Statelist, and its best_action
                stateLists.add(possible_states);
                actionsList.add(actions);

                //possible_states.printList(actions + "");
             }
             
         } // If
         
         // Adding all the StateLists & Actions to container
         container.add(stateLists);
         container.add(actionsList);
         
         
         return container;
     
            
            
     }
  
     
     
       
    // Same function than above, without unneccessary parameters, 
    // This avoids unnecessary parameters to be generated in PredatorApp
    // Not commented, exactly the same than above
    public ArrayList  generateStates_maps (Sensor s1, StateMap stMap) {
         

         ArrayList stateLists = new ArrayList ();
         ArrayList actionsList = new ArrayList ();
         
         
         ArrayList container = new ArrayList ();

         Token actions;

         // Index of State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         if (index_of_state_in_map == -1)
             return container;
         
         for (int action = 0; action < stMap.getReferencies(index_of_state_in_map).size(); action ++) {
        
             
             StateList possible_states = new StateList ();
             
             int index = stMap.findSensor(s1);


            ArrayList <ArrayList> possibleStates = (ArrayList) stMap.getReferencies(index).get(action);

            actions = stMap.getActions(index).get(action).copy();

             // Getting occurrencies_double. to compute probs
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

             stateLists.add(possible_states);
             actionsList.add(actions);
         }
         
         container.add(stateLists);
         container.add(actionsList);
         
         return container;

     }    
     
   
    
    
    
    
     
    // CHOOSES A RANDOM ACTION AND A PSEUDO-RANDOM STATE
    // RETURNS AN ARRAYLIST WITH :
    // INDEX 0 : GENERATED STATE (SENSOR)
    // INDEX 1 : CHOSEN ACTION (TOKEN)     
    @Override
    public ArrayList generateRandomActionAndPseudoRandomState (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap) {
         
         
       
        ArrayList container = new ArrayList ();

        double rand = Math.random();
        
        double rand2 = Math.random();
        
        double total_prob = 0.0;
        
        
        // RANDOM ACTION BETWEEN 0 AND 3
        
        
        int index = stMap.findSensor(s1);
        
        int taille = stMap.getActions(index).size();
        double c = (double) taille;
        c = c * rand;
        int action = (int) (c - 0.001);

        
        
        // From the random best_action, the possible States
        ArrayList possibleStates = (ArrayList) stMap.getReferencies(index).get(action);
        

        // Pseudo-Random State Selection
        int occurrencies = (int) stMap.getOccurencies(index).get(action);
        
        double occurrencies_double = (double) occurrencies;
        
        for (int y = 0; y < possibleStates.size(); y++) {
            
            ArrayList candidate = (ArrayList) possibleStates.get(y);

            int candidate_index = (int) candidate.get(1);
            
            double candidate_index_double = (double) candidate_index;
            
            total_prob = total_prob + candidate_index_double/occurrencies_double ;
            
            
            if (total_prob > rand2) {
                
                container.add(stMap.getSensor((int) candidate.get(0)));
                container.add(stMap.getActions(index).get(action));
                break; // We've found the Pseudo-Random candidate, we can return
            }
        }
        
        return container;
     }
    
    

    
    
    
    // CHOOSES THE BEST POSSIBLE ACTION FROM Action Value Table  
    @Override
    public Token generateActionFromStateActionValueTable (Sensor s1, StateActionValueTable sTable) {
         

        Token action = s1.getToken(s1.size()-1);

        int state_index = sTable.findSensor(s1);
       
        if (state_index != -1) {
                    
            // Finds the best best_action index (which is the best value index as well)
            int best_action_index = sTable.findMaxValueIndex(state_index);

            if (best_action_index != -1) 
                action = sTable.getAction(state_index).get(best_action_index);
                          
            else {
                        // RANDOM, never happens
                        action = this.generateRandomAction(sTable.getAction(state_index));
                }   

        }
            
        
     return action;

     }    
    
    
    
    
    
    // CHOOSES THE BEST ACTION FROM Value Table   
    @Override
    public Token generateActionFromStateValueTable (Sensor s1, StateMap stMap, StateValueTable sValTab) {
         
        

        Token best_action = s1.getToken(s1.size()-1);

        // Generates all the possible states and actions
        ArrayList container = this.generateStates_maps(s1, stMap);
        
        if (container.size() > 1) {
            ArrayList statelists = (ArrayList) container.get(0);

            ArrayList actions = (ArrayList) container.get(1);

            double max_value = 0.0;

            double reward = 1.0;

            // Goes through every StateLists
            for (int i = 0; i < statelists.size(); i++) {

                // If the score of a statelist is high, its action is a "good" action
                double score = 0.0;

                StateList possible_states = (StateList) statelists.get(i);

                for (int j = 0; j < possible_states.size(); j++) {

                    int index_of_state = sValTab.findSensor(possible_states.getSensor(j));

                    if (possible_states.getSensor(j).isRewarded())
                        score = score + possible_states.getProb(j) * (reward + sValTab.getValue(index_of_state));

                    else if (possible_states.getSensor(j).is_negative_rewarded())
                        score = score + possible_states.getProb(j) * (-1.0 * reward + sValTab.getValue(index_of_state));

                    else 
                        score = score + possible_states.getProb(j) * sValTab.getValue(index_of_state);
                }

                
                // Is it the best action yet ?    
                if (score > max_value) {

                    max_value = score;


                    best_action = (Token) actions.get(i);
                }
            }
        }
        
        else {
            ArrayList actions = new ArrayList ();
            
            for (int i = 0; i < s1.tokenMap.getTokenList(s1.size()-1).size(); i++) {
                
                Token a = new Token ((String) s1.tokenMap.getTokenList(s1.size()-1).get(i), s1.size()-1, s1.tokenMap);
                actions.add(a);
            }
            
            best_action = this.generateRandomAction(actions);
        }
        

     return best_action;

     } 
    

    
    
    // Selects a random action from the input ArrayList
    @Override
    public Token generateRandomAction (ArrayList actions) {
        
        int size = actions.size();
        
        
        double rand = Math.random();
        
        double size2 = (double) size;
        
        rand = rand * size2;
        
        int res = (int) rand;
        
        return (Token) actions.get(res);
    }

 
   
} // END OF FILE
