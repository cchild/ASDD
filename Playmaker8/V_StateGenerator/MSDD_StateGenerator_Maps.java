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
public class MSDD_StateGenerator_Maps extends StateGenerator {
    
        public MSDD_StateGenerator_Maps () {
    
    }
        
        
     @Override
     public ArrayList <ArrayList> generateStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist) {
         

        //ArrayList <Integer> chosenRuleSets = new ArrayList();
        ArrayList stateLists = new ArrayList();
        
        SensorList numbers = new SensorList (); 

        StateList possibleList = sList.getPossibleList(s1, sMap);
        

        
        
        
        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        //numbers.printList("STUDY LIST");
        
        for (int i = 0; i < numbers.size(); i++) {

                System.out.print("GENERATING STATES FOR " + numbers.getSensor(i+1) + "...");

                ArrayList <Integer> aList = new ArrayList();
                ArrayList <Integer> chosenRuleSets = new ArrayList();
                StateList stateList = new StateList();


                while ( (aList.size()+1)!= s1.size()) {

                    //System.out.println("\nCHOOSING NEXT RULESET...");
                    int chosen = numbers.getSensor(i+1).chooseNextRuleSet(rulesetlist, aList);
                    //System.out.println("CHOSEN RULESET : " + chosen);
                    //rulesetlist.getRuleSet(chosen).print();

                    aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                    chosenRuleSets.add(chosen);
                    //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                    //System.out.println("UPDATING STATELIST... ");
                    stateList.update(rulesetlist.getRuleSet(chosen), impossibleList);

                }

                if (hard_clean_statelist) {
                    //System.out.println("\nHARD CLEANING STATELIST ");
                    stateList.clean_hard(impossibleList, possibleList);
                }
                else {
                    //System.out.println("\nCLEANING STATELIST ");
                    stateList.clean(impossibleList);

                }
                Token action = numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1);
                System.out.println(" OK");
                ArrayList temp = new ArrayList ();
                temp.add(stateList);
                temp.add(action);
                
                stateLists.add(temp);

            }

            System.out.println("GENERATED " + numbers.size() + " StateLists.");


//            for (int i = 0; i < stateLists.size(); i++) {
//
//                stateLists.get(i).printList("ACTION = " + numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1));
//            }


            //possibleList.printList("REAL ONE");
            
            
            
            return stateLists;
            
            
     }
     

     
    // CHOOSES A RANDOM ACTION, AND RETURNS AN ARRAYLIST WITH :
    // INDEX 0 : GENERATED STATE (SENSOR)
    // INDEX 1 : CHOSEN ACTION (TOKEN)     
    @Override
    public ArrayList generateState_random (Sensor s1,SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap) {
         
         
       
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
    public ArrayList generateState_picking (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateTable sTable, StateMap stMap) {
         
        
        ArrayList a = new ArrayList ();

        
        SensorList numbers = new SensorList (); 

        Sensor res = new Sensor (s1.tokenMap);
        
        
        Token t;
        
        
        int chosenAction = 0;
        
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
                        
                        if (rand2 > 0.2) {
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
                            //System.out.println("RANDOM ACTION 4");
                            //double rand = Math.random();
                            //chosenAction = (int) Math.round(rand * hello) -1;
                            //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                            
                            chosenActionToken = generateRandomAction (s1, availableToken);
                            
                            for (int i = 0; i < numbers.size(); i++) {

                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
                                    chosenAction = i;
                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
                                }
                            }
                            
                        }
                    }
                    
                    else {
                        //System.out.println("RANDOM ACTION 3");
                        //double rand = Math.random();
                        //chosenAction = (int) Math.round(rand * hello) -1;
                        //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                        
                            
                            chosenActionToken = generateRandomAction (s1, availableToken);
                            
                            for (int i = 0; i < numbers.size(); i++) {

                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
                                    chosenAction = i;
                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
                                }
                            }
                    }
                }
               
                else {
                        //System.out.println("RANDOM ACTION");
                        double rand = Math.random();
                        chosenAction = (int) Math.round(rand * hello) -1;
                        //System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                    
                            
                            
                }   
               
                

            }
            
            else {
                
//                System.out.println("RANDOM ACTION 2");
//                double rand = Math.random();
//                chosenAction = (int) Math.round(rand * hello) -1;
//                System.out.println("rand : " + rand + " & chosenAction : " + chosenAction);
                
                                            
                chosenActionToken = generateRandomAction (s1, availableToken);
                
                for (int i = 0; i < numbers.size(); i++) {

                    if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
                        chosenAction = i;
                        //System.out.println("Selecting : " + numbers.getSensor(i+1));
                    }
                }
                            
                
//                if (numbers.size() == 1)
//                    chosenAction = 0;
            }
            //System.out.println("Chosen Action : " + chosenAction + " > " + chosenActionToken);
            
            // BUG FIX 
            if (chosenAction == -1)
                chosenAction = 0;
            
            // WILDCARD FIX
            if (chosenActionToken == s1.getToken(s1.size()-1)) {
                
                chosenActionToken = numbers.getSensor(chosenAction+1).getToken(s1.size()-1);
            }
            //System.out.println("RAND IS : " + chosenAction);
            
            //for (int i = 0; i < numbers.size(); i++) {

//////                    System.out.print("    GENERATING STATE FOR " + numbers.getSensor((int) chosenAction) + "...");

            
            
         
        int row = stMap.findSensor(s1);
        
        
            
        //System.out.println("Index : " + index + " Row : " + row + " Action : " + chosenAction + " Action Token : " + chosenActionToken);
                    
        
       
        
        
        ArrayList possibleStates = (ArrayList) stMap.getReferencies(row).get(chosenAction);
        
        //System.out.println("States : " + possibleStates);
        
        int occurrencies = (int) stMap.getOccurencies(row).get(chosenAction);
        
        int indexOfAction = stMap.findActionIndex(row, chosenActionToken);
        
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
    public Sensor generateState_bestAction (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, StateTable sTable, StateMap stMap) {
    
        

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
    public Token generateActionFromTable (Sensor s1, StateTable sTable) {
         
        
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
