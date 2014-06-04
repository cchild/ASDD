/*
 * SquareContents.java
 *
 * Created on May 29, 2002, 6:19 PM
 */

package EnvModel;

import java.util.*;

/**
 *
 * @author  Chris Child
 * 
 * Structure for storing the contents of a single square
 * in a grid environment
 */
public class SquareContents extends Object {

    ArrayList contents;
    
    /** Creates new SquareContents */
    public SquareContents(int size) {
        contents = new ArrayList(size);
    }
    
    public EnvironmentObject getEnvironmentObject(int i) {
        return (EnvironmentObject)contents.get(i);
    }
    
    public void addEnvironmentObject(EnvironmentObject object) {
        contents.add(object);
    }
    
    public void removeEnvironmentObject(EnvironmentObject object) {
        contents.remove(object);
    }
    
    public int size() {
        return contents.size();
    }

}
