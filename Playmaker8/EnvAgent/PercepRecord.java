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
 * @author  Chris Child
 * @version 
 */
public class PercepRecord extends Object implements Serializable {

    ArrayList perceps;
    
    /** Creates new PerceptRecord */
    public PercepRecord() {
        perceps = new ArrayList();
    }
    
     /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeObject(perceps);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  { 
        s.defaultReadObject();
        perceps = (ArrayList)s.readObject();
    }
    
    /*Percep record add must make a copy rather than straight add*/
    public void addPercep(Percep percep) {
         
        Percep p = (Percep)percep.clone();
        perceps.add(p);
        //System.out.print(p.getString());
        /*for (int i = 0; i < perceps.size(); i ++) {
            System.out.print("\n");
            System.out.print(getPercepString(i));
           
        }*/
        p = null;
    }
    
    public Percep getPercep(int index) {
        return (Percep)perceps.get(index);
    }
    
    public String getPercepString(int index) {
        Percep percep = getPercep(index);
        return percep.getString();
    }
        
    public int size() {
        return perceps.size();
    }

}
