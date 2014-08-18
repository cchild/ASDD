/*
 * 
 * 
 * 
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
    

    
    // GENERAL SETTINGS
    static final boolean silent_mode = false;
    

    
    // MSDD SETTINGS
    static final boolean MSDD = true;
    static final boolean load_MSDD_rules = true;
    static final boolean save_MSDD_rules = !load_MSDD_rules;
    static final int maxnodes = 50000;
    
    
    
    // STATE GENERATOR SETTINGS
    static final boolean use_Maps = true;
    static final boolean use_Rules = !use_Maps;    

    
    
    // REINFORCEMENT LEARNING SETTINGS
    static final boolean Reinforcement_Learning = true;
    static final int Reinforcement_Learning_steps = 10000;  // MAPS CAN GO TO 2 Millions, Rules around 15K
    static final boolean use_decision_tables = true;
    static final boolean save_Reinforcement_Learning_results = Reinforcement_Learning; 
    static final boolean load_Reinforcement_Learning_results = !save_Reinforcement_Learning_results;
    static final boolean use_Value_Table = false;
    static final boolean use_Action_Value_Table = !use_Value_Table;
    static final boolean use_Dynamic_Programming = true;
    
    

    
    
    
    

    
    
    
    
    
    
    
    
    
    // MAIN
    public static void main (String[] args) {


    
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////        
////////////////////////////////////////////////////////////////////////////////
        
        
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
                
            
        // SENSORLIST (DATABASE)    
            SensorList sensorList = new SensorList();
                if (!silent_mode)
                    System.out.print("Building SensorList ...");
                sensorList.fromFile(tokenMap);
                if (!silent_mode)
                    System.out.println(" OK");
            
            
        // SENSORMAP  
            SensorMap sensorMap = new SensorMap(tokenMap);
                if (!silent_mode)
                    System.out.print("Building SensorMap ...");
                sensorMap.fromFile();
                if (!silent_mode)
                    System.out.println(" OK");
            
                
        // INIT IMPOSSIBLELIST
                if (!silent_mode)
                    System.out.print("Building Impossible List...");
                SensorList impossibleList = sensorList.getImpossibleList(sensorMap);
         
                if (!silent_mode)
                    System.out.println(" OK");
         
       // STATEMAP
                if (!silent_mode)                 
                    System.out.print("Building StateMap...");
                StateMap stateMap = new StateMap();
                stateMap.fromFile(tokenMap);
                if (!silent_mode)
                    System.out.println(" OK");

                    
        // RULEMAP
            RuleMap ruleMap = new RuleMap(tokenMap);
                if (!silent_mode)
                    System.out.print("Building RuleMap ...");
                ruleMap.fromFile(sensorList);
                if (!silent_mode)
                    System.out.println(" OK");                
             ruleMap.printList("");
        // CLOSEDLIST        
            RuleList closedList = new RuleList();
        
            
        // RULESETLIST    
            RuleSetList ruleSetList = new RuleSetList(closedList, sensorList);
      
        
        // OTHERS    
            ArrayList MSDD_rules = new ArrayList ();
            MSDD_rules.add(closedList);        
            MSDD_rules.add(ruleSetList);
            StateActionValueTable stateActionValueTable;
            StateValueTable stateValueTable;

            
        // LOGFILES
        LogFiles logfiles = getInstance();
        
        // IF CREATING A NEW MSDD DATABASE, ERASES THE OLD ONE
        if (save_MSDD_rules) {

             logfiles = getInstance(5);
             logfiles = getInstance(3);    
         }        
        
        // IF CREATING A NEW REINFORCEMENT LEARNING DATABASE, ERASES THE OLD ONE
        if (save_Reinforcement_Learning_results)
            logfiles = getInstance(6);
        
        
        
        
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////        
////////////////////////////////////////////////////////////////////////////////        
        
        
        
        
        ////////////////////////
        // MSDD - LEARN RULES //
        ////////////////////////
        if ( (!load_MSDD_rules) && (MSDD) ) {
            
            System.out.println("\n\nLEARNING MSDD RULES ...");


            MSDD_rules = RuleLearnerMSDD.learnRulesMSDD(tokenMap, sensorMap, ruleMap, sensorList, ruleSetList, silent_mode, maxnodes);


            closedList = (RuleList) MSDD_rules.get(0);

            ruleSetList = (RuleSetList) MSDD_rules.get(1);
        
            
            System.out.println("\n\n" + closedList.size() + " Rules successfully learned. " + ruleSetList.size() + " RuleSets were created.");
        
        }
        
        
        
        
        ///////////////////////
        // MSDD - LOAD RULES //
        ///////////////////////
        if ( (load_MSDD_rules) && (MSDD)) {
            
                System.out.println("\n\nLOADING MSDD RULES ...");
             
                closedList.fromFile(sensorList, tokenMap);
                         
                ruleSetList.fromFile();

                
                System.out.println("\n" + closedList.size() + " Rules successfully learned. " + ruleSetList.size() + " RuleSets were created.");
        
        }
        


        ///////////////////////
        // MSDD - SAVE RULES //
        ///////////////////////        
        if ( (save_MSDD_rules) && (MSDD)) {
            
            System.out.print("\nExporting ClosedList...");
            
            closedList.export();
                       
            System.out.println(" OK");
            
            
            
            System.out.print("\nExporting RuleSetList...");
            
            ruleSetList.export();
                        
            System.out.println(" OK");
            
        }
                     
        
        
        

        
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////        
////////////////////////////////////////////////////////////////////////////////
        
        
        
        
        /////////////////////
        // STATE GENERATOR //
        /////////////////////   
        System.out.println("\n\nRUNNING STATE GENERATOR...");
        
        
        
        // DEFAULT STATE GENERATOR : MAPS
        StateGenerator stateGenerator = new MSDD_StateGenerator_Maps ();
        
        // RULES STATE GENERATOR
        if (use_Rules) {
            
            stateGenerator = new MSDD_StateGenerator_Rules ();
            System.out.println("\nRules State Generator selected.");
        }
        
        // MAPS STATE GENERATOR
        if (use_Maps) {
            
            stateGenerator = new MSDD_StateGenerator_Maps ();
            
            System.out.println("\nMaps State Generator selected.");
        }

        
        
        // SOURCE STATE
        String str = "EEEEE*";
        Sensor currentState= new Sensor(str,tokenMap);
        

        
        
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////        
////////////////////////////////////////////////////////////////////////////////        
        
        
        
        ////////////////////////////
        // REINFORCEMENT LEARNING //
        ////////////////////////////          
        if (Reinforcement_Learning) {
            
                System.out.println("\n\nREINFORCEMENT LEARNING...\n");
        
                if (use_Value_Table) 
                    System.out.println("Using Value Table");
                
                if (use_Action_Value_Table) 
                    System.out.println("Using Action Value Table");
                
                if (use_Dynamic_Programming)
                    System.out.println("Dynamic Programming enabled");
                
                
                
                
                ReinforcementLearner rLearner = new ReinforcementLearner ();
        

                
                if (use_Value_Table) {
                    
                    stateValueTable = rLearner.createStateValueTable(Reinforcement_Learning_steps, stateGenerator, currentState, sensorList, sensorMap, ruleSetList, impossibleList, stateMap);
            
                }
                
                
                if (((use_Action_Value_Table) && (use_Dynamic_Programming))) {
                    
                    stateActionValueTable = rLearner.createStateActionValueTable_dynamic(Reinforcement_Learning_steps, stateGenerator, currentState, sensorList, sensorMap, ruleSetList, impossibleList, stateMap);
                    
                }
                
                
                if (((use_Action_Value_Table) && (!use_Dynamic_Programming))) {
                    
                    stateActionValueTable = rLearner.createStateActionValueTable(Reinforcement_Learning_steps, stateGenerator, currentState, sensorList, sensorMap, ruleSetList, impossibleList, stateMap);
               
                }

                
                
                
                
                if (use_decision_tables) {
                    
                    DecisionTable decision_table = new DecisionTable ();

                    
                    if (use_Action_Value_Table)
                        decision_table = decision_table.fromStateActionValueTable(stateActionValueTable);
                    
                    if (use_Value_Table)
                        decision_table = decision_table.fromStateValueTable(stateValueTable, stateMap);


                    if (save_Reinforcement_Learning_results) 
                        decision_table.export();
                    
                    
                    System.out.println("\nDecision Table successfully exported (" + decision_table.size() + " entries).");
                
                
                }
                
                else {
                    
                    if (save_Reinforcement_Learning_results) {
                    
                        if (use_Value_Table) {
                        
                            stateValueTable.export();
                        
                            System.out.println("\nState Value Table successfully exported (" + stateValueTable.size() + " entries).");
                        }
                        
                        if (use_Action_Value_Table) {
                            
                            stateActionValueTable.export();
                            
                            System.out.println("\nState Action Value Table successfully exported (" + stateActionValueTable.size() + " entries).");
                        }
                        
                    } 
                    
                } // Decision Tables (else)
            
        } // Reinforcement Learning (if)


        
        
        
        
        
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////        
////////////////////////////////////////////////////////////////////////////////        
        
        
        
        System.out.println("\nClosing Files...");
        
        logfiles.closeall();
} 
}
