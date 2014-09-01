package EnvModel.PredatorModel;

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
import V_Sensors.Sensor;
import V_Sensors.TokenMap;

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
public class PredatorAgentPercep extends Percep implements Cloneable, Serializable {

 
    /*possible directions to look in*/
    public static final int
        NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, BELOW = 4;
    
    /*in total there are five elements to the percept for the five
     *observable squares*/
    public static final int SIZE = 5;
   
    /** Creates new Percep */
    public PredatorAgentPercep() {
        perceptions = new ArrayList(SIZE);
        for (int i = 0; i < SIZE; i++) {
            perceptions.add(i, new PredatorFluent());
            ((PredatorFluent)perceptions.get(i)).setValue(PredatorFluent.EMPTY);
        }
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
    
    public PredatorAgentPercep(RuleElements ruleElements) {
        perceptions = new ArrayList(SIZE);
        for (int i = 0; i < SIZE; i++) {
            perceptions.add(i, new PredatorFluent());
            ((PredatorFluent)perceptions.get(i)).setValue(ruleElements.get(i+1).getValue());
        }
    }

    /*the object must be cloneable so that a list of perceptions can be created*/
    public Object clone() {
        PredatorAgentPercep p = (PredatorAgentPercep)super.clone();	// clone the stack
        p.perceptions = (ArrayList)perceptions.clone();	// clone the vector
        //now clone the elements
        for (int i =0; i < perceptions.size(); i++) {
            p.perceptions.set(i, ((PredatorFluent)perceptions.get(i)).clone());
        }
        return p;				// return the clone
    }
   
    
    public int getNumFluents() {
        return SIZE;
    }
    
    /*return a tokenised version of the contents of the percep as a string*/
    public String toString() {
        String percepString = "[";
        for (int i = 0; i < SIZE; i++) {
            percepString += ((PredatorFluent)getFluent(i)).toString();
            percepString += " ";
        }
        
        percepString += "]";
        
        return percepString;
    }
    
    
    public String translation() {
        String percepString = "";
        for (int i = 0; i < SIZE; i++) {
            percepString += ((PredatorFluent)getFluent(i)).translation();
            percepString += ",";
        }
        
        percepString += "";
        
        return percepString;
    }
    
    
    
    public boolean isOntopOfAgent() {
        return ((PredatorFluent)getFluent(BELOW)).isAgentBody();
    }
   
    
     public ClauseElements convertToClauseElements(boolean precursor) {
         ClauseElements clauseElements = new ClauseElements();
         for (int i = 0; i < this.getNumFluents(); i++) {
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
         
         return clauseElements;
     }
     
      /*for the purposes of this exercise the legal state funciton will be hard-coded*/
    public boolean legalState() {
        int agents = 0;
        for (int i = 0; i < getNumFluents(); i++) {
            if (((PredatorFluent)getFluent(i)).isAgentBody())
                agents ++;
            if (agents > 1)
                return false;
        }
        
        
        //Check that we haven't got walls opposite eachother,
        //which is not a vallid state
        
        if (((PredatorFluent)getFluent(0)).isWall() &&
            ((PredatorFluent)getFluent(2)).isWall())
            return false;
        
        if (((PredatorFluent)getFluent(1)).isWall() &&
            ((PredatorFluent)getFluent(3)).isWall())
            return false;
        
        return true;
    }
    
    public double reward() {
       double reward;
        if (true) {
            if (isOntopOfAgent())
                reward = 1.0f;
            else
                reward = 0.0f;
        }
        else {
           if (isOntopOfAgent())
                reward = 0.0f;
            else
                reward = 1.0f;
        }
        return reward;
    }
    
    public Percep convertFromClauseElements(ClauseElements clauseElements) {
        
        PredatorAgentPercep pap = new PredatorAgentPercep();
        if (clauseElements.size() > (getNumFluents() + 1)) {
            int hello = 0;
        }
        
        for (int i =0; i < clauseElements.size(); i++) { 
        
            int percepPos = 0;
            if (((Clause)clauseElements.get(i)).getPredicate(0).toString().equals("Dir(N)"))
                percepPos = 0;
            else if (((Clause)clauseElements.get(i)).getPredicate(0).toString().equals("Dir(E)"))
                percepPos = 1;
            else if (((Clause)clauseElements.get(i)).getPredicate(0).toString().equals("Dir(S)"))
                percepPos = 2;
            else if (((Clause)clauseElements.get(i)).getPredicate(0).toString().equals("Dir(W)"))
                percepPos = 3;
            else if (((Clause)clauseElements.get(i)).getPredicate(0).toString().equals("Dir(U)"))
                percepPos = 4;
             else if (((Clause)clauseElements.get(i)).getPredicate(0).toString().substring(0,3).equals("Rew"))
                percepPos = 5;
            else {
                percepPos = 0;
                System.out.print("Error in percep pos. String not known");
                System.out.print(((Clause)clauseElements.get(i)).getPredicate(0).toString());
            }
           
            pap.getFluent(percepPos).convertFromClause((Clause)clauseElements.get(i));
        }
        
        return pap;
    }
    
    @Override
    public void readFromString(String str) {
    
    //System.out.println("The target String (" + str + ") is " + str.length() + "chars long");
    
    for (int i = 0; i < str.length()-1; i++)
    {
        switch(str.charAt(i)) {
            case 'W' : setPercep(i, 2);
                break;
            case 'E' : setPercep(i, 0);
                break;
            case 'A' : setPercep(i, 1);
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
            System.out.println();
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
