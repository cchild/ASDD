/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_ReinforcementLearner.StateMap;
import V_ReinforcementLearner.StateActionValueTable;
import V_ReinforcementLearner.StateValueTable;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class MSDD_StateGenerator_Rules extends StateGenerator {
    
    public MSDD_StateGenerator_Rules () {
    
    }
        
    
     // RETURNS AN ARRAYLIST OF ALL THE POSSIBLE STATES
     //    
     // INDEX 0 : ARRAYLIST CONTAINING THE STATELISTS
     // INDEX 1 : ARRAYLIST CONTAINING THE ACTIONS (TOKENS)
     //   
     // OF COURSE, STATELIST 1 CORRESPONDS TO ACTION 1    
     @Override
     public ArrayList  generateStates (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList, boolean hard_clean_statelist, StateMap stMap) {
         

        /*
        ArrayList stateLists = new ArrayList ();
        ArrayList actionsList = new ArrayList ();
        
        
        ArrayList res = new ArrayList ();
        
        
        Token action;
        SensorList numbers = new SensorList (); 

        StateList possibleList = sList.getPossibleList(s1, sMap);

        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        numbers.printList("STUDY LIST");
        
        for (int i = 0; i < numbers.size(); i++) {

                System.out.print("GENERATING STATES FOR " + numbers.getSensor(i+1) + "...");

                action = numbers.getSensor(i+1).getToken(s1.size()-1).copy();
                
                ArrayList <Integer> aList = new ArrayList();
                //ArrayList <Integer> chosenRuleSets = new ArrayList();
                StateList stateList = new StateList();


                while ( (aList.size()+1)!= s1.size()) {

                    //System.out.println("\nCHOOSING NEXT RULESET...");
                    int chosen = numbers.getSensor(i+1).chooseNextRuleSet(rulesetlist, aList);
                    //System.out.println("CHOSEN RULESET : " + chosen);
                    //rulesetlist.getRuleSet(chosen).print();

                    aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                    //chosenRuleSets.add(chosen);
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
                
                System.out.println(" OK");
                ArrayList temp = new ArrayList ();
                stateLists.add(stateList);
                
                actionsList.add(action);

            }

            //System.out.println("GENERATED " + numbers.size() + " StateLists.");
                
            
            res.add(stateLists);
            res.add(actionsList);
            
            
            return res;
                     
     }
     */

     

        
        
        ArrayList stateLists = new ArrayList ();
        ArrayList actionsList = new ArrayList ();
        
        
        ArrayList res = new ArrayList ();
        
        ArrayList container = new ArrayList ();
         
        
        SensorList numbers = new SensorList (); 

        
        
        //StateList possibleList = sList.getPossibleList(s1, sMap);

        
        // IN ORDER TO PICK AN ACTION, WE LIST THE SENSORS FORMED BY
        // MERGING S1 TO EVERY ACTION, THIS LIST IS CALLED NUMBERS
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
        
        
        //numbers.printList("STUDY LIST");
        //int size_of_actions_list = numbers.size();
        
        //double soal = (double) size_of_actions_list;
        //System.out.println("Sizes " + size_of_actions_list + " " + soal);
        //double rand = Math.random();
        
        //rand = rand * soal;
        //System.out.println(rand);
        //int final_rand = (int) rand;
        
        //if (final_rand == 0)
            //final_rand = 1;
        
        for (int final_rand = 0; final_rand < numbers.size(); final_rand++) {
            //System.out.println("Final rand : " + final_rand);
            StateList possible_states = new StateList ();
            Token random_action = new Token (s1.tokenMap);
            
            // SETTING REF OF OUR RANDOM ACTION
            random_action = numbers.getSensor(final_rand + 1).getToken(s1.size()-1).copy();
            
            //System.out.println("Rand Action : " + random_action);

                    // GENERATES ALL STATES FROM STATE S1 AND ACTION random_action
                    ArrayList <Integer> aList = new ArrayList();
                    ArrayList <Integer> chosenRuleSets = new ArrayList();



                    while ( (aList.size()+1)!= s1.size()) {

                        //System.out.println("\nCHOOSING NEXT RULESET...");
                        //System.out.println("Sensor : " + numbers.getSensor(final_rand));
                        int chosen = numbers.getSensor(final_rand + 1).chooseNextRuleSet(rulesetlist, aList);
                        //System.out.println("CHOSEN RULESET : " + chosen);
                        //rulesetlist.getRuleSet(chosen).print();

                        aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                        chosenRuleSets.add(chosen);

                        //System.out.println("UPDATING STATELIST... ");

                        possible_states.update(rulesetlist.getRuleSet(chosen), impossibleList);

                    }

                    possible_states.clean(impossibleList);

                    //System.out.print("State : " + s1 + " action : " + random_action);
             
                    //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);






             stateLists.add(possible_states);

             actionsList.add(random_action);
             
             
        }
         
         res.add(stateLists);
         res.add(actionsList);

        return res;
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
        
        
        double rand;
        
        int forceRuleSet = -1;
        
        

        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        //numbers.printList("STUDY LIST");
        

            ArrayList <Integer> aList;
            
                
            // SELECTS A RANDOM ACTION
            rand = Math.random();

            if (numbers.size() > 1)
                // A RAND BETWEEN 0 AND numers.size
                rand = Math.round(rand * numbers.size());

            else 
                rand = 0;

            
                    aList = new ArrayList();



                    while ( (aList.size()+1)!= s1.size()) {

                        boolean isValid = false;

                        //System.out.println("\nCHOOSING NEXT RULESET...");
                        
                        //System.out.println("COPY S : " + copy + " & COPY LIST : " + copy2);
                        while (!isValid) {
                            
                            
                            Sensor copy = res.merge(res);
                            ArrayList <Integer> copy2 = new ArrayList ();
                            for (int i = 0; i < aList.size(); i++) {
                            
                                copy2.add(aList.get(i));
                                
                            }
                        

                            
                            
                            //System.out.println("Rand : " + chosenAction + " action : " + numbers.getSensor((int)chosenAction+1).getToken(5));
                            
                            int chosen;
                            if (forceRuleSet == -1)
                                chosen = numbers.getSensor((int) rand +1).chooseNextRuleSet(rulesetlist, aList);
                            else {
                                chosen = forceRuleSet;
                                forceRuleSet = -1;
                            }
                            //System.out.println("CHOSEN RULESET : " + chosen);
                            //rulesetlist.getRuleSet(chosen).print();

                            //System.out.println("ALIST : " + aList);
                            aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                            
                            Sensor bb = rulesetlist.getRuleSet(chosen).chooseRandomRule().getPostcondition();
                            //System.out.println("Merging " + res + " with " + bb);
                            res = res.merge(bb);

                            //System.out.println("RES : " + res);
                            //System.out.println("ALIST : " + aList);
                            
                            int rr = impossibleList.findSensor2(res);
                            
                            //System.out.println(" COUNT IS : " + rr);
                            
                            if ( rr == 0) {
                                
                                if (res.numberOfWildcards() == 1) {
                                 
                                    isValid = true;
                                }
                                
                     
                                
                            }
                            
                            else {
                                
                                //System.out.println("IMP STATE : " + res);
                                    
                                    res = copy;
                                //System.out.println("RESET RES TO : " + res);    
                                    aList.clear();
                                    for (int o = 0; o < copy2.size(); o++) {
                                        
                                        aList.add(copy2.get(o));
                                    }
                                //System.out.println("RESET ALIST TO : " + aList);
                                
                                forceRuleSet = chosen;
                                
                            }
                        }

                    }
                    
                    
                    
                    t = numbers.getSensor((int) rand +1).getToken(numbers.getSensor((int) rand +1).size()-1);
                    
                    
                    a.add(res);
                    
                    a.add(t);

         
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
        
        
        int chosenAction = 0;
        
        Token chosenActionToken;
        
        int forceRuleSet = -1;
        
        
        
        ArrayList sourceTokens = s1.tokenMap.getTokenList(s1.size()-1);
        
        ArrayList availableToken = new ArrayList ();
        
        for (int i = 0; i < sourceTokens.size(); i++) {
            
            Token tok = new Token ((String) sourceTokens.get(i), s1.size()-1, s1.tokenMap); 
            
            availableToken.add(tok);
        }
        
        

        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        //numbers.printList("STUDY LIST");
        

            ArrayList <Integer> aList;
            
                
            // SELECTS A RANDOM ACTION
            
            int temp = sTable.findSensor(s1);
            
            if (numbers.size() > 3) {
                
                
            
                //System.out.println("Index of Sensor : " + temp);
                
                if (temp != -1) {
                    
                    if (sTable.getAction(temp).size() == 4) {
                        
                        double rand2 = Math.random();
                        
                        if (rand2 > 0.2) {
                            int stock = sTable.findMaxValueIndex(temp);

                            //System.out.println("Actions : " + sTable.getAction(temp) + " Values : " + sTable.getValue(temp));
                            //System.out.println("Max Value Index : " + action_index);
                            chosenActionToken = sTable.getAction(temp).get(stock);
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
                
                //System.out.println("RANDOM ACTION 2");
                chosenActionToken = generateRandomAction (s1, availableToken);
                            
                            for (int i = 0; i < numbers.size(); i++) {

                                if (chosenActionToken.match_exact(numbers.getSensor(i+1).getToken(s1.size()-1))) {
                                    chosenAction = i;
                                    //System.out.println("Selecting : " + numbers.getSensor(i+1));
                                }
                            }
                
                if (numbers.size() == 1)
                    chosenAction = 0;
            }
                
            
            if (numbers.size() == -1)
                chosenAction = 0;
             //System.out.println("Index : " + temp + " Action : " + chosenAction );
        
            //System.out.println("RAND IS : " + chosenAction);
            
            //for (int i = 0; i < numbers.size(); i++) {

//////                    System.out.print("    GENERATING STATE FOR " + numbers.getSensor((int) chosenAction) + "...");

                    

                    aList = new ArrayList();



                    while ( (aList.size()+1)!= s1.size()) {

                        boolean isValid = false;

                        //System.out.println("\nCHOOSING NEXT RULESET...");
                        
                        //System.out.println("COPY S : " + copy + " & COPY LIST : " + copy2);
                        while (!isValid) {
                            
                            
                            Sensor copy = res.merge(res);
                            ArrayList <Integer> copy2 = new ArrayList ();
                            for (int i = 0; i < aList.size(); i++) {
                            
                                copy2.add(aList.get(i));
                                
                            }
                        

                            
                            
                            //System.out.println("Rand : " + chosenAction + " action : " + numbers.getSensor((int)chosenAction+1).getToken(5));
                            
                            int chosen;
                            if (forceRuleSet == -1)
                                chosen = numbers.getSensor((int) chosenAction +1).chooseNextRuleSet(rulesetlist, aList);
                            else {
                                chosen = forceRuleSet;
                                forceRuleSet = -1;
                            }
                            //System.out.println("CHOSEN RULESET : " + chosen);
                            //rulesetlist.getRuleSet(chosen).print();

                            //System.out.println("ALIST : " + aList);
                            aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                            
                            Sensor bb = rulesetlist.getRuleSet(chosen).chooseRandomRule().getPostcondition();
                            //System.out.println("Merging " + res + " with " + bb);
                            res = res.merge(bb);

                            //System.out.println("RES : " + res);
                            //System.out.println("ALIST : " + aList);
                            
                            int rr = impossibleList.findSensor2(res);
                            
                            //System.out.println(" COUNT IS : " + rr);
                            
                            if ( rr == 0) {
                                
                                if (res.numberOfWildcards() == 1) {
                                 
                                    isValid = true;
                                }
                                
                     
                                
                            }
                            
                            else {
                                
                                //System.out.println("IMP STATE : " + res);
                                    
                                    res = copy;
                                //System.out.println("RESET RES TO : " + res);    
                                    aList.clear();
                                    for (int o = 0; o < copy2.size(); o++) {
                                        
                                        aList.add(copy2.get(o));
                                    }
                                //System.out.println("RESET ALIST TO : " + aList);
                                
                                forceRuleSet = chosen;
                                
                            }
                        }
                        
                        
                        //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                        //System.out.println("UPDATING STATELIST... ");
                        //stateList.update(rulesetlist.getRuleSet(chosen), impossibleList);

                    
                    }
                    
                    
                    
                    t = numbers.getSensor((int) chosenAction +1).getToken(numbers.getSensor((int) chosenAction +1).size()-1);
                    
                    
                    a.add(res);
                    
                    a.add(t);

        return a;
     }    
    
    
    
     // PICKS A RANDOM ACTION, GENERATES ALL THE CORRESPONDING STATES
     // RETURNS ARRAYLIST WITH : 
     // INDEX 0 : STATELIST (POSSIBLE STATES)
     // INDEX 1 : THE RANDOM CHOSEN ACTION
     @Override
     public ArrayList generateStates2 (Sensor s1, RuleSetList rulesetlist, SensorList impossibleList, StateMap stMap, SensorList sList, SensorMap sMap) {
         
         ArrayList container = new ArrayList ();
         
        Token random_action = new Token (s1.tokenMap);
        
        SensorList numbers = new SensorList (); 

        StateList possible_states = new StateList ();
        
        //StateList possibleList = sList.getPossibleList(s1, sMap);

        
        // IN ORDER TO PICK AN ACTION, WE LIST THE SENSORS FORMED BY
        // MERGING S1 TO EVERY ACTION, THIS LIST IS CALLED NUMBERS
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
        
        int size_of_actions_list = numbers.size();
        
        double soal = (double) size_of_actions_list;
        //System.out.println("Sizes " + size_of_actions_list + " " + soal);
        double rand = Math.random();
        
        rand = rand * soal;
        //System.out.println(rand);
        int final_rand = (int) rand;
        
        //if (final_rand == 0)
            //final_rand = 1;
        
        //System.out.println("Final rand : " + final_rand);
        // SETTING REF OF OUR RANDOM ACTION
        random_action.setReference(numbers.getSensor(final_rand + 1).getToken(s1.size()-1).getReference());
        random_action.setPosition(numbers.getSensor(final_rand + 1).getToken(s1.size()-1).getPosition());
        
        //System.out.println("Rand Action : " + random_action);
        
                // GENERATES ALL STATES FROM STATE S1 AND ACTION random_action
                ArrayList <Integer> aList = new ArrayList();
                ArrayList <Integer> chosenRuleSets = new ArrayList();
                


                while ( (aList.size()+1)!= s1.size()) {

                    //System.out.println("\nCHOOSING NEXT RULESET...");
                    //System.out.println("Sensor : " + numbers.getSensor(final_rand));
                    int chosen = numbers.getSensor(final_rand + 1).chooseNextRuleSet(rulesetlist, aList);
                    //System.out.println("CHOSEN RULESET : " + chosen);
                    //rulesetlist.getRuleSet(chosen).print();

                    aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                    chosenRuleSets.add(chosen);
                    
                    //System.out.println("UPDATING STATELIST... ");
                    
                    possible_states.update(rulesetlist.getRuleSet(chosen), impossibleList);

                }

                possible_states.clean(impossibleList);

                
                System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                
        
        
         
         
         container.add(possible_states);
         
         container.add(random_action);
         
         
         return container;
     }
    
    
    
    // GENERATES A STATE BY PICKING THE BEST ACTION (STATEMAP) 
    //
    // ONCE THE ACTION HAS BEEN CHOSEN, GENERATES A STATE USING THE STATEMAP, 
    // ACCORDING TO THE STATEMAP PROBABILITIES 
    @Override
    public Sensor generateState_bestAction (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList possibleList, StateActionValueTable sTable, StateMap stMap) {
    
        

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
    // RANDOM PART ? CURRENTLY 20%
    @Override
    public Token generateActionFromStateActionValueTable (Sensor s1, StateActionValueTable sTable) {
         
        
        ArrayList a = new ArrayList ();
        
        int chosenAction = 0;
        
        Token action = s1.getToken(s1.size()-1);
        
        double rand2 = Math.random();
        
        
            int state_index = sTable.findSensor(s1);

            if (state_index != -1) {

                int action_index = sTable.findMaxValueIndex(state_index);
                
                //System.out.println("Max value of : " + sTable.getAction(state_index) + " " + sTable.getValue(state_index) + " is : " + action_index);
                
                //System.out.println("Sum is : " + sTable.getSumOfRow(state_index));

                if (action_index != -1) 
                    action = sTable.getAction(state_index).get(action_index);
                                //System.out.println("Action Token : " + chosenActionToken);  

            }
        
        
//        else {
//            
//            double rand = Math.random();
//            chosenAction = (int) Math.round(rand * sTable.getAction(0).size()) -1;
//            if (chosenAction == -1)
//                chosenAction = 0;
//
//            System.out.println("Random !");
//            action = sTable.getAction(0).get(chosenAction);
//        }
            
        
     return (Token) action;

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
    
    
} // END FILE