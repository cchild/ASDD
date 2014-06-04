/*
 * StateValueMap.java
 *
 * Created on 28 February 2003, 13:01
 */

package ReinforcementLearner;

import java.util.*;
import EnvModel.*;
import java.io.*;

import Logging.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class StateValueMap extends Object implements Serializable {

    ArrayList stateValues;
    /** Creates new StateValueMap */
    public StateValueMap() {
        stateValues = new ArrayList();
    }
    
    /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(stateValues);
    }
    
    public int size() {
        return stateValues.size();
    }
  
    public void writeTo(String fileName) {
        try { 
                /* Create a file to write the serialized tree to. */ 
                FileOutputStream ostream = new FileOutputStream(fileName); 
                /* Create the output stream */ 
                ObjectOutputStream p = new ObjectOutputStream(ostream); 
         
                p.writeObject(this);
                
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
    }
    
    public void readFrom(String fileName) {
        try {   
            /* Open the file and set to read objects from it. */ 
            FileInputStream istream = new FileInputStream(fileName); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            /* Read the learned rules object */

            StateValueMap valueMap = (StateValueMap)q.readObject();

            this.stateValues = valueMap.getStateValues();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        } 
    }
    
    
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        stateValues = (ArrayList)s.readObject();
    }
    
    
    public void addState(Percep percep) {
        if (!exists(percep)) {
            stateValues.add(new StateValue(percep));
        }
    }
    
    public void addPercep(Percep percep) {
        addState(percep);
    }
    
  
    public boolean exists(Percep percep) {
        for (int i = 0; i < stateValues.size(); i++) {
            if (percep.isEqual(((StateValue)stateValues.get(i)).getState()))
                return true;
        }
        return false;
    }
    
    public String toString() {
        String output = new String();
        for (int i = 0; i < stateValues.size(); i++) {
            output += ((StateValue)stateValues.get(i)).toString();   
        }
        return output;
    }
    
    public void output() {
        LogFile logfile = new LogFile(2);
        for (int i = 0; i < stateValues.size(); i++) {
            logfile.print(toString());
        }
        logfile.close();
    }
    
    public StateValue getOrCreateStateValue(Percep percep) {
       for (int i = 0; i < stateValues.size(); i++) {
            if (((StateValue)stateValues.get(i)).getPercep().isEqual(percep)) {
                return (StateValue)stateValues.get(i);
            }
        }
        
       addState(percep);
       return getStateValue(percep);
    }
    
    public ArrayList getStateValues() {
        return stateValues;
    }
    
    public StateValue getStateValue(Percep percep){
        for (int i = 0; i < stateValues.size(); i++) {
            if (((StateValue)stateValues.get(i)).getPercep().isEqual(percep)) {
                return (StateValue)stateValues.get(i);
            }
        }
        
        System.out.print("ERROR retrieveing state value");
        return null;
    }
    
    public void setStateValue(Percep percep, double value) {
       StateValue stateValue = getOrCreateStateValue(percep);
       stateValue.setValue(value);
    }
    
    //Picks a random state which has already been visisted or gernerated
    public Percep pickRandomState() {
        if (stateValues.size() == 0)
            return null;
        
        int randomState = (int)(Math.random() * (double)stateValues.size());
        if (randomState == stateValues.size())
            randomState = stateValues.size() -1;
        StateValue stateValue = (StateValue)stateValues.get(randomState);
        return stateValue.getState();
    }
}
