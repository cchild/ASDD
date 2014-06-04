package EnvAgent.RuleLearner;

import EnvModel.*;

import java.util.*;
import java.io.*;

public class RuleElements extends Object implements Cloneable, Serializable {
    ArrayList elements;

    int wildcards;
    boolean wildcardsCounted;
    int firstNonWildcardPosition;
    boolean firstNonWildcardPositionEvaluated;
    
    public RuleElements() {
        elements= new ArrayList(0);
        wildcards = 0;
        wildcardsCounted = false;
        firstNonWildcardPosition = -1;
        firstNonWildcardPositionEvaluated = false;
    }

    public RuleElements(int size) {
        elements = new ArrayList(0);
        for (int i = 0; i < size; i++) {
            elements.add(new Object());
        }
        wildcards = 0;
        wildcardsCounted = false;
        firstNonWildcardPosition = size;
        firstNonWildcardPositionEvaluated = true;
    }

        /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(elements);
        s.writeInt(wildcards);
        s.writeBoolean(wildcardsCounted);
        s.writeInt(firstNonWildcardPosition);
        s.writeBoolean(firstNonWildcardPositionEvaluated);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        elements = (ArrayList)s.readObject();
        wildcards = s.readInt();
        wildcardsCounted = s.readBoolean();
        firstNonWildcardPosition = s.readInt();
        firstNonWildcardPositionEvaluated = s.readBoolean();
    }
    
    public int size() {
        return elements.size();
    }
    
    public RuleElements(Action action, Percep percep) {
        elements = new ArrayList(0);
        wildcards = 0;
        wildcardsCounted = false;
        firstNonWildcardPosition =  percep.getNumFluents() + 1;
        firstNonWildcardPositionEvaluated = true;
  
        if(action != null)
            add(action);
        else {
            Fluent wildcard = new Fluent(0);
            wildcard.setWildcard();
            add(wildcard);
        }
        
        for (int j = 0; j < percep.getNumFluents(); j++) {
            Fluent f = percep.getFluent(j);
            add(f);
        }
    }
    
    public void add(RuleObject o) {
        elements.add(o);
        wildcardsCounted = false;
        firstNonWildcardPositionEvaluated = false;
    }

    public void set(int index, RuleObject rule) {
        elements.set(index, rule);
        wildcardsCounted = false;
        firstNonWildcardPositionEvaluated = false;
    }

    public RuleObject get(int which) {
        return (RuleObject)elements.get(which);
    }

    public boolean matchAt(int position,   RuleObject o) {

        /*initialy we'll just do string matching for the rule elements,
         *but there's nothing to stop us using more complex matching with more
         *complex fluents*/
        if (o.isWildcard())
            return true;
        if (((RuleObject)elements.get(position)).isWildcard())
            return true;

        return isEqualAt(position, o);
   }

   //returns true if the two rules are the same
   public boolean isEqualAt(int position,   RuleObject o) {
       if (o.equals((RuleObject)elements.get(position)))
            return true;
        else
            return false;
   }

   public boolean isEqualTo(  RuleElements o) {
       for (int i = 0; i< elements.size(); i++) {
           if (!isEqualAt(i, (RuleObject)o.get(i)))
               return false;
       }

       return true;
   }

   public boolean equals(  RuleElements o) {
       for (int i = 0; i< elements.size(); i++) {
           if (!matchAt(i, (RuleObject)o.get(i)))
               return false;
       }

       return true;
   }

    public Object clone() {
        try {
            RuleElements o = (RuleElements)super.clone();	// clone the percep
            o.elements= new ArrayList(0);
            for (int i = 0; i < elements.size(); i++) {
                o.elements.add(((RuleObject)elements.get(i)).clone()); 
            }
            o.wildcards = wildcards;
            
            //we shouldn't strictly have to do this, but we always clone to make
            //new rule elements, so just to be safe:
            o.wildcardsCounted = false;
            o.firstNonWildcardPositionEvaluated = false;
            return o;				// return the clone
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
            throw new InternalError();
        }
    }

    public String toString() {
        String string = new String();
        for (int i = 0; i < elements.size(); i ++) {
            string += ((RuleObject)elements.get(i)).getRuleString();
        }

        return string;
    }

    /*returns true is the rule object has no action and one 
     *of the other elements is not a wildcard*/
    public boolean hasNoAction() {
        if (((RuleObject)elements.get(0)).isWildcard())
            return true;
        else
            return false;
    }

    //We should always use this rather than count wildcards for conversion
    //to the more general horne-clause style rules
    public int countNonWildcards() {
        return size() - countWildcards();
    }
    
    public int countWildcards() {
        if (wildcardsCounted) {
            return wildcards;
        }

        int wildcards = 0;
        
        for (int i = 0; i < size(); i++) {
            //check that the successor has a non-wildcard element here
            if (get(i).isWildcard()) 
                wildcards ++;
        }
        
        wildcardsCounted = true;
        this.wildcards = wildcards;
        return wildcards;   
    }
    
    public boolean isAllWildcardsFrom(int startPosition) {
        if (startPosition == 0) {
            if (getFirstNonWildcardPosition() == size())
                return true;
        }
        
        for (int i = startPosition; i < elements.size(); i++) {
            if (!((RuleObject)elements.get(i)).isWildcard())
                return false;
        }
        return true;
    }
    
    public RuleObject getFirstNonWildcard() {
        if (countNonWildcards() == 0)
            return null;
        else
            return get(getFirstNonWildcardPosition());
    }
    
    public int getFirstNonWildcardPosition() {
        
        if (getFirstNonWildcardPositionEvaluated())
            return firstNonWildcardPosition;
           
        setFirstNonWildcardPositionEvaluated(true);
        
        for (int i = 0; i < elements.size(); i++) {
            if (!((RuleObject)elements.get(i)).isWildcard()) {
                setFirstNonWildcardPosition(i);     
                return i;
            }
        }
        //System.out.print("ERROR: This rule elements was all wildcards");
        setFirstNonWildcardPosition(size());
        return size();
    }
    
    public boolean hasANonWildcard() {
        if (getFirstNonWildcardPosition() == size())
            return false;
        return true;
    }
    
    public RuleObject getNonWildcardElement(int which) {
        return get(getNonWildcardPosition(which));
    }
    
    public int getNonWildcardPosition(int which) {
        int count = 0;
        for (int i = 0; i < elements.size(); i++) {
            if (!((RuleObject)elements.get(i)).isWildcard()) {
                count++;
                if (count == which)
                    return i;
            }
        }
            
        return elements.size();
    }
    
    public boolean getFirstNonWildcardPositionEvaluated() {
        return firstNonWildcardPositionEvaluated;
    }
    
    public void setFirstNonWildcardPosition(int position) {
        firstNonWildcardPosition = position;
    }
    
    public void setFirstNonWildcardPositionEvaluated(boolean ev) {
        firstNonWildcardPositionEvaluated = ev;
    }

    public boolean actionIsInvalid() {
        if (((Action)elements.get(0)).isInvalid())
            return true;
        else
            return false;
    }

    public void copy(  RuleElements e) {
        for (int i = 0; i < e.size(); i++) {
            ((RuleObject)elements.get(i)).copy((RuleObject)e.get(i));
        }
        firstNonWildcardPositionEvaluated = false;
        wildcardsCounted = false;
    }
    
    /*Combine the two rule elements to make a more specific rule. This assumes there are
     *no conflicts in the two rules*/
    public RuleElements getCombinedMostSpecific(  RuleElements rule1,   RuleElements rule2) {
        RuleElements combined = (RuleElements)rule1.clone();
        
        for (int i = 0; i < combined.size(); i++) {
            if (!rule2.get(i).isWildcard()) {
                if (rule1.get(i).isWildcard()) {
                    combined.set(i, (RuleObject)rule2.get(i).clone());
                }
            }
        }
        
        return combined;
    }
    
}
