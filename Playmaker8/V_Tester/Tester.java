/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Tester;

import Logging.LogFiles;
import static Logging.LogFiles.getInstance;
import V_ReinforcementLearner.*;
import V_RuleLearner.*;
import V_Sensors.*;
import V_StateGenerator.*;
import java.util.ArrayList;


/**
 *
 * @author virgile
 */
public class Tester {
    
    
    // ENABLE TO PRINT EVERYTHING
    static boolean debug_mode = false;
    
    
    // ENABLE TO ONLY SEE IMPORTANT STEPS
    static boolean silent_mode = false;
    
    
    // ENABLE TO USE THE LAST SAVED DATABASE
    // DISABLE TO CREATE AND SAVE A DATABASE
    static boolean read_database = true;
    
    
    static boolean MSDD = true;
    
    
    
    // SET TO 1 TO USE Rules
    // SET TO 2 TO USE Maps
    static int method = 2;
    
    // SET TO 1 FOR 100% RANDOM
    // SET TO 2 FOR 23% RANDOM (PICKING)
    static int gen_method = 1;
    
    

    
    // MSDD MAXNODES PARAMETER
    // ONLY USED IF read_database IS SET TO FALSE
    static int maxnodes = 25000;
    
    // REINFOCEMENT LEARNING ?
    static final boolean RL = true;
  
    // LIMIT FOR REINFORCEMENT LEARNING
    static int RL_limit = 1000000;   
    
    static final boolean export_table = RL;
    
    static boolean read_table = !export_table;
 
    
    
    
    
    
    
    
    
    
    public static void main (String[] args) {


    
        
        System.out.println("INITIALIZING...");
        
     
        ////////////////////
        // INITIALIZATION //
        ////////////////////
        
        // TOKENMAP
            TokenMap tokenMap = new TokenMap();
                if (!silent_mode)
                    System.out.print("\nBuilding TokenMap ...");
                tokenMap.fromFile();
                if (!silent_mode)
                    System.out.println(" OK");
                System.out.println(tokenMap.TokenTypes);
            
        // SENSORLIST (DATABASE)    
            SensorList sList = new SensorList();
                if (!silent_mode)
                    System.out.print("Building SensorList ...");
                sList.fromFile(tokenMap);
                if (!silent_mode)
                    System.out.println(" OK");
            
            
        // SENSORMAP  
            SensorMap sMap = new SensorMap(tokenMap);
                if (!silent_mode)
                    System.out.print("Building SensorMap ...");
                sMap.fromFile();
                if (!silent_mode)
                    System.out.println(" OK");
            
            
        // RULEMAP
            RuleMap rMap = new RuleMap(tokenMap);
                if (!silent_mode)
                    System.out.print("Building RuleMap ...");
                rMap.fromFile(sList);
                if (!silent_mode)
                    System.out.println(" OK");
                
         // INIT IMPOSSIBLELIST
                if (!silent_mode)
                    System.out.print("Building Impossible List...");
                SensorList impossibleList = sList.getImpossibleList(sMap);
         
                if (!silent_mode)
                    System.out.println(" OK");
         
       // STATEMAP
                if (!silent_mode)                 
                    System.out.print("\nBuilding StateMap...");
                StateMap stMap = new StateMap();
                stMap.fromFile(tokenMap);
                if (!silent_mode)
                    System.out.println(" OK");
                
                stMap.printMap();
                
        // CLOSEDLIST        
            RuleList closedList = new RuleList();
        
            
        // RULESETLIST    
            RuleSetList rsList = new RuleSetList(closedList, sList);
      
        
        // OTHERS    
            ArrayList rules = new ArrayList ();
            rules.add(closedList);        
            rules.add(rsList);
            StateTable sTable = new StateTable ();
            
        

        // LOGFILES
        LogFiles logfiles = getInstance();
        
        // IF CREATING A NEW DATABASE, ERASES THE OLD ONE
        if (!read_database) {

             logfiles = getInstance(5);
             logfiles = getInstance(3);
             
             
         }        
        
        if (export_table)
            logfiles = getInstance(6);
        
        
        ///////////////////////////
        // MSDD - LEARNING RULES //
        ///////////////////////////
        if ( (!read_database) && (MSDD) ) {
            System.out.println("\n\nLEARNING MSDD RULES ...");

            rules = RuleLearnerMSDD_Sensor.learnRulesMSDD(tokenMap, sMap, rMap, sList, rsList, silent_mode, read_database, maxnodes);


            closedList = (RuleList) rules.get(0);

            rsList = (RuleSetList) rules.get(1);
        
            
            System.out.println("\n");
            
            System.out.println("\n" + closedList.size() + " Rules successfully learned. " + rsList.size() + " RuleSets.");
        
        }
        
        
        
        
        //////////////////////////
        // MSDD - LOADING RULES //
        //////////////////////////
        if ( (read_database) && (MSDD)) {
                System.out.println("\n\nLOADING MSDD RULES ...");
             
                closedList.fromFile(sList, tokenMap);
             
             
                rsList.fromFile();

                
                System.out.println("\n" + closedList.size() + " Rules successfully learned. " + rsList.size() + " RuleSets.");
        
        }
        
        
        
        ///////////
        // DEBUG //
        ///////////
        if (debug_mode)
            closedList.printList("CLOSEDLIST");

        if (debug_mode)
            rsList.printsoft();
    
    
        
        
        /////////////////////
        // EXPORT DATABASE //
        /////////////////////        
        if (!read_database) {
            
            System.out.print("\nExporting ClosedList...");
            
            closedList.export();
                       
            System.out.println(" OK");
            
            
            
            System.out.print("\nExporting RuleSetList...");
            
            rsList.export();
                        
            System.out.println(" OK");
            
        }
                     
        
        
        

        
        
        
        
        
        /////////////////////
        // STATE GENERATOR //
        /////////////////////   
        System.out.println("\n\nRUNNING STATE GENERATOR...");
        
        
        
        // DEFAULT CASE
        // STATEGENERATOR IS CHANGED BELOW ACCORDING TO THE SELECTED METHOD
        StateGenerator stateGenerator = new MSDD_StateGenerator_Maps ();
        
        if (method == 1) {
            stateGenerator = new MSDD_StateGenerator_Rules ();
            System.out.println("\nRules State Generator selected.");
        }
        
        if (method == 2) {
            stateGenerator = new MSDD_StateGenerator_Maps ();
            
            System.out.println("\nMaps State Generator selected.");
            
            
            
            //stMap.printMap();
        }
        
        
        
        if (gen_method == 1) {
   
            System.out.println("Random State Generation selected.");
        }
        
        if (gen_method == 2) {
            
            System.out.println("Q-Learning State Generation selected.");
            
        }
        
        
        // SOURCE STATE
        String str = "EEAEE*";
        Sensor currentState= new Sensor(str,tokenMap);
        

        

        //SensorList res = new SensorList ();
        //res.addSensor(currentState);
        
        
        
        
        int won = 0;
        
        //System.out.println("\nSOURCE STATE : " + currentState);

        int number = 0;
         
        if (RL) {
            
            //for (int y = 1; y < 16; y++) {
            
                System.out.println("\nREINFORCEMENT LEARNING...");
        
        
                ReinforcementLearner rLearner = new ReinforcementLearner ();
        
                //number = number + rLearner.createTable(RL_limit, stateGenerator, gen_method, currentState, sList, sMap, rsList, impossibleList, stMap);
                sTable = rLearner.createTable(RL_limit, stateGenerator, gen_method, currentState, sList, sMap, rsList, impossibleList, stMap);
        
            //}
            sTable.printTable("STATETABLE");
            
            if (export_table)
                sTable.export();
////            for (int y = 0; y < 20; y ++) {
////            
////                Token tok = stateGenerator.generateActionFromTable(currentState, sTable);
////                
////                
////                System.out.println(tok);
////            }
            
            

            
        }

        

        


        //stMap.printMap_soft("From File");

        SensorList res = new SensorList ();
        
        ArrayList actions = new ArrayList ();

        StateTable nop = new StateTable ();
       
        if (read_table) {
        
            System.out.println("\nImporting sTable...");
        
            
        
            nop = nop.fromFile(tokenMap);
        
            nop.printTable("IMPORTED");
   
        }
                
                
                
        int rewards = 0;
        
        if (!RL) {
            if(gen_method == 1) {

                System.out.println("\nGENERATING 50 SAMPLES...");
                
                for (int i = 0 ; i < 50; i++) {

                    ArrayList <Sensor> zou = stateGenerator.generateState_random(currentState, sList, sMap, rsList, impossibleList, stMap);

                    res.addSensor(zou.get(0));

                    actions.add(zou.get(1));
                    
                    
                    //System.out.println("State " + i + " : " + zou.get(0));
                    //System.out.println("Action : " + zou.get(1));
                }
            }
            if (gen_method == 2) {

                System.out.println("\nGENERATING 50 SAMPLES...");
                
                stMap.printMap("Hello");
                
                System.out.println();
                
                Sensor old_state = currentState.copy();
                
                for (int i = 0 ; i < 100000; i++) {

                     //ArrayList <Sensor> zou = stateGenerator.generateState_random(currentState, sList, sMap, rsList, impossibleList, stMap);

                    //ArrayList <Sensor> zou = stateGenerator.generateState_bestAction(currentState, rsList, impossibleList, nop, stMap);

                    //if (zou.size() > 0) {
                    
                        //System.out.println("Current State : " + currentState);
                        
                        
                        Sensor nextState = stateGenerator.generateState_bestAction(old_state, sList, sMap, rsList, impossibleList, nop, stMap);

                        res.addSensor(nextState);

                        //int index = nop.findSensor(currentState);
                        
                        

                        //actions.add(stateGenerator.generateRandomAction(currentState, nop.getAction(index)));
                        if (nextState.isRewarded()) {
                            System.out.println("Rewarded State : " + nextState + " from " + old_state + " step " + i);
                            rewards++;
                        }
                        
                        old_state = nextState.copy();
                    }
                    //System.out.println(" Action : " + zou.get(1));
                }
            }
            
            
            System.out.println(rewards + " Rewards");
//            for (int i = 0; i < actions.size(); i++) {
//                
//                System.out.println("Action : " + actions.get(i));
//            }
//            
//            for (int i = 0; i < res.size(); i++) {
//                
//                System.out.println("State : " + res.getSensor(i+1));
//            }
        
        
        
        

        
        
        
        
        
        
        
        
        System.out.println("Closing Files...");
        
        logfiles.closeall();
} 
}
