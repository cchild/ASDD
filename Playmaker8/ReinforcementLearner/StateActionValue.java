/*
 * StateUtility.java
 *
 * Created on 27 February 2003, 18:36
 */

package ReinforcementLearner;

import EnvModel.*;
import EnvModel.PredatorModel.*;

import java.io.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class StateActionValue extends Object implements Serializable {

    private Percep percep;
    private Action action;
    private double value;
    private double reward;
    
    private static final boolean predator = true;
    
    private static final double REFINE_CONSTANT = 0.9f;
    private static final double DEFAULT_REWARD = 0.0f; 
    private static final double DEFAULT_VALUE = 0.0f;
    
    
    /** Creates new StateUtility */
    public StateActionValue() {
        percep = null;
        action = null;
        value = DEFAULT_VALUE;
        reward = DEFAULT_REWARD;
    }
    
    // Son added this
    public StateActionValue(Percep percep, Action action, double value) {
        this.percep = (Percep)percep.clone();
        this.action = (Action)action.clone();
        this.value = value;
        setReward();
    }
    
    public StateActionValue(Percep percep, Action action, double utility, double reward) {
        this.percep = (Percep)percep.clone();
        this.action = (Action)action.clone();
        this.value = value;
        setReward();
    }
    
    
    public StateActionValue(Percep percep, Action action) {
        this.percep = (Percep)percep.clone();
        this.action = (Action)action.clone();
        String string = percep.toString();
        if (string.substring(1, 6).equals("ERROR")) {
            boolean stop = true;
        }
        
        if (this.action.toString().equals("[NOOP NOTSET]")) {
            boolean stop2 = true;
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
        s.writeObject(action);
        s.writeDouble(value);
        s.writeDouble(reward);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        percep = (Percep)s.readObject();
        action = (Action)s.readObject();
        value = s.readDouble();
        reward = s.readDouble();
    }
    
    
    private void setReward() {
        reward = percep.reward();
        /*if (predator) {
            if (((PredatorAgentPercep)percep).isOntopOfAgent())
                reward = 1.0f;
            else
                reward = -0.1f;
        }
        else {
           if (((PredatorAgentPercep)percep).isOntopOfAgent())
                reward = -0.1f;
            else
                reward = 1.0f;
        }*/
    }
    
  
    public Percep getPercep() {
        return percep;
    }
    
    public Action getAction() {
        return action;
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
        return ("\n StateActionValue: " + ((Percep)percep).toString() + " " + ((Action)action).toString() + " value: " + value + " reward: "+ reward);
    }
}
