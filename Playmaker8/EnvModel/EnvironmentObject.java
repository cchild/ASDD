package EnvModel;

import Logging.*;
/*
 * EnvironmentObject.java
 *
 * Created on March 13, 2001, 2:50 PM
 */


/**
 *
 * @author  Chris Child
 *
 * base class for all objects in the environment.
 */
public abstract class EnvironmentObject extends Object {

    int objectID;
    /** Creates new PickupObject */
    public EnvironmentObject(int objectID) {
        this.objectID = objectID;
    }
    
    public int getObjectID() {
        return objectID;
    }
    
    /*name of the object type as a string*/
    public abstract String getName();
    public void output() {
        LogFiles logfile = LogFiles.getInstance();
        logfile.print(getName(),1);
        
    }
    
    /*advance the state of this object by a time step for timed environments*/
    public void advance(float timeStep) {;}
    /*advance the state of the object by a step for tick based environments*/
    public void advanceStep() {;}
}
