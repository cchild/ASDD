
/*
 * Fluent.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvModel.PaintModel;
import EnvModel.*;
import EnvAgent.ClauseLearner.*;

import java.io.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class PaintHoldingFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NOT_HOLDING_BLOCK = 0, HOLDING_BLOCK = 1;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 2;
    public PaintHoldingFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PaintHoldingFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PaintHoldingFluent f = (PaintHoldingFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isHoldingBlock() {
        if (getValue() == HOLDING_BLOCK)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case HOLDING_BLOCK: {
                    //return "HB";
                    return "B";
                }
                case NOT_HOLDING_BLOCK: {
                    //return "NOT_HB";
                    return "b";
                }
            }
        }
        return "ERROR";
    }
    
    public void convertFromClause(Clause clause) {
         if (clause.getPredicate(0).stringIsEqual("Bool(T)"))
            setValue(HOLDING_BLOCK);
         else
             setValue(NOT_HOLDING_BLOCK);
    }
}