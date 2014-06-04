
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
public class PaintRewardFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NO_REWARD = 0, POSITIVE_REWARD = 1, NEGATIVE_REWARD = 2;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 3;
    public PaintRewardFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PaintRewardFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PaintRewardFluent f = (PaintRewardFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isPositive() {
        if (getValue() == POSITIVE_REWARD)
            return true;
        return false;
    }
    
    public boolean isNegative() {
        if (getValue() == NEGATIVE_REWARD)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case NO_REWARD: {
                    return "NO_RE"; 
                }
                case POSITIVE_REWARD: {
                    return "PO_RE";
                }
                case NEGATIVE_REWARD: {
                    return "NE_RE";
                }
            }
        }
        return "ERROR";
    }
    
    public void convertFromClause(Clause clause) {
         if (clause.getPredicate(0).stringIsEqual("Rew(Pos)"))
            setValue(POSITIVE_REWARD);
         else if (clause.getPredicate(0).stringIsEqual("Rew(Neg)"))
            setValue(NEGATIVE_REWARD);
         else if (clause.getPredicate(0).stringIsEqual("Rew(None)"))
             setValue(NO_REWARD);
         else
             setValue(NUM_VALUES);
    }
}