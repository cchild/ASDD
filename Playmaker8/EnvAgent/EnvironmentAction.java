package EnvAgent;

import EnvModel.*;

/*
 * EnvironmentAction.java
 *
 * Created on 26 February 2001, 15:21
 */

import java.util.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public abstract class EnvironmentAction extends Action {
    
    /** Creates new Influence */
    public EnvironmentAction() {
    }
    
    public void act(float time, State state)
    {
        //if... preconditions hold on environment objects
        //then... have some effect on these objects and agents
        //note that we cannot effect agents state
    }
    
    public String toString() {
        return "";
    }
    
}
