
/*
 * Fluent.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvModel.KillPredatorModel;
import EnvModel.*;
import EnvAgent.ClauseLearner.*;

import java.io.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class KillRewardFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	NO_REWARD = 0, POSITIVE_REWARD = 1, NEGATIVE_REWARD = 2;
    /** Creates new Fluent */
    
    static final String REW_NONE = "Rew(None)";
    static final String REW_POS = "Rew(Pos)";
    static final String REW_NEG = "Rew(Neg)";
    
    private static final int NUM_VALUES = 3;
    public KillRewardFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public KillRewardFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        KillRewardFluent f = (KillRewardFluent)super.clone();
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
        String str = clause.getPredicate(0).toString();
         if (str.equals(REW_POS))
            setValue(POSITIVE_REWARD);
         else if (str.equals(REW_NEG))
            setValue(NEGATIVE_REWARD);
         else if (str.equals(REW_NONE))
             setValue(NO_REWARD);
         else
             setValue(NUM_VALUES);
    }
}