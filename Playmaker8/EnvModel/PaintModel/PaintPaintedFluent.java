
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
public class PaintPaintedFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NOT_BLOCK_PAINTED = 0, BLOCK_PAINTED = 1;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 2;
    public PaintPaintedFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PaintPaintedFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PaintPaintedFluent f = (PaintPaintedFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isPainted() {
        if (getValue() == BLOCK_PAINTED)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case BLOCK_PAINTED: {
                    return "BP"; 
                }
                case NOT_BLOCK_PAINTED: {
                    return "NOT_BP";
                }
            }
        }
        return "ERROR";
    }
    
    public void convertFromClause(Clause clause) {
        if (clause.getPredicate(0).stringIsEqual("Bool(T)"))
            setValue(BLOCK_PAINTED);
        else
            setValue(NOT_BLOCK_PAINTED);
    }
}