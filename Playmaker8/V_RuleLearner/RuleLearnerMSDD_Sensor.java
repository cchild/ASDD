

package V_RuleLearner;


import V_Sensors.*;

import Logging.*;
import static Logging.LogFiles.getInstance;
import java.util.ArrayList;



/**
 *
 * @author virgile
 */
public class RuleLearnerMSDD_Sensor {
    
    public TokenMap tokenMap;
    public SensorList sensorMap;
    
    static boolean silent_mode = false;
   
    
    public RuleLearnerMSDD_Sensor(TokenMap t, SensorList s) {
        this.tokenMap = t;
        this.sensorMap = s;
    }
    
    
public static ArrayList <RuleList> learnRulesMSDD(TokenMap t, SensorMap sMap, RuleMap rMap, SensorList sList, RuleSetList rulesetlist, boolean silent , boolean read, int maxnodes)
    {
        
        
        RuleList closedList = new RuleList();
        
        
        if (silent) 
            silent_mode = true;
        
        
        if (silent_mode)
            System.out.println("\nMSDD RULELEARNER INITIALIZATION...");
        

         

         
         

         
         
         
         // INIT MAXNODES & PRUNING 
         //int maxnodes = 700; // 1.5K > 11mins
         int totalnodes = 1;
         
         boolean pruning = true;
         boolean freeloaders_filtering = true;
         
         boolean consolidate_rulesets = true;
         

         
         boolean export_closedList = !read;
         boolean read_closedList = !export_closedList;
         boolean export_RSList = export_closedList;
         boolean read_RSList = read_closedList;
               
         
         
         

         
       
         
         
         

         // INIT CLOSELIST & RSList
////         RuleList closedList = new RuleList();
         //RuleSetList rulesetlist = new RuleSetList(closedList, sList);
         
         
             // INIT OPENLIST
             RuleList openList = new RuleList();


             // INIT ROOTRULE
             String str_pre = "******";
             String str_post = "******";
             Sensor pre = new Sensor(str_pre,t);
             Sensor post = new Sensor(str_post,t);
             Rule rootRule = new Rule(pre,post, sList);
             rootRule.occurrencies = sList.size();
             //System.out.println(rootRule.prec_indexes);
             openList.addRule(rootRule);


              
             //impossibleList.printList();
             
             
             

             //rMap.printList("RMAP");
             
             
             
             
             
             
////             HashMap<Integer, Integer> map = new HashMap<>();
////             
////             
////             map.put(1, sMap.getIndexes(sMap.size()-1).get(0));
////             
////             System.out.println(map.containsValue(3592));
             
             
             // [W, E, E, A, E, W][W, E, A, W, E, N]
             
////             Sensor test1 = new Sensor("WEEAEW",t);
////             Sensor test2 = new Sensor("WEAWEN",t);
////             Rule testRule = new Rule(test1,test2, sList);
////             
////             
////             System.out.println(sList.indexesOfRule(testRule));
             
             //System.out.println(sList.indexesOfRule(rootRule).size());
             //System.out.println(sList.indexesOfRule2(rootRule).size());
            
             //System.out.println(sMap.getMatchingOccurencies(pre));
             
             if (silent_mode)
                System.out.println("\nBUILDING CLOSEDLIST...");
             
             
             if (!silent_mode)
                System.out.println("\nMAXNODES SET TO " + maxnodes);

             // STOPS IN 2 CASES : MAXNODES REACHED OR OPENLIST EMPTY
             while ((totalnodes < maxnodes) && (!openList.rulelist.isEmpty())) {

                 openList = openList.sort();

                 // THE CHOSEN RULE IS openList.getRule(0));
                 // CREATING THE CHILDREN, AND ADDING THEM TO OPENLIST

                 //System.out.println("HERE " + openList.getRule(0));

                 RuleList children = openList.getRule(0).getChildren();

                 int number_added = 0;

                 //PRUNING
                 if (pruning) 
                    number_added = openList.addWithCheck(children,sList, sMap, rMap);

                 if(!pruning)
                     number_added = openList.add(children);

                 // ADDING THE SOURCE RULE TO CLOSEDLIST
                 closedList.addRule(openList.getRule(0));

                 // number_added children were added
                 totalnodes = totalnodes + number_added; 

                 // REMOVING THE SOURCE RULE FROM OPENLIST
                 openList.remove(0); 


                 if ((totalnodes % 50) == 0) {
                     if (!silent_mode)
                        System.out.println("NODES : " + totalnodes + " ...");
                 }

             } // END WHILE


             // IF MAXNODES HAS BEEN REACHED, ADDING REMAINING RULES FROM OPENLIST
             if (totalnodes >= maxnodes) {
                 if (!silent_mode)
                     System.out.println("MAXNODES REACHED ( " + maxnodes + " )");
                 
                 openList = openList.sort();

                 while (closedList.size() < maxnodes)  {

                     closedList.addRule(openList.getRule(0));
                     openList.rulelist.remove(0);

                 } // END WHILE
                 
                 if (!silent_mode)
                    System.out.println("CLOSELIST HAS NOW " + closedList.size() + " ENTRIES.");

             }




             //openList.sort().printList("OPEN LIST");
             //closedList.printListByWithProb("CLOSED LIST",sList,1);

             //System.out.println("MSDD Rules (" + totalnodes + " entries) learned. ");


             //System.out.println("\nHELLO...");
             //closedList.printList();
             
             
             if (silent_mode)
                System.out.println("FILTERING CLOSELIST...");
             
             
             
             if (!silent_mode)
                System.out.println("\nSTARTING FILTERING...");
             if (!silent_mode)
                System.out.println("FILTERING ROOT SUCCESSOR RULES...");
             closedList = closedList.removeWildcardSuccessors();



             int  taille = closedList.size();

             if (!silent_mode)
                System.out.println("FILTERED " + (maxnodes - taille) + " Root Successor Rules.");

             
             
             if (freeloaders_filtering) {
             
                 if (!silent_mode)
                    System.out.println("\nFILTERING FREELOADERS...");
                 ArrayList arr = closedList.findFreeloaders(sList,sMap);
             
                 closedList.removeFreeloaders(arr);
             }

             int taille2 = closedList.size();

             //closedList.printListByWithProb("CLOSED LIST",sList,1,logfiles);


             if (!silent_mode)
                System.out.println("FILTERED " + (taille - taille2) + " Freeloaders.");

             if (!silent_mode)
                System.out.println("\nFILTERING DONE " + (maxnodes - closedList.size()) + " in total.");
             if (!silent_mode)
                System.out.println("CLOSELIST HAS NOW " + closedList.size() + " ENTRIES.");
             
             //closedList.printList();
             
             
             
             
             
             
             
             
             
             
             
             if (silent_mode)
                System.out.println("\nBUILDING RULESETLIST...");
             
             
             
             if (!silent_mode)
                System.out.print("\nBUILDING RULESETLIST FROM CLOSEDLIST...");
             
             rulesetlist = new RuleSetList(closedList, sList);
             rulesetlist.buildFromClosedList();
             
             if (!silent_mode)
                System.out.println(" OK");
             
             if (!silent_mode)
                System.out.println(rulesetlist.size() + " RULESETS HAVE BEEN GENERATED.");
             //rulesetlist.getRuleSet(1).consolidate(sList, closedList);

             
             //rulesetlist.printsoft();
             
             if (consolidate_rulesets) {

                 if (!silent_mode)
                    System.out.println("\nCONSOLIDATING RULESETS...");
                 int counter = rulesetlist.consolidate(sList, closedList, rMap, silent_mode);
                 //System.out.println(" OK");
                 if (!silent_mode) {
                    System.out.println(counter + " RULES SUCCESFULLY ADDED");
                    System.out.println("CLOSELIST HAS NOW " + closedList.size() + " ENTRIES.");
                 }
             }


         
         
             //rulesetlist.printall();
             //// 
             
             
             
             
             RuleList closedList2 = closedList.copy();
             
             
             
             
             
             
             
             
             
             
             
             
             
             
          
         if (silent_mode)
            System.out.println("SETTING PRECEDENCES...");
         
         
         
         
         
         if (!silent_mode)
            System.out.print("\nINITIATING INDEXES...");
         rulesetlist.initIndexes(sList, sMap);
         if (!silent_mode)
            System.out.println(" OK");
         
         //rulesetlist.print();


        
        //System.out.println("\nBUILDING REQUIRED SENSORLISTS...");
        
        
        if (!silent_mode)
            System.out.print("\nINDENTIFYING CONFLICTS & ESTABLISHING PRECEDENCES BETWEEN RULESETS...");
        ArrayList <ArrayList> a = rulesetlist.getConflicts(sList, sMap, rMap, silent_mode);
        if (!silent_mode) {
            System.out.println(" OK");
        
            System.out.println(a.get(0).get(0) + " CONFLICTS IDENTIFIED");
            System.out.println(a.get(0).get(1) + " PRECEDENCES SET");
        }
        
 
        //rulesetlist.printsoft();
      
        
        //System.out.println("CLOSEDLIST2 SIZE : " + closedList2.size());
        
        ArrayList res = new ArrayList ();
        
        
       res.add(closedList2);
       
       res.add(rulesetlist);

        
       return res;
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
////////        
////////                 String str3 = "WEEEEN";
////////         Sensor s1= new Sensor(str3,t);
////////         
////////        
////////        System.out.println("\nSOURCE STATE : " + s1.toString());
////////
////////        
////////        System.out.print("\nBUILDING POSSIBLE LIST...");
////////        StateList possibleList = sList.getPossibleList(s1, sMap);
////////        System.out.println(" OK");
////////        
////////        //possibleList.printList();
////////        
////////        
////////        
////////        ArrayList all = s1.getAllMatchingRuleSets(rulesetlist);
////////        
////////        //System.out.println("SIZE OF ALL: " + all.size());
////////        //System.out.println("ALL: " + all);
////////        System.out.println("\nBUIDLING STATELIST ");
////////        
////////
////////        ArrayList <StateList> stateLists = new ArrayList();
////////        
////////        SensorList numbers = new SensorList ();
////////        
////////        if (s1.getToken(s1.size()-1).isWildcard()) {
////////            
////////            numbers.addSensorList(s1.expand(s1.size()-1));
////////        }
////////        
////////        else { numbers.addSensor(s1); }
////////        
////////        //System.out.println("TEST ");
////////        
////////        numbers.printList("STUDY LIST");
////////        
////////        
////////        for (int i = 0; i < numbers.size(); i++) {
////////            
////////            System.out.print("GENERATING STATES FOR " + numbers.getSensor(i+1) + "...");
////////            
////////            ArrayList <Integer> aList = new ArrayList();
////////            ArrayList <Integer> chosenRuleSets = new ArrayList();
////////            StateList stateList = new StateList();
////////        
////////            
////////            while ( (aList.size()+1)!= pre.size()) {
////////
////////                //System.out.println("\nCHOOSING NEXT RULESET...");
////////                int chosen = numbers.getSensor(i+1).chooseNextRuleSet(rulesetlist, aList);
////////                //System.out.println("CHOSEN RULESET : " + chosen);
////////                //rulesetlist.getRuleSet(chosen).print();
////////
////////                aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());
////////
////////                chosenRuleSets.add(chosen);
////////                //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);
////////
////////                //System.out.println("UPDATING STATELIST... ");
////////                stateList.update(rulesetlist.getRuleSet(chosen), impossibleList);
////////
////////            }
////////
////////            if (hard_clean_statelist) {
////////                //System.out.println("\nHARD CLEANING STATELIST ");
////////                stateList.clean_hard(impossibleList, possibleList);
////////            }
////////            else {
////////                //System.out.println("\nCLEANING STATELIST ");
////////                stateList.clean(impossibleList);
////////
////////            }
////////            
////////            System.out.println(" OK");
////////            stateLists.add(stateList);
////////            
////////        }
////////        
////////        System.out.println("GENERATED " + numbers.size() + " StateLists.");
////////        
////////        
////////        for (int i = 0; i < stateLists.size(); i++) {
////////            
////////            stateLists.get(i).printList("ACTION = " + numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1));
////////        }
////////        
////////        
////////        possibleList.printList("REAL ONE");
        
        //rulesetlist.printall();
        //logfiles.closeall();

         
         
//////////////////////////////////////////////////////////////////////////////// 
//////////////////////////////////////////////////////////////////////////////// 
/////////////////////////////READING PART/////////////////////////////////////// 
//////////////////////////////////////////////////////////////////////////////// 
//////////////////////////////////////////////////////////////////////////////// 
         
         
         //else {
             

             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
//////////             
//////////        // INIT SOURCE STATE
//////////////        String str_pre = "******";
//////////////        Sensor pre = new Sensor(str_pre,t);
//////////        String str3 = "WEEEE*";
//////////        Sensor s1= new Sensor(str3,t);     
//////////     
//////////        
//////////        System.out.println("\nSOURCE STATE : " + s1.toString());
//////////        
//////////        System.out.println("\nBUILDING POSSIBLE LIST...");
//////////        StateList possibleList = sList.getPossibleList(s1, sMap);
//////////        
//////////        //possibleList.printList("REAL ONE");
//////////        
//////////        
//////////        
//////////        ArrayList all = s1.getAllMatchingRuleSets(rulesetlist);
//////////        
//////////        //System.out.println("SIZE OF ALL: " + all.size());
//////////        //System.out.println("ALL: " + all);
//////////        System.out.println("\nBUIDLING STATELIST ");
//////////        
//////////
//////////        ArrayList <StateList> stateLists = new ArrayList();
//////////        
//////////        SensorList numbers = new SensorList ();
//////////        
//////////        if (s1.getToken(s1.size()-1).isWildcard()) {
//////////            
//////////            numbers.addSensorList(s1.expand(s1.size()-1));
//////////        }
//////////        
//////////        else { numbers.addSensor(s1); }
//////////        
//////////        //System.out.println("TEST ");
//////////        
//////////        numbers.printList("STUDY LIST");
//////////        System.out.println();
//////////        
//////////        
//////////        for (int i = 0; i < numbers.size(); i++) {
//////////            
//////////            System.out.print("GENERATING STATES FOR " + numbers.getSensor(i+1) + "...");
//////////            
//////////            StateList stateList = new StateList();
//////////             
//////////            stateList.generateStates (numbers, s1, rulesetlist, i, impossibleList, possibleList, hard_clean_statelist);
//////////            
//////////            
//////////            System.out.println(" OK");
//////////            stateLists.add(stateList);
//////////            
//////////        }
//////////        
//////////        System.out.println("GENERATED " + numbers.size() + " StateLists.");
//////////        
//////////        
//////////        for (int i = 0; i < stateLists.size(); i++) {
//////////            
//////////            stateLists.get(i).printList("ACTION = " + numbers.getSensor(i+1).getToken(numbers.getSensor(i+1).size()-1));
//////////            System.out.println("Score of : " + stateLists.get(i).getScore());
//////////        }
//////////        
//////////        
//////////        possibleList.printList("REAL ONE");
//////////        
//////////        
//////////        
////////////////        String str_1 = "WEEE**";
////////////////        String str_2 = "E*****";
////////////////        Sensor prec = new Sensor(str_1,t);
////////////////        Sensor postc = new Sensor(str_2,t);
////////////////        
////////////////        Rule rule = new Rule (prec, postc, sList);
//////////        //rulesetlist.printall();
//////////        
//////////        //[W, E, E, E, *, *]
//////////        //System.out.println(sList.indexesOfRule(rule).size());
//////////        //RuleList ss = rule.getChildren();
//////////        
//////////        //ss.printList();
        //logfiles.closeall();
         //}
         
//         RuleSet ruleset = new RuleSet(closedList);
//
//         ruleset.add(closedList.getRule(7));
//         
//         //ruleset.printRules();
//         
//         RuleSet ruleset2 = new RuleSet(closedList);
//
//         ruleset2.add(closedList.getRule(5));
//         
//         //ruleset2.printRules();     
//         
//         RuleSet ruleset3 = new RuleSet(closedList);
//
//         ruleset3.add(closedList.getRule(9));
         
         //ruleset3.print(); 
         
         

////         
////         System.out.println("\nINITIATING INDEXES...");
////         rulesetlist.initIndexes(sList);
////         
////         //rulesetlist.print();
//////         System.out.println(rulesetlist.getRuleSet(682).getRule(0).isMoreGeneralizedRule(rulesetlist.getRuleSet(681).getRule(0)));
//////         System.out.println("G STAT :" + sList.Gstatistic(rulesetlist.getRuleSet(681).getRule(0), rulesetlist.getRuleSet(682).getRule(0)));
//////         rulesetlist.getRuleSet(681).printRules();
//////         rulesetlist.getRuleSet(682).printRules();
//////         
//////         int bah = rulesetlist.getRuleSet(340).consolidate(sList, closedList);
//////         
//////         rulesetlist.getRuleSet(340).printRules();
////         
//////         Sensor target = closedList.getRuleByID(rulesetlist.getRuleSet(6).references.get(0)).postcondition;
//////         System.out.println(target);
//////         SensorList sensei = closedList.getRuleByID(rulesetlist.getRuleSet(6).references.get(0)).postcondition.expandNonWildcard(3);
//////         sensei.printList();
//////         
//////         RuleList rList = closedList.getRuleByID(rulesetlist.getRuleSet(6).references.get(0)).getSameRuleSetRules(sList,closedList);
//////         
//////           rList.printList();
//////  [*, *, *, *, E, *][*, A, *, *, E, *]
////         String str3 = "WEEAES";
////         Sensor s1= new Sensor(str3,t);
////         String str4 = "****E*";
////         Sensor s4= new Sensor(str4,t);
////         String str5 = "*E**E*";
////         Sensor s5= new Sensor(str5,t); 
////         
////        
////        System.out.println("\nSOURCE STATE : " + s1.toString());
////         //rulesetlist.printall();
////
////        
////        
//////        int num = 239;
//////        
//////                for (int i = 1; i < rulesetlist.size(); i++) {
//////            
//////            if (rulesetlist.getRuleSet(num).isConflicting(rulesetlist.getRuleSet(i+1)))
//////                System.out.println("CONFLICT BETWEEN " + num + " AND " + (i+1) + rulesetlist.getRuleSet(num).getRule(0) + " // " + rulesetlist.getRuleSet(i+1).getRule(0));
//////        }
////        
////        
////        
////        System.out.println("\nINDENTIFYING CONFLICTS & ESTABLISHING PRECEDENCES BETWEEN RULESETS...");
////        ArrayList <ArrayList> a = rulesetlist.getConflicts(sList);
////        
////        System.out.println(a.get(0).get(0) + " CONFLICTS IDENTIFIED");
////        System.out.println(a.get(0).get(1) + " PRECEDENCES SET");
////        
////        //System.out.println(a.get(num));
////        
////        
////        
////        //rulesetlist.getRuleSet(3).printRules();
////        //rulesetlist.getRuleSet(239).printRules();
//////        
////        //RuleSet test = new RuleSet(rulesetlist.getRuleSet(3),rulesetlist.getRuleSet(239), sList);
////        
////        //test.printRules();
////        
////       
////        
////        //System.out.println("239 :" + test.getError(rulesetlist.getRuleSet(239)));
////        //System.out.println("3 :" + test.getError(rulesetlist.getRuleSet(3)));
////        //System.out.println(rulesetlist.getRuleSet(115).isConflicting(rulesetlist.getRuleSet(239)));
////        
////    
////        
////        
////        
////        System.out.println("\nBUILDING IMPOSSIBLE LIST...");
////        SensorList impossibleList = sList.getImpossibleList();
////          
////        //
////        
////        
////        ArrayList all = s1.getAllMatchingRuleSets(rulesetlist);
////        
////        System.out.println("SIZE OF ALL: " + all.size());
////        System.out.println("ALL: " + all);
//////        System.out.println("SIZE OF 25 : " + rulesetlist.getRuleSet(25).precedences.size());
//////        System.out.println("SIZE OF 45 : " + rulesetlist.getRuleSet(45).precedences.size());
//////        System.out.println("SIZE OF 102 : " + rulesetlist.getRuleSet(102).precedences.size());
////        
////        ArrayList <Integer> aList = new ArrayList();
////        ArrayList <Integer> chosenRuleSets = new ArrayList();
////        StateList stateList = new StateList();
////        
////        while ( (aList.size()+1)!= pre.size()) {
////            
////            int chosen = s1.chooseNextRuleSet(rulesetlist, aList);
////            
////            aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());
////            
////            chosenRuleSets.add(chosen);
////            stateList.update(rulesetlist.getRuleSet(chosen));
////            
////            System.out.println("\nCHOSEN RULESET : " + chosen);
////            System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);
////            rulesetlist.getRuleSet(chosen).printRules();
////        }
////        
////        stateList.clean_hard(impossibleList);
////        
////        stateList.printList();
         
         
         
         
        //stateList.setProb(6, stateList.getProb(6)*2);
//        stateList.init(rulesetlist.getRuleSet(102));
//        
//          stateList.update(rulesetlist.getRuleSet(102));
//          stateList.update(rulesetlist.getRuleSet(61));
//         
//        stateList.update(rulesetlist.getRuleSet(91));
        
        //stateList.printList();
        
        
//        System.out.println(rulesetlist.getRuleSet(91).precedences);
//        
//        rulesetlist.getRuleSet(91).printRules();
//        System.out.println(rulesetlist.getRuleSet(91).precedences);
//        rulesetlist.getRuleSet(55).printRules();
//        //rulesetlist.getRuleSet(39).printRules();
//        //rulesetlist.getRuleSet(55).printRules();
//        
//        
//        RuleSet test = new RuleSet(rulesetlist.getRuleSet(91),rulesetlist.getRuleSet(55), sList);
//        
//        test.printRules();
//        
//        
//        System.out.println("91 Error : " + test.getError(rulesetlist.getRuleSet(91)));
//        System.out.println("55 Error : " + test.getError(rulesetlist.getRuleSet(55)));
//        System.out.println("91 precedes ? " + test.precedingOver(rulesetlist.getRuleSet(91), rulesetlist.getRuleSet(55)));
        //impossibleList.printList();

        
        //System.out.println("PREC : " + rulesetlist.getRuleSet(91).precedences);
//        System.out.println("27 : " + impossibleList.findSensor(stateList.getSensor(26)));
//        System.out.println("20 : " + impossibleList.findSensor(stateList.getSensor(19)));
//        System.out.println("21 : " + impossibleList.findSensor(stateList.getSensor(20)));
        
        
        
//          System.out.println(rulesetlist.getRuleSet(102).precedences);
//        
//          rulesetlist.getRuleSet(102).printRules();
//          rulesetlist.getRuleSet(96).printRules();
//
//
//          RuleSet RS = new RuleSet(rulesetlist.getRuleSet(102),rulesetlist.getRuleSet(96), sList);
//        
//          RS.printRules();
        
//        System.out.println("CHOSEN FIRST RULESET : " + chosen);
//        rulesetlist.getRuleSet(chosen).printRules();
//        
//         rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots();
//        
//        
//        int chosen2 = s1.chooseNextRuleSet(rulesetlist,aList);
//        
//        System.out.println("CHOSEN SECOND RULESET : " + chosen2);
//        rulesetlist.getRuleSet(chosen2).printRules();
//        
//        
//        aList.addAll(rulesetlist.getRuleSet(chosen2).detectNonWildcardedSpots());
//        
//        
//        int chosen3 = s1.chooseNextRuleSet(rulesetlist,aList);
//        
//        System.out.println("CHOSEN THIRD RULESET : " + chosen3);
//        rulesetlist.getRuleSet(chosen3).printRules();
//        
//        
//        aList.addAll(rulesetlist.getRuleSet(chosen3).detectNonWildcardedSpots());
//        
//        
//        int chosen4 = s1.chooseNextRuleSet(rulesetlist,aList);
//        
//        System.out.println("CHOSEN FOURTH RULESET : " + chosen4);
//        rulesetlist.getRuleSet(chosen4).printRules();
//        
//        
//        aList.addAll(rulesetlist.getRuleSet(chosen4).detectNonWildcardedSpots());        
        
//        System.out.println("\nGENERATING NEXT STATE...");
//        // FIRST ROUND 
//        int firstround = s1.getBestMatchingRuleSets(rulesetlist);
//         
//        ArrayList <Integer> aList = rulesetlist.getRuleSet(firstround).detectNonWildcardedSpots();
//                 
//        System.out.println("\nCHOSEN 1st RULESET : " + firstround + aList); 
//        rulesetlist.getRuleSet(firstround).printRules();
//    
//        // SECOND ROUND
//        int secondround = s1.getBestMatchingRuleSets(rulesetlist, aList);       
//        
//        aList.addAll(rulesetlist.getRuleSet(secondround).detectNonWildcardedSpots());
//        
//        System.out.println("\nCHOSEN 2nd RULESET : " + secondround + aList);
//        rulesetlist.getRuleSet(secondround).printRules();
//        
//        
//        
//        SensorList outputStateList = rulesetlist.getRuleSet(secondround).outputsFromRuleSets(rulesetlist.getRuleSet(firstround));
//        ArrayList outputStatesProb = rulesetlist.getRuleSet(secondround).outputsProbFromRuleSets(rulesetlist.getRuleSet(firstround));
//        
//
//        // THIRD ROUND
//        int thirdround = s1.getBestMatchingRuleSets(rulesetlist, aList);
//        
//        aList.addAll(rulesetlist.getRuleSet(thirdround).detectNonWildcardedSpots());
//        System.out.println("\nCHOSEN 3rd RULESET : " + thirdround + aList);
//        rulesetlist.getRuleSet(thirdround).printRules();
//        
//        outputStateList.addSensorList(rulesetlist.getRuleSet(thirdround).outputsFromRuleSets(outputStateList));
//        
//        
//        // FOURTH ROUND
//        int fourthround = s1.getBestMatchingRuleSets(rulesetlist, aList);
//        
//        aList.addAll(rulesetlist.getRuleSet(fourthround).detectNonWildcardedSpots());
//        System.out.println("CHOSEN 4th RULESET : " + fourthround + aList);
//        rulesetlist.getRuleSet(fourthround).printRules();
//        
//        outputStateList.addSensorList(rulesetlist.getRuleSet(fourthround).outputsFromRuleSets(outputStateList));
//        
//        
////        
////        int fifthround = s1.getBestMatchingRuleSets(rulesetlist, aList);
////        
////         aList.addAll(rulesetlist.getRuleSet(fifthround).detectNonWildcardedSpots());
////        System.out.println("CHOSEN 5th RULESET : " + fifthround + aList);
////        rulesetlist.getRuleSet(fifthround).printRules();
////       
////        outputStateList.addSensorList(rulesetlist.getRuleSet(fifthround).outputsFromRuleSets(outputStateList));
//        
//        int tmp = outputStateList.size();
//        for (int i = 0; i < tmp; i++) {
//            
//            outputStateList.addSensorList(outputStateList.getSensor(i+1).expand(outputStateList.getSensor(i+1).size()-1));
//        }
//        
//        System.out.println("\nCLEANING OUTPUTSTATELIST...");
//        
//        if (outputstate_hard_cleaning) {
//            outputStateList = outputStateList.clean_hard(sList,s1);
//        }
//        else { outputStateList = outputStateList.clean_hard(sList); }
//        
//        
//        outputStateList.printListWithOcc(sList);
        
        //System.out.println(outputStatesProb);
        
        

        //System.out.println(a.get(0));
        
//        THIS ROUND IS USELESS SINCE ACTION IS ALWAYS * IN POSTCONDITION
//        int finalround = s1.getBestMatchingRuleSets(rulesetlist, aList);
//        
//        System.out.println("CHOSEN 6th RULESET : " + finalround + aList);
//        rulesetlist.getRuleSet(finalround).printRules();        
        
        
        //rulesetlist.getRuleSet(682).printRules();
        //rulesetlist.getRuleSet(728).printRules();
        
        
        //System.out.println(rulesetlist.getRuleSet(681).getPrecursor().sensorMatch_exact(rulesetlist.getRuleSet(680).getPrecursor()));
        //System.out.println(rulesetlist.getRuleSet(216).getPrecursor().numberOfNonWildcards() );
        //rList.printList();
        //rulesetlist.getRuleSet(210).printRules();
        //rulesetlist.getRuleSet(216).printRules();
         
         //logfiles.closeall();
//         Sensor outputStateList = new Sensor("E*****",t);
//         Rule rule2 = new Rule (outputStateList, closedList.getRule(500).postcondition);
//         System.out.println(closedList.getRule(500));
//         System.out.println(rule2);
//         System.out.println(closedList.getRule(500).isMoreGeneralizedRule(rule2));
         
         
         
         
//         int a = 1602;
//         String str = "EEW*E*";
//         Rule prospect = new Rule(new Sensor(str,t), closedList.getRule(1600).postcondition);
//         System.out.println("Target Rule : " + closedList.getRule(1600));
//         System.out.println("Perspect Rule : " + prospect);
//         System.out.println("Generalized ? : " + closedList.getRule(1600).isMoreGeneralizedRule(prospect));
//        
      
         
    }



    




public void main (String[] args) {


    
    //learnRulesMSDD();

     
     
    
}  


}

  


