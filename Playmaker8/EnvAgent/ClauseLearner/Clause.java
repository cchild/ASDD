package EnvAgent.ClauseLearner;

import java.io.*;
import java.util.*;

import EnvModel.*;

import EnvModel.PredatorModel.*;

/*
 * Action.java
 *
 * Created on March 6, 2001, 6:24 PM
 */


/**
 *
 * @author  Chris Child
 * @version 
 * This will contain all "available" actions for the agent and works as an 
 * interface between the agent and the agent body. The action should contain 
 * all possible actions for the agent and should not allow mutually exclusive actions
 * to be set.
 */
public class Clause extends Term implements Cloneable, Serializable {
    
    String name;
    ArrayList predicates;
    
    /** Creates new Action */
    public Clause() {
        name = "";
        predicates = new ArrayList(0);
    }
    
    public Clause(String name, int noPredicates) {
        this.name = name;
        predicates = new ArrayList(0);
        for (int i = 0; i < noPredicates; i++) {
            predicates.add(new Variable());
        }
    }
    
    /*clone is implemented so that lists of agent actions can be created*/
    public Object clone() {
        //try {
            Clause o = (Clause)super.clone();	// clone the percep
            o.name = new String();
            o.name = name;
            o.predicates = new ArrayList(0);
            for (int i = 0; i < predicates.size(); i++) {
                o.predicates.add(((Term)predicates.get(i)).clone()); 
            }
            return o;				// return the clone
        //} catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
        //    throw new InternalError();
        //}
    }
    
    /*Call standard write object.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        String tempName = name;
        name = null;
        ArrayList tempPredicates = predicates;
        predicates = null;
        s.defaultWriteObject();
        
        s.writeObject(tempName);
        s.writeInt(tempPredicates.size());
        for (int i = 0; i < tempPredicates.size(); i++) {
            s.writeObject(tempPredicates.get(i));
        }
    }
  
    /*Call standard read object.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        name = (String)s.readObject();
        int tempSize = s.readInt();
        for (int i = 0; i < tempSize; i++) {
            predicates.add(s.readObject());
        }
    }
    
    public String toString() {
        String outString = new String();
        outString += name + "(";
        for (int i = 0; i < predicates.size(); i ++) {
            outString += predicates.get(i).toString();
            if (i < predicates.size() -1)
                outString += ",";
        }
        return outString + ") [ID:" + getUniqueID() + "]";
    }

    public Term getPredicate(int which) {
        return (Term)predicates.get(which);
    }
    
    public void addPredicate(Term predicate) {
        predicates.add(predicate);
    }
    
    public void setPredicate(int which, Term predicate) {
        predicates.set(which, predicate);
    }
    
    public boolean isEqual(Term o) {
        Clause c = (Clause)o;
        if (!c.getClass().equals(this.getClass())) {
            //We shouldn't stop at this point
            return false;
        }
        
          if (c.predicates.size() != predicates.size()) {
                //these are the same type of cluse but different number of predicates
                return false;
         }
        
        if (this.getUniqueID() != 0)
            if (c.getUniqueID() != 0) {
                if (this.getUniqueID() == c.getUniqueID()) {
                    //String tester = this.getPredicate(0).toString();
                    //String tester2 = c.getPredicate(0).toString();
                    //if (!tester.equals(tester2))
                    //{
                   //     int stoper = 1;
                    //}
                   
                    return true;
                }
                else
                    return false;
            }
        
        if (this.getName().hashCode() != c.getName().hashCode() || !this.getName().equals(c.getName()))
            return false;
        
        for (int i = 0; i < predicates.size(); i ++) {
            if (!c.getPredicate(i).isEqual(getPredicate(i)))
                return false;
        }
        
        return true;
    }
    
    public boolean sameOutVariable(Clause testHead) {
        
        if (!testHead.getClass().equals(this.getClass())) {
            //We shouldn't stop at this point
            return false;
        }
        
        if (testHead.getNoPredicates() != this.getNoPredicates())
             return false;
        
        if (getUniqueID() != 0)
            if (testHead.getUniqueID() != 0)
                if (getUniqueID() == testHead.getUniqueID())
                    return true;
        
        if (getName().hashCode() != testHead.getName().hashCode() || !getName().equals(testHead.getName()))
            return false;
        
     
        
        //For clauses with only 1 prediate, the predicate must hold the value
        //so these are the same output variable if they are the same type
        if (testHead.getNoPredicates() == 1 && this.getNoPredicates() == 1) {
            String type1 = ((Variable)testHead.getPredicate(0)).getType();
            String type2 = ((Variable)this.getPredicate(0)).getType();
            if (type1.hashCode() == type2.hashCode() && type1.equals(type2))
                return true;
            else
                return false;
       }
        
        //For Pred/Prey If they have the same first predicate then they are the same variable
        if (!testHead.getPredicate(0).isEqual(getPredicate(0)))
            return false;
       
        return true;
    }


     public boolean equalBodyVariable(Clause testHead) {

        if (testHead.getNoPredicates() != this.getNoPredicates())
             return false;

        if (testHead.toString().contains("Next")){
            int indexHead = testHead.toString().indexOf("Next");
            //head should always contain next anyway, but best to check
            String comp1 = testHead.toString().substring(0, indexHead-1);
            String comp2 = toString().substring(0, indexHead-1);
            if (!comp1.equalsIgnoreCase(comp2)) {
                //the bit before the Next isn't the same so this is a different varibale
                //e.g. see next, or painted next
                return false;
            }
        }

        //For clauses with only 1 prediate, the predicate must hold the value
        //so these are the same output variable if they are the same type
        if (testHead.getNoPredicates() == 1 && this.getNoPredicates() == 1) {
            String type1 = ((Variable)testHead.getPredicate(0)).getType();
            String type2 = ((Variable)this.getPredicate(0)).getType();
            if (type1.hashCode() == type2.hashCode() && type1.equals(type2)) {
                //now test that they have the same value
                if (((Variable)testHead.getPredicate(0)).getValue() == ((Variable)this.getPredicate(0)).getValue())
                    return true;
                else
                    return false;
            }
            else
                return false;
       }

        //For Pred/Prey If they have the same first predicate then they are the same variable
        if (testHead.getPredicate(0).isEqual(getPredicate(0))) {
            //and if they have the same second predicte then they're equal
             if (testHead.getPredicate(1).isEqual(getPredicate(1)))
                  return true;
             else
                  return false;
        } else {
            return false;
        }
    }

    public boolean isAction() {

        if (this.toString().contains("Act")){
            return true;
        }
        return false;
    }

        
    /*returns the number of value that can be taken by this rule object
     */
    public int getNumValues() {
        int total = 0;
        for (int i = 0; i < predicates.size(); i ++) {
            total += getPredicate(i).getNumValues();
        }
        
        return total;
    }
    
    /*A function for setting rule by a number so that we can itterate through all values*/
    public void setByValue(int value) {
        System.out.print("YOU SHOULDN'T SET A CLAUSE BY VALUE");
    }
    
    /*Value as in set by value*/
    public int getValue() {
          System.out.print("YOU SHOULDN'T GET VALUE ON A CLAUSE");
          return 0;
    }
    
    /*returns true if these rule elements match. Wildcards not counted*/
    public boolean equals(Term o) {
        if (!o.getClass().equals(this.getClass())) {
            //We shouldn't stop at this point
            return false;
        }
        
          if (((Clause)o).predicates.size() != predicates.size())
            return false;
    
        if (getUniqueID() != 0) {
            /****************************
             *Clauses with variables should have an ID 0 for the next bit
             ****************************/
            if (((Clause)o).getUniqueID() != 0)
                if (getUniqueID() == ((Clause)o).getUniqueID())
                    return true;
                else
                    return false;
        }
        
        if (getName().hashCode() != ((Clause)o).getName().hashCode())
            return false;
        
        if (!getName().equals(((Clause)o).getName()))
            return false;
        
        for (int i = 0; i < predicates.size(); i ++) {
            if (!((Clause)o).getPredicate(i).equals(getPredicate(i)))
                return false;
        }
        
        return true;
    }
    
    public boolean isGround() {
        for (int i = 0; i < predicates.size(); i ++) {
            if (!getPredicate(i).isGround())
                return false;
        }
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    public int getNoPredicates() {
        return predicates.size();
    }
    
    public ArrayList getVariables() {
        ArrayList variables = new ArrayList();
        for (int i = 0; i < predicates.size(); i++) {
            if (getPredicate(i).isVariable()) {
                //This is a varibale so add it
                variables.add(getPredicate(i));
            }
            else {
                ArrayList newVars = getPredicate(i).getVariables();
                //this might be a clause so add the varibales in it
                if (newVars != null)
                    variables.add(newVars);
            }
        }
        
        if (variables.size() > 0)
            return variables;
        else
            return null;
    }
    
  
}
