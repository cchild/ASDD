package EnvModel.PaintModel;

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
public class PaintBlock extends EnvironmentObject {
    
    int state;
    
    public static final int UNDEFINED = 0, BLOCK_PAINTED = 1, NOT_BLOCK_PAINTED = 2;
    
    /** Creates new PredatorObject */
    public PaintBlock(int objectID) {
        super(objectID);
        state = NOT_BLOCK_PAINTED;
    }
    
    public String getState() {
        if (state == BLOCK_PAINTED) {
            return ("Block painted");
        } else if (state == NOT_BLOCK_PAINTED) {
            return ("Not block painted");
        } else {
            return ("error state");
        }
    }
    
    public int getStateID() {
        return state;
    }
    
     public void setState(int newState) {
         state = newState;
     }
 
  
    public String getName() {
        return ("Paint block");
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
    }
    
  
    
}
