/*
 * ruleSetList.java
 *
 * Created on 20 November 2002, 18:34
 */

package EnvAgent.RuleLearner;

import EnvModel.*;

import java.util.*;
import java.io.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class RuleSetMap implements Serializable {

    private ArrayList ruleSetList;
    /** Creates new ruleSetList */
    public RuleSetMap() {
        ruleSetList = new ArrayList();
    }
    
    public ArrayList getArrayList() {
        return ruleSetList;
    }
    
    public RuleSet get(int which) {
        return (RuleSet)ruleSetList.get(which);
    }
    
    public void set(int which, RuleSet val) {
        ruleSetList.set(which, val);
    }
    
    public int size() {
        return ruleSetList.size();
    }
    
    public void remove(RuleSet node) {
        ruleSetList.remove(node);
    }
    
    public void remove(int which) {
        ruleSetList.remove(which);
    }
    
    public void add(RuleSet node) {
        if (!contains(node))
            ruleSetList.add(node);
    }
    
    public void addAt(int pos, RuleSet node) {
        ruleSetList.add(pos, node);
    }
    
    public void addAll(RuleSetMap otherList) {
        ruleSetList.addAll(otherList.getArrayList());
    }
    
   
    /*Call standard write object on the ruleSetList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(ruleSetList);
    }
  
    /*Call standard read object on the ruleSetList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        ruleSetList = (ArrayList)s.readObject();
    }
  
    public boolean contains(RuleSet set) {
        for (int i = 0; i  < size(); i++) {
            if (get(i) == set)
                return true;
        }
        return false;
    }
}
