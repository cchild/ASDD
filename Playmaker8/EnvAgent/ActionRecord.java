/*
 * PerceptRecord.java
 *
 * Created on June 13, 2002, 3:35 PM
 */

package EnvAgent;

import java.util.*;
import java.io.*;

import EnvModel.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class ActionRecord extends Object implements Serializable {

    ArrayList actions;
    
    /** Creates new PerceptRecord */
    public ActionRecord() {
        actions = new ArrayList();
    }
    
      /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeObject(actions);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  { 
        s.defaultReadObject();
        actions = (ArrayList)s.readObject();
    }
    
    public void addAction(Action action) {
        Action a = (Action)action.clone();
        actions.add(a);
    }
    
    public Action getAction(int index) {
        return (Action)actions.get(index);
    }
    
    public String getActionString(int index) {
        Action action = ((Action)actions.get(index));
        return action.toString();
    }
    
    public int size() {
        return actions.size();
    }
        

}
