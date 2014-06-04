
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
public class KillPredatorFluent extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	EMPTY = 0, AGENT_BODY = 1, WALL = 2;
    /** Creates new Fluent */
    
    static final String OBJ_A = "Obj(A)";
    static final String OBJ_W = "Obj(W)";
    static final String OBJ_E = "Obj(E)";
    static final String REW_POS = "Rew(Pos)";
    static final String REW_NEG = "Rew(Neg)";
    static final String REW_NONE = "Rew(None)";
    
    
    
    private static final int NUM_VALUES = 3;
    public KillPredatorFluent() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public KillPredatorFluent(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        KillPredatorFluent f = (KillPredatorFluent)super.clone();
        return f;				// return the clone
    }
    
    public boolean isAgentBody() {
        if (getValue() == AGENT_BODY)
            return true;
        return false;
    }
    
    public boolean isWall() {
        if (getValue() == WALL)
            return true;
        return false;
    }
    
    public String toString() {
        
        if (getValue() == getNumValues())
            return "*"; //don't care symbol
        else {
            switch (getValue()){
                case EMPTY: {
                    return "EMPTY"; 
                }
                case AGENT_BODY: {
                    return "AGENT_BODY";
                }
                case WALL: {
                    return "WALL";
                }
            }
        }
        return "ERROR";
    }
    
    @Override
    public void convertFromClause(Clause clause) {
        
        String str;
        if (clause.getNoPredicates() == 2) {
            Term term = clause.getPredicate(1);
            if (term.stringIsEqual(OBJ_A))
                setValue(KillPredatorFluent.AGENT_BODY);
            else if (term.stringIsEqual(OBJ_E))
                setValue(KillPredatorFluent.EMPTY);
            else if (term.stringIsEqual(OBJ_W))
                setValue(KillPredatorFluent.WALL);
            else 
                System.out.print("Error in convert to Predator Fluent");
        }
        else {
            Term term = clause.getPredicate(0);
            if (term.stringIsEqual(REW_POS))
                setValue(KillRewardFluent.POSITIVE_REWARD);
            else if (term.stringIsEqual(REW_NEG))
                setValue(KillRewardFluent.NEGATIVE_REWARD);
            else if (term.stringIsEqual(REW_NONE))
                setValue(KillRewardFluent.NO_REWARD);
            else 
                System.out.print("Error in convert to Predator Fluent");
        }
    }
    
}
