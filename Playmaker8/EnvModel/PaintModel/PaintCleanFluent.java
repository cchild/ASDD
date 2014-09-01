
/*
 * Fluent.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvModel.PaintModel;
import EnvModel.*;

import java.io.*;
import EnvAgent.ClauseLearner.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class PaintCleanFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NOT_GRIPPER_CLEAN = 0, GRIPPER_CLEAN = 1;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 2;
    public PaintCleanFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PaintCleanFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PaintCleanFluent f = (PaintCleanFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isGripperClean() {
        if (getValue() == GRIPPER_CLEAN)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case GRIPPER_CLEAN: {
                    //return "GC"; 
                    return "C";
                }
                case NOT_GRIPPER_CLEAN: {
                    //return "NOT_GC";
                    return "c";
                }
            }
        }
        return "ERROR";
    }
    
    public void convertFromClause(Clause clause) {
        if (clause.getPredicate(0).stringIsEqual("Bool(T)"))
            setValue(GRIPPER_CLEAN);
        else
            setValue(NOT_GRIPPER_CLEAN);
    }
}