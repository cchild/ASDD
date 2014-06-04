package EnvModel;

import java.io.*;

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
public abstract class RuleObject extends Object implements Cloneable, Serializable {

    /** Creates new Action */
    public RuleObject() {
        ;
    }
    
    public Object clone()  {
         try {
           	RuleObject o = (RuleObject)super.clone();
                return o;
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
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
    }
    
    public abstract String toString();
    public String getRuleString() {
        return toString();
    }
    
    /*Set the rule object to a don't care state which will match with anything (e.g. "*")
     */
    public abstract void setWildcard();
    
    /*returns whether this rule object is set to the wildcard (*) state
     */
    public abstract boolean isWildcard();
    
    public boolean isEqual(RuleObject o) {
        if (!o.getClass().equals(this.getClass())) {
            //We shouldn't stop at this point
            return false;
        }
   
        if (o.getValue() != this.getValue())
            return false;
        
        return true;
    }
    
    /*returns the number of value that can be taken by this rule object
     */
    public abstract int getNumValues();
    
    /*A function for setting rule by a number so that we can itterate through all values*/
    public abstract void setByValue(int value);
    
    /*Value as in set by value*/
    public abstract int getValue();
    
    /*returns true if these rule elements match. Wildcards not counted*/
    public abstract boolean equals(RuleObject o);
    
    /*copy the given rule element*/
    public abstract void copy(RuleObject o);
    
    
}
