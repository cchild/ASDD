
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
public class PaintDryFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NOT_GRIPPER_DRY = 0, GRIPPER_DRY = 1;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 2;
    public PaintDryFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PaintDryFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PaintDryFluent f = (PaintDryFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isGripperDry() {
        if (getValue() == GRIPPER_DRY)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case GRIPPER_DRY: {
                    return "GD"; 
                }
                case NOT_GRIPPER_DRY: {
                    return "NOT_GD";
                }
            }
        }
        return "ERROR";
    }
    
     public void convertFromClause(Clause clause) {
         if (clause.getPredicate(0).toString().equals("Bool(T)"))
            setValue(GRIPPER_DRY);
         else
             setValue(NOT_GRIPPER_DRY);
    }
}