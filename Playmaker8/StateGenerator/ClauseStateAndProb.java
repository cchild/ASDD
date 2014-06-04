/*
 * StateAndProb.java
 *
 * Created on 28 February 2003, 17:53
 */

package StateGenerator;

import EnvModel.*;
import EnvAgent.ClauseLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClauseStateAndProb {

    private ClauseElements percep;
    private double probability;
    
    /** Creates new StateAndProb */
    public ClauseStateAndProb() {
    }
    
    public ClauseStateAndProb(ClauseElements percep, float prob) {
        this.percep = (ClauseElements)percep.clone();
        probability = prob;
    }
    
    public void setPercep(ClauseElements percep) {
        this.percep = percep;
    }
    
    public void setProbability(double probability) {
        this.probability = probability;
    }
    
    public double getProbability() {
        return probability;
    }
    
    public ClauseElements getPercep() {
        return percep;
    }

}
