package EnvAgent.ClauseLearner;

import java.io.*;
import java.util.*;

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
public abstract class Term extends Object implements Cloneable, Serializable {

    short uniqueID;
    
    static short uniqueIDCounter = 0;
    int hashCode;
    
    /** Creates new Action */
    public Term() {;
        hashCode = 0;
    }
    
    public void setUniqueID() {
        uniqueIDCounter ++;
        uniqueID = uniqueIDCounter;
    }
    
    public int getUniqueID() {
        return uniqueID;
    }
        
    
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        try {
            Term o = (Term)super.clone();	// clone the percep
            o.uniqueID = uniqueID;
            o.uniqueIDCounter = uniqueIDCounter;
            o.hashCode = hashCode;
            return o;				// return the clone
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
            throw new InternalError();
        }
    }
    
    /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeShort(uniqueID);
        s.writeInt(hashCode);
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        s.defaultReadObject();
        uniqueID = s.readShort();
        hashCode = s.readInt();
        if (uniqueID > uniqueIDCounter)
            uniqueIDCounter = uniqueID;
    }
    
    public abstract String toString();

    public int getHashCode() {
        if (hashCode !=0)
            return hashCode;
        else {
            String st = toString();
            hashCode = st.hashCode();
            return hashCode;
        }
    }

    public boolean stringIsEqual(String oString){
        if (hashCode ==0) {
            hashCode = getHashCode();
        }
        if (oString.hashCode() == hashCode)
            return true;
        else
            return false;
    }

    public boolean isEqual(Term o) {
        if (!o.getClass().equals(this.getClass())) {
            //We shouldn't stop at this point
            return false;
        }
   
        if (o.getValue() != this.getValue())
            return false;
        
        //if (!o.getType().equalsIgnoreClase(this.getType()))
        //    return false;
        
        return true;
    }
    
    /*returns the number of value that can be taken by this rule object
     */
    public abstract int getNumValues();
    
    /*Value as in set by value*/
    public abstract int getValue();
    
    /*returns true if these rule elements match. Wildcards not counted*/
    public abstract boolean equals(Term o);
    
    /*returns true if no variables present*/
    public abstract boolean isGround();
    
    public boolean isVariable() {
        return false;
    }
    
    public ArrayList getVariables() {
        return null;
    }
    
    public int findID(ClauseList level1Clauses){
        
        if (this.getUniqueID() != 0)
            return getUniqueID();
        
        if (level1Clauses != null) {
            for (int findLoop = 0; findLoop < level1Clauses.size(); findLoop ++) {
                Term level1ClauseHead = level1Clauses.get(findLoop).getClauseHead();
                if (level1Clauses.get(findLoop).getClauseHead() == null) {
                    if (level1Clauses.get(findLoop).getClauseElements().get(0).isEqual(this))
                        return level1Clauses.get(findLoop).getClauseElements().get(0).getUniqueID();
                } else {
                    if (level1Clauses.get(findLoop).getClauseHead().isEqual(this))
                        return level1Clauses.get(findLoop).getClauseHead().getUniqueID();
                }
            }
        }
        
        return 0;
    }
    
    public void setID(int newID) {
        uniqueID = (short)newID;
    }
}
