package EnvModel;

import java.io.*;
import EnvAgent.ClauseLearner.*;
/*
 * Action.java
 *
 * Created on March 6, 2001, 6:24 PM
 */


/**
 *
 * @author  Chris Child
 * @version 
 * This will contain all "available" actions for the agent and works as an 
 * interface between the agent and the agent body. The action should contain 
 * all possible actions for the agent and should not allow mutually exclusive actions
 * to be set.
 */
public abstract class Action extends RuleObject implements Cloneable, Serializable {

    /** Creates new Action */
    public Action() {;
    }
    
    public Object clone() {
          //try {
           	    Action a = (Action)super.clone();
                    return a;				// return the clone
            //} catch (CloneNotSupportedException e) {
                 // this shouldn't happen because Stack is Cloneable
             //   throw new InternalError();
            //}
    
    }
    
      /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        super.writeObject(s);
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        super.readObject(s);
    }
    
    public void randomAction() {
        setByValue((int)(getNumValues() * Math.random()));
        while (!legalAction()) {
            setByValue((int)(getNumValues() * Math.random()));
        }        
    }
    
     public void firstLegalAction() {
        int which = 0;
        setByValue(which);
        while (!legalAction()) {
            which ++;
            setByValue(which);
        }        
    }

    public Action nextLegalAction() {
        int which = getValue();
        which ++;
        setByValue(which);
        while (!legalAction() && which < getNumValues()) {
             which ++;
             setByValue(which);
        }

        if (legalAction())
            return this;
        else
            return null;
    }
    
    public abstract void randomDumbAction();
    
    /*returns a tokenised representation of the action as a string*/
    public abstract String toString();
    /*returns true if this element is currently set to the wildcard state*/
    abstract public boolean isWildcard();
    
    //returns the number of values which this action can take
    public abstract int getNumValues();
    
    public abstract void setByValue(int value);
    
    
    
    /*returns true if the action is invalid (e.g. NOOP NORTH);*/
    public abstract boolean isInvalid();
    
    public abstract void copy(RuleObject o);
    
    public abstract int getValue();
    
    public abstract boolean legalAction();
    
    public abstract ClauseElements convertToClauseElements(boolean precursor);
    
    public abstract void setDefault();
    
}
