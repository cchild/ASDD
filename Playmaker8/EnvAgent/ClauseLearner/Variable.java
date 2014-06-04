/*
 * Variable.java
 *
 * Created on July 18, 2002, 5:44 PM
 */

package EnvAgent.ClauseLearner;

import java.io.*;
import java.util.*;

/**
 *
 * @author  eu779
 * @version 
 */
public class Variable extends Term implements Cloneable, Serializable {

    private short type;
    private short currentValue;
    //private String theString;
    
    static ArrayList allTypes = new ArrayList();
    static ArrayList maxValues = new ArrayList();
    
    static final String DIR = "Dir";
    static final String OBJ = "Obj";
    static final String THE_ACT = "TheAct";
    /**/    static final String KILL_ACT = "KillAct";
    static final String PAINT_ACT = "PaintAct";
    static final String THE_BOOL = "Bool";
    static final String REW = "Rew";
    
    
    public Variable() {
        currentValue = 0;
        this.setType("Undef");
        //theString = null;
    }
   
      /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        Variable f = (Variable)super.clone();
        f.currentValue = currentValue;
        f.type = type;
        //f.theString = theString;
        return f;				// return the clone
    }
   
    /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeShort(currentValue);
        s.writeShort(type);
        //s.writeObject(theString);
        
        if (allTypes.size() != 0) {
            ArrayList storeTypes = new ArrayList(); 
            ArrayList storeValues = new ArrayList();
            
            for (int i = 0; i < allTypes.size(); i++) {
                storeTypes.add(allTypes.get(i));
                storeValues.add(maxValues.get(i));
            }
            
            s.writeObject(storeTypes);
            s.writeObject(storeValues);
            
            allTypes.clear();
            maxValues.clear();
            System.out.print("Got to write object");
            System.out.flush();
        }
        
        //System.out.print("WARNING: How do we serialize statics");
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        s.defaultReadObject();
        currentValue = s.readShort();
        type = s.readShort();
        //theString = (String)s.readObject();
        
        if (allTypes.size() == 0) {
            ArrayList storeTypes = null;
            ArrayList storeValues = null;
            
            storeTypes = (ArrayList)s.readObject();
            storeValues = (ArrayList)s.readObject();
            
            for (int i = 0; i < allTypes.size(); i++) {
                allTypes.add(storeTypes.get(i));
                maxValues.add(storeValues.get(i));
            }
        }
    }
    
    public void setValue(int newValue) {
        currentValue = (short)(newValue + 1);
        //theString = null;

        if (allTypes == null)
            allTypes = new ArrayList();
        
        if (maxValues == null)
            maxValues = new ArrayList();
        
        String theType = getType();
        for (int i = 0; i < allTypes.size(); i++) {
            if (theType.equals((String)allTypes.get(i))) {
                if (newValue > ((Integer)maxValues.get(i)).intValue()) {
                    maxValues.set(i, new Integer(newValue));
                }
            }
        }
     
    }
    
    public void setType(String type) {     
        //theString = null;
        if (allTypes == null)
            allTypes = new ArrayList();
         if (maxValues == null)
            maxValues = new ArrayList();
        
        for (short i = 0; i < (short)allTypes.size(); i++) {
            if (type.equals((String)allTypes.get(i))) {
                this.type = i;
                return;
            }
        }
        
        this.type = (short)allTypes.size();
        allTypes.add(new String(type));
        maxValues.add(new Integer(0));
    }
    
    public String getType() {
        return (String)allTypes.get(this.type);
    }
    
    public void setUndefined() {
        currentValue = 0;
        //theString = null;
    }
    

    
    public int getValue() {
        return currentValue - 1;
    }
    
    //returns the number of values this Variable can take
    public int getNumValues() {
        
        if (allTypes == null)
            allTypes = new ArrayList();
         if (maxValues == null)
            maxValues = new ArrayList();
        
     
        for (int i = 0; i < allTypes.size(); i++) {
            if (getType().equals((String)allTypes.get(i)))
                return ((Integer)maxValues.get(i)).intValue()+1;
        }
        return 0;
    }


    
    public String toString() {
        //if (theString != null)
        //    return theString;
        String theString = null;
        
        if (!isGround())
            theString = getType() + "UI"; //uninstanciated
        else {
            String outString = new String() + getType() + "(";
            if (getType().equals(DIR)) {
                switch (getValue()) {
                    case 0: outString += "N)"; break;
                    case 1: outString += "E)"; break;
                    case 2: outString += "S)"; break;
                    case 3: outString += "W)"; break;
                    case 4: outString += "U)"; break;
                    default:
                        outString += "Error";
                }
            } else if (getType().equals(OBJ)) {
                  switch (getValue()) {
                    case 0: outString += "E)"; break;
                    case 1: outString += "A)"; break;
                    case 2: outString += "W)"; break;
                    default:
                        outString += "Error";
                }
            } else if (getType().equals(THE_ACT)) {
                 switch (getValue()) {
                    case 6: outString += "N)"; break;
                    case 7: outString += "E)"; break;
                    case 8: outString += "S)"; break;
                    case 9: outString += "W)"; break;
                    default:
                        outString += "Error";
                }
            } else if (getType().equals(KILL_ACT)) {
                 switch (getValue()) {
                     //This is the order in terms of values. Not quite sure how this actually works.
                     case 0: outString += "N)"; break;
                     case 1: outString += "E)"; break;
                     case 2: outString += "S)"; break;
                     case 3: outString += "W)"; break;
                     case 4: outString += "K)"; break;
                    default: {
                        int value = getValue();
                        outString += "Error";
                    }
                }
            } else if (getType().equals(PAINT_ACT)) {
                 switch (getValue()) {
                    //NOOP =0, DRY =1, NEW = 2, PAINT = 3, PICKUP = 4, WILDCARD = 5;
                    case 1: outString += "DRY)"; break;
                    case 2: outString += "NEW)"; break;
                    case 3: outString += "PAINT)"; break;
                    case 4: outString += "PICKUP)"; break;
                    default:
                        outString += "Error";
                }
            } else if (getType().equals(THE_BOOL)) {
                 switch (getValue()) {
                    case 0: outString += "F)"; break;
                    case 1: outString += "T)"; break;
                    default:
                        outString += "Error";
                }
            } else if (getType().equals(REW)) {
                 switch (getValue()) {
                    case 0: outString += "None)"; break;
                    case 1: outString += "Pos)"; break;
                    case 2: outString += "Neg)"; break;
                    default:
                        outString += "Error";
                }
            } else {
                theString = "error";
            }
            theString = outString;
        }
        
        return theString;
    }
        
      
    
    //Set the rule object to a don't care state which will match with anything (e.g. "*")
    public void setWildcard() {
        currentValue = 0;
        hashCode = 0;
        //theString = null;
    } 
    
    public boolean isWildcard() {
        if (currentValue == 0)
            return true;
        else
            return false;
    }
    
    public boolean equals(Term o) {
        if (getValue() == ((Variable)o).getValue())
            return true;
        else
            return false;
    }    
    
    public boolean isGround() {
        if (currentValue == 0)
            return false;
        return true;
    }
    
    @Override
    public boolean isVariable() {
        return true;
    }  
}
