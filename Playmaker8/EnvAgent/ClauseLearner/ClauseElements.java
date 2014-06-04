package EnvAgent.ClauseLearner;

import EnvModel.*;
import EnvAgent.RuleLearner.*;

import java.util.*;
import java.io.*;

//import EnvModel.PredatorModel.*;
import EnvModel.*;

public class ClauseElements extends Object implements Cloneable, Serializable {
    ArrayList elements;
    static int[] usedit = new int[1000/*o.size()*/];;
    Term head;
    boolean ordered;
   
   
    
    public ClauseElements() {
        elements= new ArrayList(0);
        head = null;
        ordered = true;
      
    }
    
    /*public ClauseElements(RuleElements ruleElements, boolean precursor, ClauseList level1Clauses) {
        ClauseElements converter = convertRuleElementsToClauseElements(ruleElements, precursor);
        elements = converter.elements;
        head = null;
        if (level1Clauses != null)
            orderByUniqueID(level1Clauses);
       
    }*/

    
    public ClauseElements(  Percep percep,   Action action, boolean precursor, ClauseList level1Clauses) {
        if (true) { //PREDATOR ENVIRONMENT
            ClauseElements converter = convertPercepActionToClauseElements(percep, action, precursor);
            elements = converter.elements;
            head = null;
            if (level1Clauses != null)
                orderByUniqueID(level1Clauses);
            else {
                int notordered = 1;
            }
        } else { //other environment
            ;
        }
    }

    public ClauseElements convertPercepActionToClauseElements(  Percep aPercep,   Action anAction, boolean precursor) {
        //Wildcard Variable, which can also match actions
       
        ClauseElements clauseElements = new ClauseElements();
        
        if (anAction != null)
            clauseElements.add(anAction.convertToClauseElements(precursor));
        clauseElements.add(aPercep.convertToClauseElements(precursor));
        
        return clauseElements;
    }

    
    
   /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        ArrayList tempElements = elements;
        elements = null;
        Term tempHead = head;
        head = null;
        s.defaultWriteObject();

        s.writeInt(tempElements.size());
        for (int i = 0; i < tempElements.size(); i++) {
            s.writeObject(tempElements.get(i));
        }
        
        s.writeObject(tempHead);
        s.writeBoolean(ordered);
        
        head = tempHead;
        elements = tempElements;
      
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        int tempSize = s.readInt();
        for (int i = 0; i < tempSize; i++) {
            elements.add(s.readObject());
        }
        head = (Term)s.readObject();
        ordered = s.readBoolean();
   
    }
    
    public int size() {
        return elements.size();
    }

    public void add(Term o) {
        elements.add(o);
        ordered = false;
    }
    
    public void add(ClauseElements clauseElements) {
        for (int i = 0; i < clauseElements.size(); i++) {
            add(clauseElements.get(i));
        }
    }
    
    public void addHead(Term h) { 
        head = h;
    }
   
    public void removeHead() {
        head = null;
    }
    
    public Term getHead() {
        return head;
    }
    
    public int getBodySize() {
        return elements.size();
    }

    public Term get(int which) {
        return (Term)elements.get(which);
    }
    
    public void remove(int which) {
        elements.remove(which);
    }
 
    public boolean getOrdered() {
        return ordered;
    }
    
    public void setOrdered(boolean order)
    {
        ordered = order;
    }
    
    public void findUniqueIDs(ClauseList level1Clauses) {
        
        if (head != null) {
            head.setID(head.findID(level1Clauses));
        }
        
        for (int j = 0; j < elements.size(); j++) {
            Term firstTerm = (Term)elements.get(j);
            if (level1Clauses != null) {
                firstTerm.setID(firstTerm.findID(level1Clauses));
            }
        }
    }
    
    public void orderByUniqueID(ClauseList level1Clauses) {
        if (!ordered) {
            findUniqueIDs(level1Clauses);
            
            for (int i = 0; i < elements.size() -1; i++) {
                boolean swapped = false;
                for (int j = 0; j < elements.size()-1; j++) {
                    Term firstTerm = (Term)elements.get(j);
                    Term secondTerm = (Term)elements.get(j+1);
                
                    if (firstTerm.getUniqueID() > secondTerm.getUniqueID()) {
                        Term swapper = (Term)elements.get(j);
                        elements.set(j, (Term)elements.get(j+1));
                        elements.set(j+1, swapper);
                        swapped = true;
                    }
                }
                if (!swapped) {
                    //we didn't swap any on the last loop so break out
                    //(slightly quicker ordering, but not much
                    i = elements.size();
                }
            }
            ordered = true;
        }
        
    }

    public int getLastElementID() {
         if (head != null)
             return head.getUniqueID();
         else {
             return ((Term)(elements.get(elements.size()-1))).getUniqueID();
         }
    }
 
    /*Subsumes return true if this clause is a generalisation of
     *dependency o*/
    /*With clauses this means that this clauses elements are a subset of o's elements
     *Or it can mean that there are equals in some way, but "this" is more general
     *WE CAN USE THE GENERALISATION RULE BASED ON THE DATABASE. IF THE DATABASE
     *equals OF ONE RULE ARE A SUBSET OF THE OTHER THEN THE OTHER IS MORE GENERAL
     *
     */
    boolean subsumes (ClauseElements o) {
       /*Successor must be identical. We will likely know where the first non-wildcard
         element is as it's stored*/
        //CURRENTLY ASSUMES ORDERING
       
        if (!head.isEqual(o.getHead()))
            return false;
        
        //System.out.print("\nWARNING: subsumes not implemented in ClauseLearner. Should check for subsets\n");
        
        //for the moment we'll just do it as head same and body d1 more general than d2 as before.
        //VARIABLE SUBSTITUTIONS!
        return bodyequals(o);   
    }
    
    /*Clause elements match a rule elements if all of the clauses conditions are met by at least
     one of the elements.
     There will often be a set of potential equals. Each one of which will imply a different head*/
    public boolean bodyequals(  ClauseElements o) {
        //System.out.print("\nWARNING: bodyequals in ClauseElements. Should return a head matching");
        
        java.util.Arrays.fill(usedit,0);
        int nextTest = 0; //elements are ordered, so once used we can start from there
        boolean zeroPresent = false;
        for (int i = 0; i < elements.size(); i++) {
            boolean satisfied = false;
            for (int j = nextTest; j < o.size(); j++) {
                if ((usedit[j] == 0) && get(i).equals(o.get(j))) {
                    //Once one of the o elements has been equals it shouldn't be used again
                    satisfied = true;
                    usedit[j] = 1;
                    if ((o.get(j).getUniqueID() != 0) && (get(i).getUniqueID() != 0) && (zeroPresent == false))
                        nextTest = j + 1;
                    else {
                        zeroPresent = true;
                        nextTest = 0;
                    }
                    j = o.size();
                } else if (o.get(j).getUniqueID() > get(i).getUniqueID()) {
                    //elements are ordered, so if we find the ID of the rule element is bigger
                    //than the ID of the observed clause we know there is no match
                    
                    //BUT make sure these are not ID 0's (i.e. not set in the DB)
                    if ((o.get(j).getUniqueID() != 0) && (get(i).getUniqueID() != 0)) {        
                        return false;
                    }
                }
            }
            if (!satisfied) {
                return false;
            }
       }

       return true;
  }
    
    /*Tests that every element is equal*/
    public boolean bodyIsEqual(  ClauseElements c) {
         //check if they have the same number of elements. If not then can't be equal
       if (c.getBodySize() != getBodySize())
           return false;
       
       //if both are ordered the same this will return true;
       boolean equal = true;
       for (int i = 0; i < elements.size(); i++) {
           if (!get(i).isEqual(c.get(i)))
               equal = false;
           if (!equal)
               i = elements.size();
       }
       
       if (equal)
           return true;
       
       if (getOrdered() && c.getOrdered())
           return false;
       
       //if the element ordering is different thay can still be equal
       for (int i = 0; i < elements.size(); i++) {
           if (!contains(c.get(i))) {
               return false;
           }
       }
       
       return true;
  }
   
   public Term possibleHeadForBodyMatch(  ClauseElements o) {
        //System.out.print("\nWARNING: possibleHeadForBodyMatch in ClauseElements. Should only be able to match with one ruleElement");
        //System.out.print("Need to keep varibale substitutions from body match and use for head");
        
        //If there are no varibales involved then we can just match with no problem

       return head;
   }
   
   public boolean headequals(  Term match) {
       return head.equals(match);
   }
   
   public boolean contains(  Term t) {
       for (int i = 0; i < elements.size(); i++) {
            if (get(i).isEqual(t)) 
                return true;
       }
       return false;
   }

   public boolean containsBodyEquivalent(Clause theHead) {
       for (int i = 0; i < elements.size(); i++)
           if (((Clause)elements.get(i)).equalBodyVariable(theHead))
                return true;
 
       return false;
   }

   public boolean containsAnAction() {
       for (int i = 0; i < elements.size(); i++)
           if (((Clause)elements.get(i)).isAction())
                return true;

       return false;
   }

    public Clause getAction() {
       for (int i = 0; i < elements.size(); i++)
           if (((Clause)elements.get(i)).isAction())
                return ((Clause)elements.get(i));

       return null;
   }

    public boolean hasSameAction(ClauseElements other) {
        if (containsAnAction() && other.containsAnAction())
        {
            return (getAction().isEqual(other.getAction()));
        }
        return false;
    }
   
   public boolean isEqualTo(ClauseElements c) {
       
       boolean equalHead = false;
       if (head == null) {
           if (c.getHead() == null) {
               equalHead = true;
           } else {
               return false;
           }
       } 
       
       if (!equalHead) {
           if (c.getHead() == null)
               return false;
           if (!head.isEqual(c.getHead()))
                return false;
       }
       
      return bodyIsEqual(c);
   }

    @Override
    public Object clone() {
        try {
            ClauseElements o = (ClauseElements)super.clone();	// clone the percep
            if (getHead() != null)
                o.addHead((Term)head.clone());
            else
                o.addHead(null);
            o.elements= new ArrayList(0);
            for (int i = 0; i < elements.size(); i++) {
                o.elements.add(((Term)elements.get(i)).clone()); 
            }
            return o;				// return the clone
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        String string = new String();
        
        if (getHead() != null)
            string += getHead().toString();
        
        for (int i = 0; i < elements.size(); i ++) {
            string += ((Term)elements.get(i)).toString();
        }

        return string;
    }
    
    /*Combine the two rule elements to make a more specific rule. This assumes there are
     *no conflicts in the two rules*/
    public ClauseElements getCombinedMostSpecific(  ClauseElements rule1,   ClauseElements rule2) {
        ClauseElements combined = (ClauseElements)rule1.clone();
        
        for (int i = 0; i < rule2.size(); i++) {
            if (!rule1.contains(rule2.get(i))) 
                combined.add((Term)rule2.get(i).clone());
        }
        
        combined.setOrdered(false);
        combined.orderByUniqueID(null);
        return combined;
    }
    
    public ClauseElements getCombinedBodies(  ClauseElements rule1,   ClauseElements rule2) {
        ClauseElements combined = getCombinedMostSpecific(rule1, rule2);
        combined.removeHead();
        return combined;
    }
    
    public Percep convertToPercep(Percep percep, Action action) {
        //Wildcard Variable, which can also match actions
       
        
        return percep.convertFromClauseElements(this);
    }
    

  
    public ClauseList clauseVariableSubstitutions() {
        //Go through all the varibales in the clause. If the same varibale with the same
        //value appears more than once we will make a new rule with this replacement 
        //and add it to substitutions. 
        
        ClauseList substitutions = new ClauseList();
        
        //First make an array of all the varibales contained in this rule
       
        ArrayList variableStore = new ArrayList();
        if (getHead() != null)
            variableStore.add(getHead().getVariables());
        
        for (int i = 0; i < size(); i++) {
            variableStore.add(get(i).getVariables());
        }
        
        //Now remove all non-ground varibales (we've allready subsituted them so there's no need)
        for (int i = variableStore.size(); i >= 0; i--) {
            if (!get(i).isGround())
                variableStore.remove(i);
        }
        
        ArrayList individualVariables = new ArrayList();
        int variablesCountStore[] = new int[variableStore.size()];
        for (int i = 0; i < variableStore.size(); i++)
            variablesCountStore[i] = 0;
        
        for (int vs = 0; vs < variableStore.size(); vs++)  {
            Variable vsVar = (Variable)variableStore.get(vs);
            boolean present = false;
            for (int iv = 0; iv < individualVariables.size(); iv ++) {
                Variable ivVar = (Variable)individualVariables.get(iv);
                if (ivVar.isEqual(vsVar)) {
                    present = true;
                    variablesCountStore[iv] ++;
                    iv = individualVariables.size();
                }
            }
            if (!present) {
                //first occurrence of this variable
                individualVariables.add(vsVar);
                variablesCountStore[individualVariables.size() -1] = 1;
            }
        }
        
        //At this point indicidualVaribales is an array of all the individual varihbales
        //and varibalesCountStore contains the number of times we've found these varibales
        
        //Now we need to return the clauseElements with all the individual varibales substituted
        //if they occur more than once.
        
        return substitutions;
        
    }


    
  
  
}
