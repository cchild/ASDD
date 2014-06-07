package EnvModel.PredatorModel;

/*
 * PredatorAgentBody.java
 *
 * Created on March 13, 2001, 2:50 PM
 */

import java.util.*;
import EnvModel.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * The agent body is an environment object and as such is part of the
 * environment itself. The body implements setting the percept from 
 * the current state of the environment. It also implements the actions
 * available to the agent by directly effecting the environment.
 */
public class PredatorAgentBody extends AgentBody {
    
    int role;
    
    public static final int UNDEFINED = 0, PREDATOR = 1, PREY = 2;
    
    /** Creates new PredatorObject */
    public PredatorAgentBody(int objectID, PredatorEnvState predatorEnvState) {
        super(objectID, predatorEnvState);
        role = UNDEFINED;
    }

    public Percep getPercep() {
        return super.getPercep();
    }
    
    public int getRole() {
        return role;
    }
    
    public void setRole(int role) {
        this.role = role;
    }
    
  
    
    public PredatorAgentPercep getPredatorPercep() {
        return (PredatorAgentPercep)getPercep();
    }
    
    public PredatorEnvState getPredatorEnvState() {
        return (PredatorEnvState)getState();
    }
    
    public PredatorAction getPredatorAction() {
        return (PredatorAction)getAction();
    }
        
 
    //agent body perceives the environment
    //percept includes agent's square as the position to view from
    public void setPercep() {
        if (getPercep() == null)
            setPercep(new PredatorAgentPercep());
        Coordinates position = getPredatorEnvState().whereIs(this);

        //should return a function which sets the percept in the agent
        for (int i = 0; i < PredatorAgentPercep.SIZE; i++) {
            //get the contents of the squares ahead of the agent
                
            int direction = 0;
            int xDiff = 0;
            int yDiff = 0;
            switch (i)
            {
                case PredatorAgentPercep.BELOW: xDiff = 0;  yDiff =0; break;
                case PredatorAgentPercep.NORTH: xDiff = 0; yDiff = -1; break;
                case PredatorAgentPercep.SOUTH: xDiff = 0; yDiff = 1; break;
                case PredatorAgentPercep.EAST: xDiff = 1; yDiff = 0; break;
                case PredatorAgentPercep.WEST: xDiff = -1; yDiff = 0; break;
            }
            
            SquareContents contents = getPredatorEnvState().getSquareContents(position.getX() + xDiff, position.getY() + yDiff);
            if (contents != null) {
                boolean agentPresent = false;
                for (int j = 0; j < contents.size(); j++) {
                    
                    //check that we're not looking at ourselves
                    if (contents.getEnvironmentObject(j) != this) {
                      
                    
                        String name = contents.getEnvironmentObject(j).getName();
                    
                        if (name.equals("Agent Body")) {
                            agentPresent = true;
                            break;
                        }
                        //example of other objects present
                        //else if (name.equals("Spanner"))
                        //    spannerPresent = true;
                    }  
                }
                
                //now set the percep to what the agent can see
                if (agentPresent)
                    getPredatorPercep().setPercep(i, PredatorFluent.AGENT_BODY);
                else
                    getPredatorPercep().setPercep(i, PredatorFluent.EMPTY);
            }
            else {
                if (getPredatorEnvState().inBounds(position.getX() + xDiff, position.getY() + yDiff)) 
                    getPredatorPercep().setPercep(i, PredatorFluent.EMPTY);
                else
                    getPredatorPercep().setPercep(i, PredatorFluent.WALL);
            }
        }
        //Singleton logfile = Singleton.getInstance();
        //logfile.print(getPredatorPercep().getString());
        // 
    }
    
  
    
    public boolean doMove(int actionDirection) {
        int stateDirection;
        switch (actionDirection) {
            case PredatorAction.NORTH: {
                stateDirection = PredatorEnvState.NORTH; break; 
            }
            case PredatorAction.SOUTH: {
                stateDirection = PredatorEnvState.SOUTH; break; 
            }
            case PredatorAction.EAST: {
                stateDirection = PredatorEnvState.EAST; break; 
            }
            case PredatorAction.WEST: {
                stateDirection = PredatorEnvState.WEST; break; 
            }
            default : {
                stateDirection = PredatorEnvState.NORTH;
                Singleton logfile = Singleton.getInstance();
                logfile.print("\n***Illegal move direction in agent body***\n",1);
                
            }
        }
                      
        return (getPredatorEnvState().move(this, stateDirection));
    }
    
    public String getName() {
        return ("Agent Body");
    }
    
    //advance the state of the agent by timeStep
    public void advanceStep() {
        /*Singleton logfile = Singleton.getInstance();
        logfile.print("\n");
        if (getRole() == PREDATOR) {
            logfile.print("Predator: ");
        }
        else {
            logfile.print("Prey: ");    
        }
         */
        super.advanceStep();

        if (getAction() != null)
        {
            switch (getPredatorAction().getAction()) {
                case PredatorAction.NOOP: {
                    break;
                }
                case PredatorAction.MOVE: {
                    doMove(getPredatorAction().getMoveDirection());
                    break;
                }
            }

            //we have taken this action now. Clear action to noop;
            getPredatorAction().clear();
        }
    }
    
  
    
}
