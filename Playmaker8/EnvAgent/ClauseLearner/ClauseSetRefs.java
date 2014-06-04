/*
 * ClauseSetRefs.java
 *
 * Created on 11 October 2005, 18:46
 */

package EnvAgent.ClauseLearner;

import java.util.*;
import java.io.*;

/**
 *
 * @author Chris Child
 */
public class ClauseSetRefs extends Object implements Cloneable, Serializable {
    
    ArrayList refList;
    /** Creates a new instance of ClauseSetRefs */
    public ClauseSetRefs() {
        refList = new ArrayList();
    }
    
    public void addRef(int ref) {
        refList.add(new Integer(ref));
    }
    
    public boolean contains(int ref) {
        for (int i = 0; i < refList.size(); i++) {
            if (((Integer)refList.get(i)).intValue() == ref)
                return true;
        }
        
        return false;
    }
    
    public int size() {
        return refList.size();
    }
    
    public int get(int i) {
        return ((Integer)refList.get(i)).intValue();
    }
    
}
