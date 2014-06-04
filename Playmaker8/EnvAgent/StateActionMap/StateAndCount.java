package EnvAgent.StateActionMap;

import EnvModel.*;
import EnvAgent.RuleLearner.*;

import java.util.*;
import java.io.*;

//a single node describes a rule, or dependency relationship in the data
public class StateAndCount extends Object implements Serializable {
    Percep state;
    int count;
    
    /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(state);
        s.writeInt(count);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        state = (Percep)s.readObject();
        count = s.readInt();
    }
    
    
    public StateAndCount(Percep state) {
        this.state = (Percep)state.clone();
        count = 0;
    }
    
    public StateAndCount(Percep state, int count) {
        this.state = (Percep)state.clone();
        this.count = count;
    }
    
    public void increment() {
        count ++;
    }
    
    public int getCount() {
        return count;
    }
    
    public Percep getPercep() {
        return state;
    }
    
    public boolean isEqualTo(Percep state) {
        if (this.state.isEqual(state))
            return true;
        else
            return false;
    }
}
    
 