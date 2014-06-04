/*
 * Fluent.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvModel;

import java.io.*;
import EnvAgent.ClauseLearner.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class Fluent extends RuleObject implements Cloneable, Serializable {

    short currentValue;
    short numValues;
    
    public Fluent()
    {
        this.numValues = 0;
        this.currentValue = 0;
    }
    /** Creates new Fluent */
    public Fluent(int numValues) {
        this.numValues = (short)numValues;
        this.currentValue = 0;
    }
   
      /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
         //try {
                Fluent f = (Fluent)super.clone();
                f.numValues = numValues;
                f.currentValue = currentValue;
                return f;	
            //} catch (CloneNotSupportedException e) {
                // this shouldn't happen because Stack is Cloneable
            //    throw new InternalError();
            //}
    }
   
    /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        super.writeObject(s);
        s.writeShort(currentValue);
        s.writeShort(numValues);
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        super.readObject(s);
        currentValue = s.readShort();
        numValues = s.readShort();
    }
    
    public void setValue(int newValue) {
        currentValue = (short)newValue;
    }
    
    public void setNumValues(int vals) {
        numValues = (short)vals;
    }
    
    public void setUndefined() {
        currentValue = numValues;
    }
    
    public int getValue() {
        return currentValue;
    }
    
    //returns the number of values this fluent can take
    public int getNumValues() {
        return numValues;
    }
    
    public String toString() {
        if (currentValue == numValues)
            return "*"; //don't care symbol
        return 
            String.valueOf(currentValue);
    }
    
    //Set the rule object to a don't care state which will match with anything (e.g. "*")
    public void setWildcard() {
        currentValue = numValues;
    } 
    
    public boolean isWildcard() {
        if (currentValue == numValues)
            return true;
        else
            return false;
    }
    
    public void setByValue(int value) {
        setValue(value);
    }    
    
    public boolean equals(RuleObject o) {
        if (currentValue == ((Fluent)o).getValue())
            return true;
        else
            return false;
    }    
    
    public void copy(RuleObject o) {
        currentValue = (short)((Fluent)(o)).getValue();
        numValues = (short)((Fluent)o).getNumValues();
    }
    
    public void convertFromClause(Clause clause){
        ;
    }
    
}
