package EnvModel.KillPredatorModel;

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
public class KillPredatorAgentBody extends AgentBody {
    
    int role;
    
    public static final int UNDEFINED = 0, PREDATOR = 1, PREY = 2;
    
    /** Creates new PredatorObject */
    public KillPredatorAgentBody(int objectID, KillPredatorEnvState predatorEnvState) {
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
    
  
    
    public KillPredatorAgentPercep getPredatorPercep() {
        return (KillPredatorAgentPercep)getPercep();
    }
    
    public KillPredatorEnvState getPredatorEnvState() {
        return (KillPredatorEnvState)getState();
    }
    
    public KillPredatorAction getPredatorAction() {
        return (KillPredatorAction)getAction();
    }
        
 
    //agent body perceives the environment
    //percept includes agent's square as the position to view from
    public void setPercep() {
        if (getPercep() == null)
            setPercep(new KillPredatorAgentPercep());
        Coordinates position = getPredatorEnvState().whereIs(this);

        //should return a function which sets the percept in the agent
        for (int i = 0; i < KillPredatorAgentPercep.SIZE -1; i++) {
            //get the contents of the squares ahead of the agent
                
            int direction = 0;
            int xDiff = 0;
            int yDiff = 0;
            switch (i)
            {
                case KillPredatorAgentPercep.BELOW: xDiff = 0;  yDiff =0; break;
                case KillPredatorAgentPercep.NORTH: xDiff = 0; yDiff = -1; break;
                case KillPredatorAgentPercep.SOUTH: xDiff = 0; yDiff = 1; break;
                case KillPredatorAgentPercep.EAST: xDiff = 1; yDiff = 0; break;
                case KillPredatorAgentPercep.WEST: xDiff = -1; yDiff = 0; break;
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
                    getPredatorPercep().setPercep(i, KillPredatorFluent.AGENT_BODY);
                else
                    getPredatorPercep().setPercep(i, KillPredatorFluent.EMPTY);
            }
            else {
                if (getPredatorEnvState().inBounds(position.getX() + xDiff, position.getY() + yDiff)) 
                    getPredatorPercep().setPercep(i, KillPredatorFluent.EMPTY);
                else
                    getPredatorPercep().setPercep(i, KillPredatorFluent.WALL);
            }
        }
        
        if (role == PREDATOR) {
            if (getPredatorEnvState().getKilledState() == (int)KillPredatorEnvState.KILL) {
                getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.POSITIVE_REWARD);
            } else if (getPredatorEnvState().getKilledState() == (int)KillPredatorEnvState.ANTI_KILL) {
                getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.NEGATIVE_REWARD);
            } else {
                getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.NO_REWARD);
            }
       
            //we've perceived this now so re-set the environment state
            //we only do this on the predator turn as its them doing the killing
            getPredatorEnvState().setKilledState(KillPredatorEnvState.NO_KILL);
            
             //KILL_LOG
            //LogFile logfile = new LogFile(1);
            //logfile.print(getPredatorPercep().getString());
            //logfile.close();
        } else {
              if (getPredatorEnvState().getKilledState() == (int)KillPredatorEnvState.KILL) {
                 getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.NEGATIVE_REWARD);
              } else if (getPredatorEnvState().getKilledState() == (int)KillPredatorEnvState.ANTI_KILL) {
                getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.POSITIVE_REWARD);
              } else {
                getPredatorPercep().setPercep( KillPredatorAgentPercep.SIZE -1, KillRewardFluent.NO_REWARD);
              }
        }
        
     
    }
    
    //this function works directly on the environment
    //at present it will just pick up the first object in the square
    //which is not itself
    public void doKill() {
        
  
        Coordinates positionMe = getPredatorEnvState().whereIs(this);
        SquareContents squareContents = getPredatorEnvState().getSquareContents(positionMe);
        
       //if it wasn't a kill then we must have tried to kill when there was nothing there
        getPredatorEnvState().setKilledState(KillPredatorEnvState.ANTI_KILL);
        
         //itterate through the square contents
        for (int i = 0; i < squareContents.size(); i++)
        {
            //make sure we don't pick ourselves up
            EnvironmentObject obj = squareContents.getEnvironmentObject(i);
            if (obj != this) {    
                //Don't remove the other agent for the moment
                //getPredatorEnvState().remove(obj, positionMe);
                //Just set an enviornment variable to say w've just killed
                getPredatorEnvState().setKilledState(KillPredatorEnvState.KILL);
            }
        }
  
  
        //return false;
    }
    
    public boolean doMove(int actionDirection) {
        int stateDirection;
        switch (actionDirection) {
            case KillPredatorAction.NORTH: {
                stateDirection = KillPredatorEnvState.NORTH; break; 
            }
            case KillPredatorAction.SOUTH: {
                stateDirection = KillPredatorEnvState.SOUTH; break; 
            }
            case KillPredatorAction.EAST: {
                stateDirection = KillPredatorEnvState.EAST; break; 
            }
            case KillPredatorAction.WEST: {
                stateDirection = KillPredatorEnvState.WEST; break; 
            }
            default : {
                stateDirection = KillPredatorEnvState.NORTH;
                LogFile logfile = new LogFile(1);
                logfile.print("\n***Illegal move direction in agent body***\n");
                logfile.close();
            }
        }
                      
        return (getPredatorEnvState().move(this, stateDirection));
    }
    
    public String getName() {
        return ("Agent Body");
    }
    
    //advance the state of the agent by timeStep
    public void advanceStep() {
        /*LogFile logfile = new LogFile(1);
        logfile.print("\n");
        if (getRole() == PREDATOR) {
            logfile.print("Predator: ");
        }
        else {
            logfile.print("Prey: ");    
        }
        logfile.close();*/
        super.advanceStep();

        if (getAction() != null)
        {
            switch (getPredatorAction().getAction()) {
                case KillPredatorAction.NOOP: {
                    break;
                }
                case KillPredatorAction.MOVE: {
                    doMove(getPredatorAction().getMoveDirection());
                    break;
                }
                case KillPredatorAction.KILL : {
                    doKill();
                    break;
                }
            }

            //we have taken this action now. Clear action to noop;
            getPredatorAction().clear();
        }
    }
    
  
    
}
