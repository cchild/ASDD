package EnvAgent.StateActionMap;

import EnvModel.*;
import EnvAgent.RuleLearner.*;

import java.util.*;
import java.io.*;

//a single node describes a rule, or dependency relationship in the data
public class StateActions extends Object implements Serializable {
    Percep state;
    ArrayList actionStates;
    
    public StateActions(Percep state) {
        this.state = (Percep)state.clone();
        actionStates = new ArrayList();
    }
    
      /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(state);
        s.writeObject(actionStates);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        state = (Percep)s.readObject();
        actionStates = (ArrayList)s.readObject();
    }
    
    
    
    
    public Percep getState() {
        return state;
    }
    
    public int getNumActions() {
        return actionStates.size();
    }
    
    public ActionState getActionState(int which) {
        return (ActionState)actionStates.get(which);
    }
    
    public Action getAction(int which) {
        return getActionState(which).getAction();
    }
    
    public void addActionState(Action action, Percep state) {
        boolean found = false;
        for (int i = 0; i < actionStates.size(); i++) {
            if (getActionState(i).actionequals(action)) {
                getActionState(i).addState(state);
                found = true;
            }
        }
        
        if (!found) {
            actionStates.add(new ActionState(action, state));
        }
    }
    
    public ArrayList getStatesAndCounts(Action action) {
        for (int i = 0; i < actionStates.size(); i++) {
            if (getActionState(i).actionequals(action)) {
                return getActionState(i).getStatesAndCounts();
            }
        }
        
        return null;
    }
}
    
 