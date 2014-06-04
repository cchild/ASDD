package EnvAgent.StateActionMap;

import EnvModel.*;
import EnvAgent.RuleLearner.*;

import java.util.*;
import java.io.*;

//a single node describes a rule, or dependency relationship in the data
public class ActionState extends Object implements Serializable {
    Action action;
    ArrayList statesAndCounts;
    
    public ActionState(Action action) {
        this.action = (Action)action.clone();
        statesAndCounts = new ArrayList();
    }
    
    public ActionState(Action action, Percep state) {
        this.action = (Action)action.clone();
        statesAndCounts = new ArrayList();
        
        statesAndCounts.add(new StateAndCount((Percep)state.clone(), 1));
    }
    
      /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(action);
        s.writeObject(statesAndCounts);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        action = (Action)s.readObject();
        statesAndCounts = (ArrayList)s.readObject();
    }
    
    
    public Action getAction() {
        return action;
    }
    
    public boolean actionequals(Action action) {
        return getAction().isEqual(action);
    }
    
    public void addState(Percep state) {
        boolean found = false;
        for (int i = 0; i < statesAndCounts.size(); i++) {
            if (((StateAndCount)statesAndCounts.get(i)).isEqualTo(state)) {
                ((StateAndCount)statesAndCounts.get(i)).increment();
                found = true;
                break;
            }
        }
        
        if (!found) {
            statesAndCounts.add(new StateAndCount(state, 1));
        }
    }
    
    public ArrayList getStatesAndCounts() {
        return statesAndCounts;
    }
}
    
 