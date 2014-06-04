package EnvModel;

/*
 * State.java
 *
 * Created on March 6, 2001, 6:24 PM
 */


/**
 *
 * @author  eu779
 * @version 
 * This will contain the current state of the environment, including the
 * state of all objects within the environment
 */
public abstract class State extends Object {

    /*This is incremented each time a new object is created to
     *allow each environment object to have a unique reference number*/
    int nextEnvironmentObjectID;
    
    /** Creates new State */
    public State() {;
        nextEnvironmentObjectID = -1;
    }
    
    public void outputState() {;}
    
    /*advance for time based agent environment*/
    public void advance(float time) {;}
    
    /*advance for step based agent environment*/
    public void advanceStep() {;}
    
    /*Returns a unique reference ID for the next environment object*/
    protected int getNextObjectID() {
        nextEnvironmentObjectID ++;
        return nextEnvironmentObjectID;
    }
    
    /*Object creation so that we can create environment objects with unique IDs.
     * Should be over-ridden to create new object types*/
    protected abstract EnvironmentObject createObject(String objectType);
}
