/*
 * ActionStore.java
 *
 * Created on June 19, 2002, 1:56 PM
 */

package EnvModel;

import java.util.*;

/**
 *
 * @author  Chris Child
 * Class for storing actions. Currently just an array list
 */
public class ActionStore extends Object {

    ArrayList actions;
    /** Creates new ActionStore */
    public ActionStore() {
    }
    
    public void addAction(Action action) {
        actions.add(action);
    }
    
    public Action getAction(int i) {
        return (Action)actions.get(i);
    }
    
    public int size() {
        return actions.size();
    }

}
