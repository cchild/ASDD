/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_StateGenerator;

import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class MSDD_StateGenerator extends StateGenerator {
    
        public MSDD_StateGenerator () {
    
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
     

     
     
        @Override
    public ArrayList generateState (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList) {
         
         
       
        ArrayList a = new ArrayList ();

        
        SensorList numbers = new SensorList (); 

        Sensor res = new Sensor (s1.tokenMap);
        
        
        Token t = res.getToken(0);
        
        double rand = 0.0;
        
        int forceRuleSet = -1;
        
        

        
        if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        //numbers.printList("STUDY LIST");
        

            ArrayList <Integer> aList;
            
 
            
            //System.out.println("RAND IS : " + rand);
            
            //for (int i = 0; i < numbers.size(); i++) {

//////                    System.out.print("    GENERATING STATE FOR " + numbers.getSensor((int) rand) + "...");

                    

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
                        
                                       // SELECTS A RANDOM ACTION
                            rand = Math.random();
                            
                            if (numbers.size() > 1)
                                rand = Math.round(rand * numbers.size());
            
                            else 
                                rand = 0;
                            
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
                        
                        
                        //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                        //System.out.println("UPDATING STATELIST... ");
                        //stateList.update(rulesetlist.getRuleSet(chosen), impossibleList);

                    
                    }
                    
                    
                    
                    t = numbers.getSensor((int) rand +1).getToken(numbers.getSensor((int) rand +1).size()-1);
                    
                    
                    a.add(res);
                    
                    a.add(t);
                    //System.out.println("GENERATED STATE IS : " + res);
                

                //System.out.println("GENERATED " + numbers.size() + " StateLists.");


    //            for (int i = 0; i < stateLists.size(); i++) {
    //
    //                stateLists.get(i).printList("ACTION = " + numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1));
    //            }


         
        return a;
     }
    
    
    
    
    
    @Override
    public ArrayList generateState2 (Sensor s1, SensorList sList, SensorMap sMap, RuleSetList rulesetlist, SensorList impossibleList) {
         
         
       
        ArrayList a = new ArrayList ();

        
        SensorList numbers = new SensorList (); 

        Sensor res = new Sensor (s1.tokenMap);
        
        
        Token t;
        
    
        int forceRuleSet = -1;
        
      if (s1.getToken(s1.size()-1).isWildcard()) {
            
            numbers.addSensorList(s1.expand(s1.size()-1));
        }
        
        else { numbers.addSensor(s1); }
            
        
        //numbers.printList("STUDY LIST");
        

            ArrayList <Integer> aList;
            
 
            int rand = 0;
            double whichone_score = 0.0;
            Token which_action = new Token (res.tokenMap);
            //System.out.println("RAND IS : " + rand);
            

                
                ArrayList <ArrayList> here  = this.generateStates(s1, sList, sMap, rulesetlist, impossibleList, true);
                
                
                for (int j = 0; j < here.size(); j++) {
                    
                    StateList sL = (StateList) here.get(j).get(0);
                    Token action = (Token) here.get(j).get(1);
                    
                    
                    System.out.println("STATELIST FROM : " + s1 + " with action " + action + " score : " + sL.getScore());
                    
                    if (sL.getScore() > whichone_score) {
                        
                        whichone_score = sL.getScore();
                        which_action = action;
                        rand = j;
                    }
                }
                
                
                System.out.println("CHOSEN ACTION : " + rand + " >> " + which_action);


//////                    System.out.print("    GENERATING STATE FOR " + numbers.getSensor((int) rand) + "...");

                    

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
                        
                                       // SELECTS A RANDOM ACTION
                            //rand = Math.random();
                            
                            if (numbers.size() > 1)
                                rand = Math.round(rand * numbers.size());
            
                            else 
                                rand = 0;
                            
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
                        
                        
                        //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                        //System.out.println("UPDATING STATELIST... ");
                        //stateList.update(rulesetlist.getRuleSet(chosen), impossibleList);

                    
                    }
                    
                    
                    
                    t = numbers.getSensor((int) rand +1).getToken(numbers.getSensor((int) rand +1).size()-1);
                    
                    
                    a.add(res);
                    
                    a.add(t);
                    //System.out.println("GENERATED STATE IS : " + res);
                

                //System.out.println("GENERATED " + numbers.size() + " StateLists.");


    //            for (int i = 0; i < stateLists.size(); i++) {
    //
    //                stateLists.get(i).printList("ACTION = " + numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1));
    //            }


         
        return a;
     }    
        
    
   
}
