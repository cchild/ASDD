package EnvModel.KillPredatorModel;

/*
 * PredatorAgentPercep.java
 *
 * Created on March 6, 2001, 5:38 PM
 */

import java.util.*;
import EnvModel.*;
import EnvAgent.*;
import EnvAgent.RuleLearner.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.PredatorAgent.PredatorAgent;

import java.io.*;

/**
 *
 * @author  Chris Child
 * This object stores the contents of perception which the agent
 * body creates from the current state of the environment. 
 * Implements cloneable so that a list of perceptions can be stored
 * by the agent. Also has a getString function which allows the
 * perep to be output to a text file.
 */
public class KillPredatorAgentPercep extends Percep implements Cloneable, Serializable {

 
    /*possible directions to look in*/
    public static final int
        NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, BELOW = 4, REWARD = 5;
    
    /*in total there are five elements to the percept for the five
     *observable squares*/
    public static final int SIZE = 6;
    
    static final String DIR_N = "Dir(N)";
    static final String DIR_E = "Dir(E)";
    static final String DIR_S = "Dir(S)";
    static final String DIR_W = "Dir(W)";
    static final String DIR_U = "Dir(U)";
   
    /** Creates new Percep */
    public KillPredatorAgentPercep() {
        perceptions = new ArrayList(SIZE);
        for (int i = 0; i < SIZE -1; i++) {
            perceptions.add(i, new KillPredatorFluent());
            ((KillPredatorFluent)perceptions.get(i)).setValue(KillPredatorFluent.EMPTY);
        }
        
        perceptions.add(SIZE -1, new KillRewardFluent());
        ((KillRewardFluent)perceptions.get(SIZE-1)).setValue(KillRewardFluent.NO_REWARD);
    }
    
          /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        super.writeObject(s);

        s.writeObject(perceptions);
    }
    
       /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        super.readObject(s);
        perceptions = (ArrayList)s.readObject();
    }
    
    public KillPredatorAgentPercep(RuleElements ruleElements) {
        perceptions = new ArrayList(SIZE);
        for (int i = 0; i < SIZE-1; i++) {
            perceptions.add(i, new KillPredatorFluent());
            ((KillPredatorFluent)perceptions.get(i)).setValue(ruleElements.get(i+1).getValue());
        }
         
        perceptions.add(SIZE-1, new KillRewardFluent());
        ((KillRewardFluent)perceptions.get(SIZE-1)).setValue(ruleElements.get(SIZE-1+1).getValue());
    }

    /*the object must be cloneable so that a list of perceptions can be created*/
    public Object clone() {
        KillPredatorAgentPercep p = (KillPredatorAgentPercep)super.clone();	// clone the stack
        p.perceptions = (ArrayList)perceptions.clone();	// clone the vector
        //now clone the elements
        for (int i =0; i < perceptions.size() -1; i++) {
            p.perceptions.set(i, ((KillPredatorFluent)perceptions.get(i)).clone());
        }
        p.perceptions.set(perceptions.size() -1, ((KillRewardFluent)perceptions.get(perceptions.size() -1)).clone());
        return p;				// return the clone
    }
   
    
    public int getNumFluents() {
        return SIZE;
    }
    
    /*return a tokenised version of the contents of the percep as a string*/
    public String toString() {
        String percepString = "[";
        for (int i = 0; i < SIZE; i++) {
            percepString += getFluent(i).toString();
            percepString += " ";
        }
        
        percepString += "]";
        
        return percepString;
    }
    
    
    public String translation() {
        String percepString = "";
        for (int i = 0; i < SIZE; i++) {
            percepString += ((KillPredatorFluent)getFluent(i)).translation();
            percepString += "";
        }
        
        percepString += "";
        
        return percepString;
    }
    
    
    
    public boolean isOntopOfAgent() {
        return ((KillPredatorFluent)getFluent(BELOW)).isAgentBody();
    }
    
   
    
     public ClauseElements convertToClauseElements(boolean precursor) {
         ClauseElements clauseElements = new ClauseElements();
         for (int i = 0; i < this.getNumFluents() -1; i++) {
            Clause see;
            if (precursor == true) 
                see = new Clause("See", 2);
            else
                see = new Clause("SeeNext", 2);

            Variable direction = new Variable();

            //We'll pick up these from background knowledge later
            direction.setType("Dir");
            //System.out.print("\nDirection may be set wrong in convertRuleElementsToClauseElements");
            direction.setValue(i); 
            see.setPredicate(0, direction);
            Variable seeObject = new Variable();
            seeObject.setType("Obj");
            seeObject.setValue(this.getFluent(i).getValue());
            see.setPredicate(1, seeObject);
            clauseElements.add(see);
        }
         
        Clause see;
        if (precursor == true) 
            see = new Clause("See", 1);
        else
            see = new Clause("SeeNext", 1);

        Variable reward = new Variable();

        //We'll pick up these from background knowledge later
        reward.setType("Rew");
        //System.out.print("\nDirection may be set wrong in convertRuleElementsToClauseElements");
        reward.setValue(this.getFluent(this.getNumFluents() -1).getValue());
  
        see.setPredicate(0, reward);
        clauseElements.add(see);
         
         return clauseElements;
     }
     
      /*for the purposes of this exercise the legal state funciton will be hard-coded*/
    public boolean legalState() {
        int agents = 0;
        //note the -1 because we now have the reward state at the end
        for (int i = 0; i < getNumFluents() -1; i++) {
            if (((KillPredatorFluent)getFluent(i)).isAgentBody())
                agents ++;
            if (agents > 1)
                return false;

        }


        if (((KillRewardFluent)this.getFluent(this.getNumFluents()-1)).isPositive())
        {
            //It's impossible for the agent to have gone out of the percep completely
            if (agents != 1) {
                return false;
            }
        }
        
        //Check that we haven't got walls opposite eachother,
        //which is not a vallid state
        
        if (((KillPredatorFluent)getFluent(0)).isWall() &&
            ((KillPredatorFluent)getFluent(2)).isWall())
            return false;
        
        if (((KillPredatorFluent)getFluent(1)).isWall() &&
            ((KillPredatorFluent)getFluent(3)).isWall())
            return false;
        
        return true;
    }
    
    public double reward() {
       double reward;
       
       //the true bit here means prey rather than predator
       //this has changed to relect the new reward for a kill
    
        if (((KillRewardFluent)this.getFluent(this.getNumFluents()-1)).isPositive())
            reward = 10.0f;
        else if (((KillRewardFluent)this.getFluent(this.getNumFluents()-1)).isNegative())
            reward = -10.0f;
        else
            reward = 0.0f;
      
        return reward;
    }
    
    public Percep convertFromClauseElements(ClauseElements clauseElements) {
       
        KillPredatorAgentPercep pap = new KillPredatorAgentPercep();
        if (clauseElements.size() > (getNumFluents() + 1)) {
            int hello = 0;
        }
        
        for (int i =0; i < clauseElements.size(); i++) { 
            int percepPos = 0;
            
            Term term = ((Clause)clauseElements.get(i)).getPredicate(0);
            
            if (term.stringIsEqual(DIR_N))
                percepPos = 0;
            else if (term.stringIsEqual(DIR_E))
                percepPos = 1;
            else if (term.stringIsEqual(DIR_S))
                percepPos = 2;
            else if (term.stringIsEqual(DIR_W))
                percepPos = 3;
            else if (term.stringIsEqual(DIR_U))
                percepPos = 4;
            else {
                String substr = term.toString().substring(0,3);
                if (substr.equals("Rew"))
                    percepPos = 5;
                else {
                    percepPos = 0;
                    System.out.print("Error in percep pos. String not known");
                    System.out.print(((Clause)clauseElements.get(i)).getPredicate(0).toString());
                }
            }
            
           pap.getFluent(percepPos).convertFromClause((Clause)clauseElements.get(i));
        }
        
        return pap;
    }
    
    
     @Override
    public void readFromString(String str) {
    
    System.out.println("The target String (" + str + ") is " + str.length() + "chars long");
    //String out = "";
    for (int i = 0; i < str.length()-1; i++)
    {
        switch(str.charAt(i)) {
            case 'W' : setPercep(i, 2);
                break;
            case 'E' : setPercep(i, 0);
                break;
            case 'A' : setPercep(i, 1);
                break;
        // REWARDS
        // 1 PO_RE
        // 0 NO_RE
        // 2 NE_RE
            case '=' : setPercep(i, 0);
                break;
            case '+' : setPercep(i, 1);
                break;
            case '-' : setPercep(i, 2);
                break;
            // No relevant case found, ERROR is returned
            default : setPercep(i, 4);
                break;
        }
        //System.out.println(str.charAt(i));
    }
    }
    
    
    public int readFile (PredatorAgent pred) {
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                pred.getPercep().readFromString(line);

            System.out.println("Line " + i + " :  " + line + "  >>  " + "Percep " + i + pred.getPercep());
                
                //faites ici votre traitement
            
            }
            
            
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        
        
        return 1;
    }
}
