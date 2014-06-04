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
public class StateActionValueMap extends Object implements Serializable {

    ArrayList stateActionValues;
    /** Creates new StateValueMap */
    public StateActionValueMap() {
        stateActionValues = new ArrayList();
    }
    
    /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(stateActionValues);
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

            StateActionValueMap stateActionValueMap = (StateActionValueMap)q.readObject();

            this.stateActionValues = stateActionValueMap.getStateActionValues();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        } 
    }
    
    
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        stateActionValues = (ArrayList)s.readObject();
    }
    
    
    public void addStateAction(Percep percep, Action action) {
        if (!exists(percep, action)) {
            stateActionValues.add(new StateActionValue(percep, action));
        }
    }
    
    public void addPercepAction(Percep percep, Action action) {
        addStateAction(percep, action);
    }
    
  
    public boolean exists(Percep percep, Action action) {
        for (int i = 0; i < stateActionValues.size(); i++) {
            if (percep.isEqual(((StateActionValue)stateActionValues.get(i)).getState()))
                if (action.isEqual(((StateActionValue)stateActionValues.get(i)).getAction()))
                    return true;
        }
        return false;
    }
    
    public String toString() {
        String output = new String();
        for (int i = 0; i < stateActionValues.size(); i++) {
            output += ((StateActionValue)stateActionValues.get(i)).toString();   
        }
        return output;
    }
    
    public void output() {
        for (int i = 0; i < stateActionValues.size(); i++) {
            System.out.print(toString());   
        }
        System.out.flush();
    }
    
    public StateActionValue getOrCreateStateActionValue(Percep percep, Action action) {
       for (int i = 0; i < stateActionValues.size(); i++) {
            if (((StateActionValue)stateActionValues.get(i)).getPercep().isEqual(percep))
               if (((StateActionValue)stateActionValues.get(i)).getAction().isEqual(action))
                   return (StateActionValue)stateActionValues.get(i);
        }
        
       addStateAction(percep, action);
       return getStateActionValue(percep, action);
    }
    
    public ArrayList getStateActionValues() {
        return stateActionValues;
    }
    
    public StateActionValue getStateActionValue(Percep percep, Action action){
        for (int i = 0; i < stateActionValues.size(); i++) {
            if (((StateActionValue)stateActionValues.get(i)).getPercep().isEqual(percep)) {
               if (((StateActionValue)stateActionValues.get(i)).getAction().isEqual(action))
                return (StateActionValue)stateActionValues.get(i);
            }
        }
        
        System.out.print("ERROR retrieveing state value");
        return null;
    }
    
    public void setStateActionValue(Percep percep, Action action, double value) {
       StateActionValue stateActionValue = getOrCreateStateActionValue(percep, action);
       stateActionValue.setValue(value);
    }
    
    //Picks a random state which has already been visisted or gernerated
    public Percep pickRandomState() {
        if (stateActionValues.size() == 0)
            return null;
        
        int randomState = (int)(Math.random() * (double)stateActionValues.size());
        if (randomState == stateActionValues.size())
            randomState = stateActionValues.size() -1;
        StateActionValue stateActionValue = (StateActionValue)stateActionValues.get(randomState);
        return stateActionValue.getState();
    }
    
    public Action pickRandomAction(Percep state) {
        if (stateActionValues.size() == 0)
            return null;
        ArrayList actions = new ArrayList();
        
        for (int i = 0; i < stateActionValues.size(); i++) {
            if (((StateActionValue)stateActionValues.get(i)).getPercep().isEqual(state)) {
               actions.add(((StateActionValue)stateActionValues.get(i)).getAction());
            }
        }
        
        if (actions.size() == 0)
            return null;
        
        int randomAction = (int)(Math.random() * (double)actions.size());
        if (randomAction == actions.size())
            randomAction = actions.size() -1;
        Action action = (Action)actions.get(randomAction);
        return action;
    }
    
    public final Action getBestAction(Percep state) {
         if (stateActionValues.size() == 0)
            return null;
        Action maxAction = null;
        double maxActionValue = -1000000.0f;
        
        boolean foundOne = false;
        
       
        
        for (int i = 0; i < stateActionValues.size(); i++) {
            if (((StateActionValue)stateActionValues.get(i)).getPercep().isEqual(state)) {
               if  (((StateActionValue)stateActionValues.get(i)).getValue() > maxActionValue) {
                   foundOne = true;
                    StateActionValue stav = (StateActionValue)stateActionValues.get(i);
                    Action posAction = stav.getAction();
                    if (!posAction.toString().equals("[NOOP NOTSET]")) {
                        maxActionValue = stav.getValue();
                        maxAction = posAction;
                    } else {
                        LogFile logfile = new LogFile();
                        logfile.print("\n The State action values when it goes wrong are:\n");
                        for (int j = 0; j < stateActionValues.size(); j++) {
                            logfile.print("\n" + j + " "+ this.stateActionValues.get(j).toString());
                        }
                        logfile.flush();
                        logfile.close();
                        stav = (StateActionValue)stateActionValues.get(i);
                        posAction = stav.getAction();
                    }
               }
            }
        }
        
        if (!foundOne) {
            boolean stop = true;
        }
        
        if (maxAction != null) {
            maxAction = maxAction;
            return maxAction;
        }
        else
            return null;
    }
}
