/*
 * StateUtility.java
 *
 * Created on 27 February 2003, 18:36
 */

package ReinforcementLearner;

import EnvModel.*;

import java.io.*;
import java.text.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class StateValue extends Object implements Serializable {

    private Percep percep;
    private double value;
    private double reward;
    
    private static final boolean predator = true;
    
    private static final double REFINE_CONSTANT = 0.9f;
    private static final double DEFAULT_REWARD = 0.0f; 
    private static final double DEFAULT_VALUE = 0.0f;
    
    
    /** Creates new StateUtility */
    public StateValue() {
        percep = null;
        value = DEFAULT_VALUE;
        reward = DEFAULT_REWARD;
    }
    
    public StateValue(Percep percep) {
        this.percep = (Percep)percep.clone();
        String string = percep.getString();
        if (string.substring(1, 6).equals("ERROR")) {
            boolean stop = true;
        }
        //System.out.print("\n" + string.substring(1,5));
        //System.out.print("\n" + string);
        value = DEFAULT_VALUE;
        setReward();
    
    }
    
        /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(percep);
        s.writeDouble(value);
        s.writeDouble(reward);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        percep = (Percep)s.readObject();
        value = s.readDouble();
        reward = s.readDouble();
    }
    
    
    private void setReward() {
        reward =  percep.reward();
    }
    
    public StateValue(Percep percep, double utility, double reward) {
        this.percep = (Percep)percep.clone();
        this.value = value;
        setReward();
    }
    
    public Percep getPercep() {
        return percep;
    }
    
    public Percep getState() {
        return percep;
    }
    
    public double getValue () {
        return value;
    }
    
    public void setValue(double value) {
        this.value= value;
    }
    
    public void setReward(double reward) {
        this.reward = reward;
    }
    
    public double getReward() {
        return reward;
    }
    
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.#####");

        return ("\n StateValue: " + percep.getString() + " value: " + df.format(value) + " reward: "+ reward);
    }
}
