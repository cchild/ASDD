/*
 * StateAndProb.java
 *
 * Created on 28 February 2003, 17:53
 */

package StateGenerator;

import EnvModel.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class StateAndProb {

    private Percep percep;
    private double probability;
    
    /** Creates new StateAndProb */
    public StateAndProb() {
    }
    
    public StateAndProb(Percep percep, float prob) {
        this.percep = (Percep)percep.clone();
        probability = prob;
    }
    
    public void setPercep(Percep percep) {
        this.percep = percep;
    }
    
    public void setProbability(double probability) {
        this.probability = probability;
    }
    
    public double getProbability() {
        return probability;
    }
    
    public Percep getPercep() {
        return percep;
    }

}
