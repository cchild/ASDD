package EnvModel;


import EnvAgent.*;
import EnvAgent.PredatorAgent.*;
import Logging.*;
import V_ReinforcementLearner.StateTable;
import V_Sensors.Sensor;
import V_Sensors.Token;
import V_Sensors.TokenMap;
import V_StateGenerator.MSDD_StateGenerator_Maps;
import V_StateGenerator.MSDD_StateGenerator_Rules;

//This is a clock tick based environment. Rather than float action timers this
//environment works in clock ticks and is therefore easier to debug.
//In this version of the environment agents are able to act directly on the
//state of the environment because it makes for much nicer code.
//In this way agents can now directly influence their bodies rather
//than waiting for the environment to do it.
//The dissadvantage is that you cannot resolve simultaneous actions any more.
//We can then also remove the list of actions which the environment has and allow
//the agent to do things like act directly on its body.

import java.util.*;

public abstract class TickEnvironment extends Environment
{
  
    /*True if this is a turn based moves environment*/ 
    protected boolean turnBasedMoves;
    /*True if this is a turn based perceptions environment*/
    protected boolean turnBasedPerceptions;
    
    /**Constructor for the environment*/
    public TickEnvironment() {
        initEnvironment();
    }
    
    public TickEnvironment(boolean turnBasedMoves, boolean turnBasedPerceptions) {
        initEnvironment();
        this.turnBasedMoves = turnBasedMoves;
        this.turnBasedPerceptions = turnBasedPerceptions;
    }
    
    protected void initEnvironment() {
        super.initEnvironment();
        this.turnBasedMoves = true;
        this.turnBasedPerceptions = true;
    }
    
   
    /*add an agent (as opposed to an agent body) to the environment
     */
    public void addAgent (Agent agent) { 
        agents.addAgent(agent);
    }
    
    /*remove an agent from the environment. Also removes their turn-record*/
    public void removeAgent (Agent agent) {
        agents.removeAgent(agent);
    }
    
    /*If the environment has turn based moved or perceptions it is turn based*/
    public boolean getTurnBased() {
        return (turnBasedMoves || turnBasedPerceptions);
    }
    
    /*Get the first agent from the list and outputs the string representation
     *of their perceptions and actions*/
    public void testAgentRecords() {
        Agent agent = agents.getAgent(0);
        if (!agent.getRealThinker()) {
            agent = agents.getAgent(1);
        }
        ActionRecord actionRecord = agent.getActionRecord();
        PercepRecord percepRecord = agent.getPercepRecord();

        if (agent.LEARN_CLAUSES_ASDD || agent.LEARN_RULES_APRIORI)
        {
            //only print out this big file if we're not doing the enormous one
            for (int i = 0; i < percepRecord.size(); i++) {
                System.out.print("\n" + i);
                System.out.print(percepRecord.getPercep(i).toString());
                System.out.print(" ");
                System.out.print(actionRecord.getAction(i).toString());
            }
        }
        
        System.out.print("\n");
        System.out.flush();
        
        agent.savePrecepActionRecord();
        
        
        if (!agent.USE_REINFORCEMENT_POLICY) {
            //agent.learnRules();
            if (agent.LEARN_RULES_MSDD || agent.LEARN_RULES_APRIORI || !agent.USE_SAVED_STATE_ACTION_MAP)
                agent.learnRules();
            if (agent.LEARN_CLAUSES_ASDD)
                agent.learnClauses();
        }
            
        
    }
    
    
    public void testAgentRecords2(int limit) {
        Agent agent = agents.getAgent(0);
        if (!agent.getRealThinker()) {
            agent = agents.getAgent(1);
        }
        ActionRecord actionRecord = agent.getActionRecord();
        PercepRecord percepRecord = agent.getPercepRecord();

        if (agent.LEARN_CLAUSES_ASDD || agent.LEARN_RULES_APRIORI)
        {
            //only print out this big file if we're not doing the enormous one
            for (int i = 0; i < limit; i++) {
                System.out.print("\n" + i);
                System.out.print(percepRecord.getPercep(i).toString());
                System.out.print(" ");
                System.out.print(actionRecord.getAction(i).toString());
            }
        }
        
        System.out.print("\n");
        System.out.flush();
        
        agent.savePrecepActionRecord();
        
        
        if (!agent.USE_REINFORCEMENT_POLICY) {
            //agent.learnRules();
            if (agent.LEARN_RULES_MSDD || agent.LEARN_RULES_APRIORI || !agent.USE_SAVED_STATE_ACTION_MAP)
                agent.learnRules();
            if (agent.LEARN_CLAUSES_ASDD)
                agent.learnClauses();
        }
            
        
    }
    
    
    
        
    
    /*move everything in the environment along by "time" given
     *in seconds. All agents, objects and environment actions are
     *advanced*/
    public void updateEnvironment() {
       
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.getAgent(i);
            //System.out.println("Agent : " + current);
            boolean currentAgentsTurn =agents.getAgentsTurn(current);
            agents.updateAgentsTurn(current);
            
            
            if ((turnBasedPerceptions && currentAgentsTurn)
                || !turnBasedPerceptions) {
                if (current.getRealThinker()) {
                    current.perceive();
                    //System.out.println("1");
                }
            }
            
            if ((turnBasedPerceptions && currentAgentsTurn) 
                || !turnBasedMoves)
            {
                Action action = current.deliberate();
                current.act(action);
                //System.out.println("Action : " + action);
                //System.out.println("Position : " + current.getPercep());
                //KILL_LOG
                /*if (current.getRealThinker()) {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print(action.toString() + " \n");
                     
                }*/

                
//                                if (i == 0) {
//                
//                    
//                    System.out.println("/////////////////////");
//        
//                    TokenMap tokenMap = new TokenMap ();
//                
//                    //tokenMap.fromFile();
//                    
//                    
//                    StateTable nop = new StateTable ();
//        
//                    //nop = nop.fromFile(tokenMap);
//        
//                    //nop.printTable("IMPORTED");
//                    System.out.println("\nPercep : " + current.getPercep());
//           
//
//                    
//                    System.out.println("Action : " + action);
//
//                    String str = current.getPercep().translation() + "*";
//                
//
//                
//                    Sensor currentState = new Sensor (str, tokenMap );
//                
//                    System.out.println("Current State : " + currentState);
//                    
//                    if (currentState.isRewarded())
//                        System.out.println("REWARD");
//                }
            }
            
          
        }
        
        /*Note: There are currently no turn based environment actions*/
            
        /*advance all objects and elements of environment state by one step*/
        state.advanceStep();
        /*Output the current state to standard io*/
        //state.outputState();
    }
    
    
    
    
    public void updateEnvironmentFromRL() {
       
        for (int i = 0; i < agents.size(); i++) {
            Agent current = agents.getAgent(i);
            //System.out.println("Agent : " + current);
            boolean currentAgentsTurn =agents.getAgentsTurn(current);
            agents.updateAgentsTurn(current);
            
            
            if (i == 0) {
            
                 }
            
            
            if ((turnBasedPerceptions && currentAgentsTurn)
                || !turnBasedPerceptions) {
                if (current.getRealThinker()) {
                    current.perceive();
                    //System.out.println("1");
                }
            }
            
            if ((turnBasedPerceptions && currentAgentsTurn) 
                || !turnBasedMoves)
            {
                Action action = current.deliberate();
                
                
                // INTERCEPTS THE ACTION
                if (i == 0) {
                
                    
                    System.out.println("/////////////////////");
        
                    TokenMap tokenMap = new TokenMap ();
                
                    tokenMap.fromFile();
                    
                    
                    StateTable nop = new StateTable ();
        
                    nop = nop.fromFile(tokenMap);
        
                    //nop.printTable("IMPORTED");
                    System.out.println("\nPercep : " + current.getPercep());
           

                    
                    System.out.println("Action : " + action);

                    String str = current.getPercep().translation() + "*";
                

                
                    Sensor currentState = new Sensor (str, tokenMap );
                
                    System.out.println("Current State : " + currentState);
                    
                    if (currentState.isRewarded())
                        System.out.println("REWARD");
                    
                    
                    MSDD_StateGenerator_Maps sGen = new MSDD_StateGenerator_Maps ();
                    
                    Token chosenAction = sGen.generateActionFromTable(currentState, nop);
                    
                    
                    System.out.println("Chosen Action : " + chosenAction);
                    
                    
                    // 6 MOVE NORTH
                    // 7 MOVE EAST
                    // 8 MOVE SOUTH
                    // 9 MOVE WEST
                    
                    
                    switch (chosenAction.toString()) {
                        case "W":  System.out.println("West ?");
                            action.setByValue(9);
                            break;
                        case "E":  System.out.println("East ?");
                            action.setByValue(7);
                            break;
                        case "S":  System.out.println("South ?");
                            action.setByValue(8);
                            break;
                        case "N":  System.out.println("North ?");
                            action.setByValue(6);
                            break;
                        default : System.out.println("What ? ");
                    }
                }
                
                
                
                
                
                System.out.println("Acting :  " + action);
                current.act(action);
                
                
                
                
                
                //KILL_LOG
                /*if (current.getRealThinker()) {
                    Singleton logfile = Singleton.getInstance();
                    logfile.print(action.toString() + " \n");
                     
                }*/
            }
            
          
        }
        
        /*Note: There are currently no turn based environment actions*/
            
        /*advance all objects and elements of environment state by one step*/
        state.advanceStep();
        /*Output the current state to standard io*/
        //state.outputState();
    }
        
        
        
    public  boolean getAgentsTurn(Agent agent) {
         return agents.getAgentsTurn(agent);
    }
}
      

