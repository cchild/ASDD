/*
 * FluentValuesAndProb.java
 *
 * Created on 28 February 2003, 17:36
 */

package StateGenerator;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class FluentValuesAndProb {

    int numValues;
    int values[];
    double probabilities[];
    /** Creates new FluentValuesAndProb */
    public FluentValuesAndProb(int numValues) {
        this.numValues = numValues;
        values = new int[numValues];
        probabilities = new double[numValues];
    }
    
    public int getNumValues() {
        return numValues;
    }
    
    public double getProbability(int which) {
        return probabilities[which];
    }
    
    public int getValue(int which) {
        return values[which];
    }
    
    public void setProbability(int which, double prob) {
        probabilities[which] = prob;
    }
    
    public void setValue(int which, int value) {
        values[which] = value;
    }
}
