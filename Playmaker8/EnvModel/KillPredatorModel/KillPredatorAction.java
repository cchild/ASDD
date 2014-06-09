
/*
 * PredatorAction.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

package EnvModel.KillPredatorModel;

import EnvModel.*;
import EnvAgent.ClauseLearner.*;

import java.io.*;

/**
 *
 * @author  Chris Child
 * @version 
 * This will contain all "available" actions for the agent and works as an 
 * interface between the agent and the agent body. The action should not
 * allow mutually exclusive actions to be set
 */
public class KillPredatorAction extends Action implements Cloneable, Serializable {

    public static final int
        NOOP =0, MOVE =1, KILL = 2, WILDCARD=3;
    public static final int
        NOTSET = 0, NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4, AFTER_DIRECTIONS = 5;
    /** Creates new State */
    private int action;
    private int moveDirection;
    public KillPredatorAction() {
        super();
        clear();
    }
    
        /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        super.writeObject(s);
        s.writeInt(action);
        s.writeInt(moveDirection);
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        super.readObject(s);
        action = s.readInt();
        moveDirection = s.readInt();
    }
    
    /*return the int refernece of the current action*/
    public int getAction() {
        return action;
    }
    
    public void setWildcard() {
        action = WILDCARD;
        moveDirection = 0;
    }
    
    public boolean legalAction(){
        if (action == NOOP)
            return false;
        if (action == MOVE && (moveDirection == NOTSET || moveDirection == AFTER_DIRECTIONS))
            return false;
        if (action == KILL && moveDirection != NOTSET)
            return false;
        return true;
    }
    
    /*return the int reference of the current move direction*/
    public int getMoveDirection() {
        return moveDirection;
    }
   
    /*Actions need to be cloneable so that a list of actions can be stored
     *by the agent*/
    public Object clone() {
        
        //try {
           	KillPredatorAction a = (KillPredatorAction)super.clone();	// clone the stack
                a.action = action;
                a.moveDirection = moveDirection;
                return a;	
            //} catch (CloneNotSupportedException e) {
                 // this shouldn't happen because Stack is Cloneable
            //    throw new InternalError();
            //}
   
    }
    
    /*Set the action to move in the given direction*/
    public void setMove(int newDirection) {
        action = MOVE;
        moveDirection = newDirection;
    }
   
    /*clear the action to do nothing state*/
    public void clear() {
        action = NOOP;
        moveDirection = NOTSET;
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
            case MOVE: {
                actionString += "MOVE";
                break;
            }
            case KILL: {
                actionString += "KILL";
                break;
            }
            default: {
                actionString += "ERROR";
                break;
            }
        }
        
        if (action == MOVE) {
            actionString += " ";

            switch (moveDirection) {
                case NORTH: {
                    actionString += "NORTH";
                    break;
                }
                case EAST: {
                    actionString += "EAST";
                    break;
                }
                case SOUTH: {
                    actionString += "SOUTH";
                    break;
                }
                case WEST: {
                    actionString += "WEST";
                    break;
                }
                case NOTSET: {
                    actionString += "NOTSET";
                    break;
                }
                default: {
                    actionString += "ERROR";
                    break;
                }
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
            case MOVE: {
                actionString += "";
                break;
            }
            case KILL: {
                actionString += "KILL";
                break;
            }
            default: {
                actionString += "ERROR";
                break;
            }
        }
        
        if (action == MOVE) {
            actionString += "";

            switch (moveDirection) {
                case NORTH: {
                    actionString += "N";
                    break;
                }
                case EAST: {
                    actionString += "E";
                    break;
                }
                case SOUTH: {
                    actionString += "S";
                    break;
                }
                case WEST: {
                    actionString += "W";
                    break;
                }
                case NOTSET: {
                    actionString += "NOTSET";
                    break;
                }
                default: {
                    actionString += "ERROR";
                    break;
                }
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
        //This next comment is how it used to be done, which seems to make no sense
        /*wildcard will always be the last action value
        *and therefore the number of values which this action
        *can take.
        *AFTER_ACTIONS is one above the last action.
         */
        //return WILDCARD * AFTER_DIRECTIONS;
        
        //There are actually five actions. 4 moves + KILL
        return 4 + 1;
    }
    
    public void setByValue(int value) {
        
        switch (value) {
            case 0: action = MOVE; moveDirection = NORTH; break;
            case 1: action = MOVE; moveDirection = EAST; break;
            case 2: action = MOVE; moveDirection = SOUTH; break;
            case 3: action = MOVE; moveDirection = WEST; break;
            case 4: action = KILL; moveDirection = NOTSET; break;
            default: action = NOOP; moveDirection = NOTSET; break;
        }
        
        //This is how it used to be done, which seems odd
        /*action = 0;
        //slightly odd way of doing things, but we're
        //setting the value to the number of direcitons
        while (value > WEST) {
            action ++;
            value -= (WEST + 1);
        }
        moveDirection = value;
        
        if (action >= WILDCARD) {
            int error = 1;
        }
        if (moveDirection > WEST) {
            int error = 1;
        }*/
    }
    
    public boolean isInvalid() {
        if (action > WILDCARD)
            return true;
        if (action == NOOP)
            if (moveDirection != NOTSET)
                return true;
        return false;
    }
    
    public boolean equals(RuleObject o) {
        if (action == ((KillPredatorAction)o).getAction())
            if (moveDirection == ((KillPredatorAction)o).getMoveDirection())
                return true;
        return false;
    }
    
    public void copy(RuleObject o) {
        action = ((KillPredatorAction)o).getAction();
        moveDirection = ((KillPredatorAction)o).getMoveDirection();
    }
    
    
    //get the value of this action as it would be if set by value
    public int getValue() {
        if (action == MOVE) {
            if (moveDirection != NOTSET)
                return moveDirection-1;
            else
                return 40; //an error      
        } else {
            if (action == KILL)
                return 4;
            else
                return 40; //an error
        }
    }
    
     public ClauseElements convertToClauseElements(boolean precursor) {
         ClauseElements clauseElements = new ClauseElements();
         if (precursor == true) {
            Clause action = new Clause("Act", 1);
            Variable theAction = new Variable();
            theAction.setType("KillAct");
            theAction.setValue(this.getValue());
            action.setPredicate(0, theAction);
            clauseElements.add(action);
        }
         
         return clauseElements;
     }
     
    public void randomDumbAction() {
        setByValue((int)(getNumValues() * Math.random()));
        while (!legalAction() || action != MOVE) {
            setByValue((int)(getNumValues() * Math.random()));
        }        
    }
     
     public void setDefault() {
         setMove(3);
     }
}
