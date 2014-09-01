package EnvModel;

import EnvAgent.ClauseLearner.*;
import EnvAgent.PredatorAgent.PredatorAgent;
import V_Sensors.Sensor;
import java.io.*;
import java.util.*;

/*
 * Percep.java
 *
 * Created on March 6, 2001, 5:38 PM
 */

/**
 *
 * @author  Chris Child
 * This object stores the contents of perception which the agent
 * body creates from the current state of the environment. 
 * Implements cloneable so that a list of perceptions can be stored
 * by the agent. Also has a getString function which allows the
 * perep to be output to a text file.
 */

/*THIS SHOULD NOT BE ABSTRACT. THEN WE COULD HAVE PERCEPS GENERATED FRWITHOUT KNOWING THE
 *ACTUAL TYPE OF AGENT*/

public abstract class Percep extends Object implements Cloneable, Serializable {

    /*Store the five perceptions*/
    protected ArrayList perceptions;
    
    /** Creates new Percep */
    public Percep() {
    }
    
        /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
    }
    
   public Object clone()  {
        try {
           	Percep p = (Percep)super.clone();
                return p;
            } catch (CloneNotSupportedException e) {
                 // this shouldn't happen because Stack is Cloneable
                throw new InternalError();
            }
   
     
   }
   
    /*set an individual element of the perceptual array*/
    public void setPercep(int which, int type) {
        Fluent fluent = (Fluent)perceptions.get(which);
        fluent.setValue(type);
    }
    
    /*at an individual element of the perceptual array*/
    public int getPercep(int which) {
        return getFluent(which).getValue();
    }
    
    public Fluent getFluent(int which) {
        return (Fluent)perceptions.get(which);
    }

    /*Return a string of tokens representiing the contents of the perception*/
    
    
    public abstract String translation();
    
    
    /*return the number of fluents this percep contains*/
    public abstract int getNumFluents();
    
    
    /*returns true if both percepts are the same*/
     public boolean isEqual(Percep percep) {
        if (this.getClass() != percep.getClass())
            return false;
        if (this.getNumFluents() != percep.getNumFluents())
            return false;
    
        for (int i = 0; i < getNumFluents(); i++) {
            if (getFluent(i).getValue() != percep.getFluent(i).getValue())
                return false;
        }
        
        return true;
    }

    public abstract ClauseElements convertToClauseElements(boolean precursor);
    
    public abstract boolean legalState();
    
    abstract public double reward();
    
    abstract public Percep convertFromClauseElements(ClauseElements clauseElements);
    
    
    public abstract void readFromString(String str);
    
    
    public abstract int readFile(PredatorAgent pred);
    
    //public abstract Percep percepFromSensor(Sensor s);
}
