/*
 * PerceptRecord.java
 *
 * Created on June 13, 2002, 3:35 PM
 */

package EnvAgent;

import java.util.*;
import java.io.*;

import EnvModel.*;
import EnvAgent.ClauseLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClausePercepRecord extends Object implements Serializable {

    ArrayList clausePerceps;
    
    /** Creates new PerceptRecord */
    public ClausePercepRecord() {
        clausePerceps = new ArrayList();
    }
    
     /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeObject(clausePerceps);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  { 
        s.defaultReadObject();
        clausePerceps = (ArrayList)s.readObject();
    }
    
     /*Percep record add must make a copy rather than straight add*/
    public void addPercep(ClauseElements percep) {
        ClauseElements p = (ClauseElements)percep.clone();
        clausePerceps.add(p);
        //System.out.print(p.getString());
        /*for (int i = 0; i < perceps.size(); i ++) {
            System.out.print("\n");
            System.out.print(getPercepString(i));
           
        }*/
        p = null;
    }
    
    public ClauseElements getPercep(int index) {
        return (ClauseElements)clausePerceps.get(index);
    }
    
    public String getPercepString(int index) {
        ClauseElements percep = getPercep(index);
        return percep.toString();
    }
        
    public int size() {
        return clausePerceps.size();
    }
    
    public String toString() {
        String s = new String();
        for (int i = 0; i < size(); i ++){
            s += "\n" + getPercepString(i);
        }
        return s;
    }
   
}
