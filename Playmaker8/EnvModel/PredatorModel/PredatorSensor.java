
/*
 * Sensor.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvModel.PredatorModel;
import EnvModel.*;
import EnvAgent.ClauseLearner.*;

import java.io.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class PredatorSensor extends Fluent implements Cloneable, Serializable {

   /*possible things which can be precieved*/
    public static final int
	EMPTY = 0, AGENT_BODY = 1, WALL = 2;
    /** Creates new Fluent */
    
    private static final int NUM_VALUES = 3;
    public PredatorSensor() {
        setNumValues(NUM_VALUES);
        setValue(0);
    }
    
    public PredatorSensor(int val) {
        setNumValues(NUM_VALUES);
        setValue(val);
    }
    
    //No need to implement read object and write object. Handled by super class
       
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        PredatorSensor f = (PredatorSensor)super.clone();
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
                    return "E"; 
                }
                case AGENT_BODY: {
                    return "A";
                }
                case WALL: {
                    return "W";
                }
            }
        }
        return "ERROR";
    }
    
    public void convertFromClause(Clause clause) {
        Term term = clause.getPredicate(1);
        if (term.stringIsEqual("Obj(A)"))
            setValue(PredatorSensor.AGENT_BODY);
        else if (term.stringIsEqual("Obj(E)"))
            setValue(PredatorSensor.EMPTY);
        else if (term.stringIsEqual("Obj(W)"))
            setValue(PredatorSensor.WALL);
        else
            System.out.print("Error in convert to Predator Fluent");
    }
    
}
