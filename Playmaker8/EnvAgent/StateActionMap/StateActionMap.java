package EnvAgent.StateActionMap;

import EnvModel.*;
import EnvAgent.RuleLearner.*;
import EnvAgent.*;

import java.util.*;
import java.io.*;

//a single node describes a rule, or dependency relationship in the data
public class StateActionMap extends ArrayList implements Serializable {
   
    public StateActionMap() {
        ;
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
    
    
    public void addStateActionNextState(Percep previousState, Action action, Percep nextState) {
        boolean found = false;
        for (int i = 0; i < size(); i ++) {
            if (getStateActions(i).getState().isEqual(previousState)) {
                getStateActions(i).addActionState(action, nextState);
                 found = true;
                 break;
            }
        }
        
        if (!found) {
            StateActions stateActions = new StateActions(previousState);
            add(stateActions);
            stateActions.addActionState(action, nextState);
        }
    }
    
    public StateActions getStateActions(int which) {
        return (StateActions)get(which);
    }
  
    public ArrayList getStatesAndCounts(Percep state, Action action) { 
        for (int i = 0; i < size(); i ++) {
            if (getStateActions(i).getState().isEqual(state))
                return getStateActions(i).getStatesAndCounts(action);
        }
        
        System.out.print("ERROR: states not found in getStates and Counts (StateActionMap)\n");
        
        return null;
    }
    
    public void generateFromPercepActionList(PercepRecord percepRecord, ActionRecord actionRecord) {
        for (int i = 0; i < percepRecord.size()-1 ; i++) {
            addStateActionNextState(percepRecord.getPercep(i), actionRecord.getAction(i), percepRecord.getPercep(i+1));
        }
    }
    
    public Percep pickRandomPercep() {
        int randomState =  (int)(Math.random()*(double)size());
        return ((StateActions)get(randomState)).getState();
    }
    
}
    
 