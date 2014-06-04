package EnvModel;

/*
 * PickupObject.java
 *
 * Created on March 13, 2001, 2:50 PM
 */

import java.util.*;
import Logging.*;

/**
 *
 * @author  eu779
 * @version 
 *
 * base class for all objects in the pickuop environment.
 */
public abstract class AgentBody extends EnvironmentObject {

    /*Percep contains the agent bodies current perception of the environment*/
    Percep percep;
    /*State of the agent body*/
    State state;
    /*The action which the agent body will carry out next time advance occurs*/
    /*This could be extended to a message list of actions*/
    Action action;
    
    /** Creates new PickupObject */
    public AgentBody(int objectID, State state) {
        super(objectID);
        this.state = state;
    }

    public Percep getPercep() {
        return percep;
    }
       
    public void setAction(Action newAction) {
        action = newAction;
    }
    
    public Action getAction() {
        return action;
    }
 
    //agent body perceives the environment
    //percept includes agent's square as the position to view from
    public void setPercep() {} 
   
    public void setPercep(Percep percep) {
        this.percep = percep;
    }
    
    public State getState() {
        return state;
    }
    
    /*returns a string which identifies the environment object*/
    public String getString() {
        return ("Agent Body ");
    }
    
    //advance the state of the agent by timeStep
    public void advance(float timeStep) {
        //LogFile logfile = new LogFile();
        //logfile.print(action.getString());
        //logfile.close();
        //take the action indicated by the agent
        //switch (action.getAction())
    }
    
    //advance the state of the agent in a time tick Environment
    public void advanceStep() {
        //LogFile logfile = new LogFile();
        //logfile.print("\n");
        //if (action != null)
        //    logfile.print(action.getString());
        //else
         //   logfile.print("Action not set");
        //logfile.close();
    }
        
}
