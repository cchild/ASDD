
/*
 * PredatorAction.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

package EnvModel.PaintModel;
import EnvAgent.ClauseLearner.*;

import EnvModel.*;

import java.io.*;

/**
 *
 * @author  Chris Child
 * @version 
 * This will contain all "available" actions for the agent and works as an 
 * interface between the agent and the agent body. The action should not
 * allow mutually exclusive actions to be set
 */
public class PaintAction extends Action implements Cloneable, Serializable {

    public static final int
        NOOP =0, DRY =1, NEW = 2, PAINT = 3, PICKUP = 4, WILDCARD = 5;
 
    private int action;
   
    public PaintAction() {
        super();
        clear();
    }
    
        /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        super.writeObject(s);
        s.writeInt(action);
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        super.readObject(s);
        action = s.readInt();
    }
    
    /*return the int refernece of the current action*/
    public int getAction() {
        return action;
    }
    
    public void setWildcard() {
        action = WILDCARD;
    }
    
    public boolean legalAction(){
        if (action == NOOP || action == WILDCARD)
            return false;
        return true;
    }
   
   
    /*Actions need to be cloneable so that a list of actions can be stored
     *by the agent*/
    public Object clone() {
        
        //try {
           	PaintAction a = (PaintAction)super.clone();	// clone the stack
                a.action = action;
                return a;	
            //} catch (CloneNotSupportedException e) {
                 // this shouldn't happen because Stack is Cloneable
            //    throw new InternalError();
            //}
   
    }
    
    /*clear the action to do nothing state*/
    public void clear() {
        action = NOOP;
    }
    
    /*Return a tokenised string representing the currently set action*/
    public String toString() {
        String actionString = "[";
        
        if (action == WILDCARD)
            return "*";
        
        switch (action) {
            case NOOP: {
                actionString += "NOOP";
                break;
            }
            case DRY: {
                actionString += "DRY";
                break;
            }
            case NEW: {  
                actionString += "NEW";
                break;
            }
            case PAINT: {  
                actionString += "PAINT";
                break;
            }
            case PICKUP: {  
                actionString += "PICKUP";
                break;
            }
            default: {
                actionString += "ERROR";
                break;
            }
        }
        
        actionString += "]";
    
        return actionString;
    }
    
    
    public String translation() {
        String actionString = "";
        
        if (action == WILDCARD)
            return "*";
        
        switch (action) {
            case NOOP: {
                actionString += "NOOP";
                break;
            }
            case DRY: {
                actionString += "DRY";
                break;
            }
            case NEW: {  
                actionString += "NEW";
                break;
            }
            case PAINT: {  
                actionString += "PAINT";
                break;
            }
            case PICKUP: {  
                actionString += "PICKUP";
                break;
            }
            default: {
                actionString += "ERROR";
                break;
            }
        }
        
        actionString += "";
    
        return actionString;
    }
 
    public boolean isWildcard() {
        if (action == WILDCARD)
            return true;
        else
            return false;
    }
    
    //returns the number of values which this action can take
    public int getNumValues() {
        /*wildcard will always be the last action value
        *and therefore the number of values which this action
        *can take.
        *AFTER_ACTIONS is one above the last action.
         */
   
        return WILDCARD;
    }
    
    public void setByValue(int value) {
        
        action = value;
        
        if (action >= WILDCARD) {
            int error = 1;
        }
   
    }
    
    public boolean isInvalid() {
        if (action > WILDCARD)
            return true;
        if (action == NOOP)
            return true;
        return false;
    }
    
    public boolean equals(RuleObject o) {
        if (action == ((PaintAction)o).getAction())
            return true;
        return false;
    }
    
    public void copy(RuleObject o) {
        action = ((PaintAction)o).getAction();
    }
    
    
    //get the value of this action as it would be if set by value
    public int getValue() {
        int value = action;
        return value;
    }
    
     public ClauseElements convertToClauseElements(boolean precursor) {
         ClauseElements clauseElements = new ClauseElements();
         if (precursor == true) {
            Clause action = new Clause("Act", 1);
            Variable theAction = new Variable();
            theAction.setType("PaintAct");
            theAction.setValue(this.getValue());
            action.setPredicate(0, theAction);
            clauseElements.add(action);
        }
         
         return clauseElements;
     }
     
     public void setDefault() {
         setByValue(PaintAction.DRY);
     }
     
     public void randomDumbAction() {
         randomAction();
     }
     
     public void readFromString(String str) {
         
     }
}
