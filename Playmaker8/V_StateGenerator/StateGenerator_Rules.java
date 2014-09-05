/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_Sensors.StateMap;
import V_ReinforcementLearner.StateActionValueTable;
import V_ReinforcementLearner.StateValueTable;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class StateGenerator_Rules extends StateGenerator {
    
    public StateGenerator_Rules () {
    
    }
        
    
     // RETURNS AN ARRAYLIST OF ALL THE POSSIBLE STATES
     //    
     // INDEX 0 : ARRAYLIST CONTAINING THE STATELISTS
     // INDEX 1 : ARRAYLIST CONTAINING THE ACTIONS (TOKENS)
     // INDEX 2 : LIST OF CHOSEN RULESETS (RVLR) 
     //   
     // OF COURSE, STATELIST 1 CORRESPONDS TO ACTION 1    
     @Override
     public ArrayList  generateAllPossibleStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist, StateMap stMap) {
         

        ArrayList stateLists = new ArrayList ();
        ArrayList actionsList = new ArrayList ();
        ArrayList chosenRuleSets = new ArrayList();
        
        ArrayList container = new ArrayList ();
         
        
        SensorList current_state_with_expanded_actions = new SensorList (); 

        //System.out.println("State : " + s1);
        
        // Expanding current_state with possible actions
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            current_state_with_expanded_actions.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { current_state_with_expanded_actions.addSensor(s1); }
        

        
        for (int i = 0; i < current_state_with_expanded_actions.size(); i++) {
            
            
            StateList possible_states = new StateList ();
            ArrayList rulesets_for_action = new ArrayList ();
            Token random_action;
            
            // SETTING REF OF OUR RANDOM ACTION
            random_action = current_state_with_expanded_actions.getSensor(i + 1).getToken(s1.size()-1).copy();
            
            
            // GENERATES ALL STATES FROM STATE S1 AND ACTION random_action
            ArrayList <Integer> aList = new ArrayList();
            


            
            // Choosing RuleSets until we fill up the whole Sensor        
            while ( (aList.size()+1)!= s1.size()) {

                int chosen_ruleset = current_state_with_expanded_actions.getSensor(i + 1).chooseNextRuleSet(rulesetlist, aList);


                //System.out.println("\nChosen RuleSet : ");
                //rulesetlist.getRuleSet(chosen_ruleset).printRules();
                
                
                rulesets_for_action.add(chosen_ruleset);
               
                
                
                
                aList.addAll(rulesetlist.getRuleSet(chosen_ruleset).detectNonWildcardedSpots());

                

                possible_states.update(rulesetlist.getRuleSet(chosen_ruleset), impossibleList);

            }

            possible_states.clean(impossibleList);


            stateLists.add(possible_states);
            
            actionsList.add(random_action);
            
            chosenRuleSets.add(rulesets_for_action);
            
            //possible_states.printList(random_action + "");
             
             
        }
         
         container.add(stateLists);
         container.add(actionsList);
         container.add(chosenRuleSets);
         
        return container;
     }
    
    
    
     
     
     

    // CHOOSES A RANDOM ACTION, AND GENERATES A STATE 
    // RETURNS AN ARRAYLIST WITH :
    // INDEX 0 : GENERATED STATE (SENSOR)
    // INDEX 1 : CHOSEN ACTION (TOKEN)
    // INDEX 2 : LIST OF CHOSEN RULESETS (RVLR) 
    @Override
    public ArrayList generateRandomActionAndPseudoRandomState (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap) {
         
         
       
        ArrayList container = new ArrayList ();

        ArrayList chosen_rulesets = new ArrayList ();
        
        SensorList s1_with_actions_expand = new SensorList (); 

        Sensor new_state = new Sensor (s1.tokenMap);
        
        
        Token action;
        

        // Used to resolve some cases where the same RuleSet needs to be used again
        int forceRuleSet = -1;
        int stuck = 0;
        

        // Generates all the possible full Sensors (with action included)
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            s1_with_actions_expand.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { s1_with_actions_expand.addSensor(s1); }
            
        

        ArrayList <Integer> covered_indexes;


        // SELECTS A RANDOM ACTION
        double rand = Math.random();

        // A RAND BETWEEN 0 AND numers.size
        if (s1_with_actions_expand.size() > 1)
            rand = Math.round(rand * s1_with_actions_expand.size());

        else 
            rand = 0;


        covered_indexes = new ArrayList();


        
        
        
        // While we have not covered all the indexes of new_state
        while ( (covered_indexes.size()+1)!= s1.size()) {

            
            // A state is Valid when all of its indexes are covered, except the action slot
            boolean isValid = false;
            stuck = 0;
            
            while (!isValid) {
                
                // Copying
                Sensor copy = new_state.merge(new_state);
                ArrayList <Integer> copy2 = new ArrayList ();
                
                for (int i = 0; i < covered_indexes.size(); i++) {

                    copy2.add(covered_indexes.get(i));

                }


                
                // Choosing RuleSet
                int chosen_ruleset;
                
                // Force RuleSet if last step was wrong, see below
                if (forceRuleSet == -1) {
                    chosen_ruleset = s1_with_actions_expand.getSensor((int) rand +1).chooseNextRuleSet(rulesetlist, covered_indexes);
                    
                }
                else {
                    chosen_ruleset = forceRuleSet;
                    forceRuleSet = -1;
                }
                
                
                for (int u = 0; u < rulesetlist.getRuleSet(chosen_ruleset).size(); u++) {
                    
                    chosen_rulesets.add(rulesetlist.getRuleSet(chosen_ruleset).getRule(u).id);
                    
                }
                
                // Updates the indexes covered
                covered_indexes.addAll(rulesetlist.getRuleSet(chosen_ruleset).detectNonWildcardedSpots());


                Sensor postcondition = rulesetlist.getRuleSet(chosen_ruleset).chooseRandomRule().getPostcondition();
                
                
                new_state = new_state.merge(postcondition);

                
                int impossible_state_index = impossibleList.findSensor2(new_state);

                //System.out.println("New State : " + new_state);

                // State is not in the Impossible List, it is valid
                if ( impossible_state_index == 0) {

                    if (new_state.numberOfWildcards() == 1) {
                        //System.out.println("\n\nFinal State : " + new_state);
                        isValid = true;
                    }

                }

                else {

                    // Sensor is Impossible, we go back to the previous stage

                    new_state = copy;
                   
                    covered_indexes.clear();
                        
                    for (int o = 0; o < copy2.size(); o++) {

                        covered_indexes.add(copy2.get(o));
                    }
                   

                    // But we use the same RuleSet
                    forceRuleSet = chosen_ruleset;
                    
                    stuck ++;

                    // Sometimes, the algortihm got stuck for low input data
                    //
                    // If it is stuck for more than 3 steps, we return the current sensor with the action generated
                    if (stuck > 3) {
                        action = s1_with_actions_expand.getSensor((int) rand +1).getToken(s1_with_actions_expand.getSensor((int) rand +1).size()-1);
                        container.add(s1);
                        container.add(action);
                        return container;
                        
                    }
                }
            }
            
            

        }

        
        action = s1_with_actions_expand.getSensor((int) rand +1).getToken(s1_with_actions_expand.getSensor((int) rand +1).size()-1);


        container.add(new_state);

        container.add(action);
        
        container.add(chosen_rulesets);
        
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
    
    
    
    
    // Same function than in Maps
    // Accelerates the process to find possible States
    // Only used in some specific cases
    public ArrayList  generateStates_maps (Sensor s1, StateMap stMap) {
         

         ArrayList stateLists = new ArrayList ();
         ArrayList actionsList = new ArrayList ();
         
         
         ArrayList container = new ArrayList ();

         Token actions;

         // Index of State
         int index_of_state_in_map = stMap.findSensor(s1);
         
         
         for (int action = 0; action < s1.tokenMap.getTokenList(s1.size()-1).size(); action ++) {
        
             
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
    
    
     
    
    
    // CHOOSES THE BEST ACTION FROM Value Table   
    @Override
    public Token generateActionFromStateValueTable (Sensor s1, StateMap stMap, StateValueTable sValTab) {
         
        

        Token best_action = s1.getToken(s1.size()-1);

        // Generates all the possible states and actions
        ArrayList container = this.generateStates_maps(s1, stMap);
        
        ArrayList statelists = (ArrayList) container.get(0);
        
        ArrayList actions = (ArrayList) container.get(1);
        
        double max_value = 0.0;
        
        
        // Goes through every StateLists
        for (int i = 0; i < statelists.size(); i++) {
            
            // If the score of container statelist is high, its action is container "good" action
            double score = 0.0;
            
            StateList possible_states = (StateList) statelists.get(i);
            
            for (int j = 0; j < possible_states.size(); j++) {
                
                int index_of_state = sValTab.findSensor(possible_states.getSensor(j));
                
                score = score + possible_states.getProb(j) * sValTab.getValue(index_of_state);
            }
            
            
            // Is it the best action yet ?    
            if (score > max_value) {
                
                max_value = score;
                
                
                best_action = (Token) actions.get(i);
            }
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