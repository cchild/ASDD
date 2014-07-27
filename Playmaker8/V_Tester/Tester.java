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
    
    
    // MSDD > 1
    static int method = 1;
    
    // MSDD MAXNODES PARAMETER
    // ONLY USED IF read_database IS SET TO FALSE
    static int maxnodes = 5000;
  
    
 
    
    
    
    
    
    
    
    
    
    public static void main (String[] args) {


    
        
        System.err.println("Initializing...");
        
     
        ////////////////////
        // INITIALIZATION //
        ////////////////////
        
        // TOKENMAP
            TokenMap tokenMap = new TokenMap();
                if (!silent_mode)
                    System.out.print("\nBUILDING TOKENMAP ...");
                tokenMap.fromFile();
                if (!silent_mode)
                    System.out.println(" OK");
            
        // SENSORLIST (DATABASE)    
            SensorList sList = new SensorList();
                if (!silent_mode)
                    System.out.print("BUILDING SENSORLIST ...");
                sList.fromFile(tokenMap);
                if (!silent_mode)
                    System.out.println(" OK");
            
            
        // SENSORMAP  
            SensorMap sMap = new SensorMap(tokenMap);
                if (!silent_mode)
                    System.out.print("BUILDING SENSORMAP ...");
                sMap.fromFile();
                if (!silent_mode)
                    System.out.println(" OK");
            
            
        // RULEMAP
            RuleMap rMap = new RuleMap(tokenMap);
                if (!silent_mode)
                    System.out.print("BUILDING RULEMAP ...");
                rMap.fromFile(sList);
                if (!silent_mode)
                    System.out.println(" OK");
                
         // INIT IMPOSSIBLELIST
                if (!silent_mode)
                    System.out.print("BUILDING IMPOSSIBLE LIST...");
                SensorList impossibleList = sList.getImpossibleList(sMap);
         
                if (!silent_mode)
                    System.out.println(" OK");
         
       
        // CLOSEDLIST        
            RuleList closedList = new RuleList();
        
            
        // RULESETLIST    
            RuleSetList rsList = new RuleSetList(closedList, sList);
      
            
        // OTHERS    
            ArrayList rules = new ArrayList ();
            rules.add(closedList);        
            rules.add(rsList);
        

        // LOGFILES
        LogFiles logfiles = getInstance();
        
        // IF CREATING A NEW DATABASE, ERASES THE OLD ONE
        if (!read_database) {

             logfiles = getInstance(5);
             logfiles = getInstance(3);

         }        
        
        
        
        
        
        ///////////////////////////
        // MSDD - LEARNING RULES //
        ///////////////////////////
        if ( (!read_database) && (method == 1) ) {
            System.err.println("\nLearning MSDD Rules ...");

            rules = RuleLearnerMSDD_Sensor.learnRulesMSDD(tokenMap, sMap, rMap, sList, rsList, silent_mode, read_database, maxnodes);


            closedList = (RuleList) rules.get(0);

            rsList = (RuleSetList) rules.get(1);
        
            
            System.err.println("\n");
            
            System.err.println(closedList.size() + " Rules successfully learned. " + rsList.size() + " RuleSets.");
        
        }
        
        
        
        
        //////////////////////////
        // MSDD - LOADING RULES //
        //////////////////////////
        if ( (read_database) && (method == 1)) {
                System.err.println("\nLoading MSDD Rules ...");
             
                closedList.fromFile(sList, tokenMap);
             
             
                rsList.fromFile();

                
                System.err.println(closedList.size() + " Rules successfully learned. " + rsList.size() + " RuleSets.");
        
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
            
            System.err.print("\nExporting ClosedList...");
            
            closedList.export();
                       
            System.err.println(" OK");
            
            
            
            System.err.print("\nExporting RuleSetList...");
            
            rsList.export();
                        
            System.err.println(" OK");
            
        }
                     
        
        
        
        System.err.println("\nClosing Files...");
        logfiles.closeall();
        
        
        
        
        
        /////////////////////
        // STATE GENERATOR //
        /////////////////////   
        System.err.println("\n\nRunning State Generator...");
        
        StateGenerator stateGenerator = new MSDD_StateGenerator ();
        
        
        
        // SOURCE STATE
        String str = "WEEEE*";
        Sensor currentState= new Sensor(str,tokenMap);
        

        

        //SensorList res = new SensorList ();
        //res.addSensor(currentState);
        
        
        
        
        int won = 0;
        
        System.err.println("\nSOURCE STATE : " + currentState);


        System.err.print("\nREINFORCEMENT LEARNING...");
        
        
        ReinforcementLearner rLearner = new ReinforcementLearner ();
        
        StateTable sTable = rLearner.createTable(2000, stateGenerator, currentState, sList, sMap, rsList, impossibleList, won);
                    
        System.err.println(" OK");
        //res.printList("500 OCCURENCIES");
            

        sTable = rLearner.cleanTable(sTable);
        
        
        //sTable.fromSensorList(res);
        
        sTable.printTable("STATETABLE");
        
        
        //System.err.println("\nWON " + won + " times.");
        
        
        

        
    
    
} 
}
