package EnvModel.PaintModel;

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
public class PaintAgentPercep extends Percep implements Cloneable, Serializable {

 
    /*possible directions to look in*/
    public static final int
        BLOCK_PAINTED = 0, GRIPPER_CLEAN = 1, GRIPPER_DRY = 2, HOLDING_BLOCK = 3, REWARD = 4;
    
    /*in total there are five elements to the percept for the five
     *observable squares*/
    public static final int SIZE = 5;
    /*Store the five perceptions*/
    ArrayList perceptions;
    /** Creates new Percep */
    public PaintAgentPercep() {
        perceptions = new ArrayList(SIZE);
        perceptions.add(0, new PaintPaintedFluent());
        perceptions.add(1, new PaintCleanFluent());
        perceptions.add(2, new PaintDryFluent());
        perceptions.add(3, new PaintHoldingFluent());
        perceptions.add(4, new PaintRewardFluent());
        
        ((Fluent)perceptions.get(0)).setValue(PaintPaintedFluent.NOT_BLOCK_PAINTED);
        ((Fluent)perceptions.get(1)).setValue(PaintCleanFluent.GRIPPER_CLEAN);
        ((Fluent)perceptions.get(2)).setValue(PaintDryFluent.GRIPPER_DRY);
        ((Fluent)perceptions.get(3)).setValue(PaintHoldingFluent.HOLDING_BLOCK);
        ((Fluent)perceptions.get(4)).setValue(PaintRewardFluent.NO_REWARD);
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
    
    public PaintAgentPercep(RuleElements ruleElements) {
        ((Fluent)perceptions.get(0)).setValue(ruleElements.get(1).getValue());
        ((Fluent)perceptions.get(1)).setValue(ruleElements.get(2).getValue());
        ((Fluent)perceptions.get(2)).setValue(ruleElements.get(3).getValue());
        ((Fluent)perceptions.get(3)).setValue(ruleElements.get(4).getValue());
        ((Fluent)perceptions.get(4)).setValue(ruleElements.get(5).getValue());
       
    }

    /*the object must be cloneable so that a list of perceptions can be created*/
    public Object clone() {
        PaintAgentPercep p = (PaintAgentPercep)super.clone();	// clone the stack
        p.perceptions = (ArrayList)perceptions.clone();	// clone the vector
        //now clone the elements
        for (int i =0; i < perceptions.size(); i++) {
            p.perceptions.set(i, ((Fluent)perceptions.get(i)).clone());
        }
        return p;				// return the clone
    }
    
    /*set an individual element of the perceptual array*/
    public void setPercep(int which, int type) {
        Fluent fluent = (Fluent)perceptions.get(which);
        fluent.setValue(type);
    }
    
    /*at an individual element of the perceptual array*/
    public int getPercep(int which) {
        return getFluent(which).getValue();
    }
    
    public Fluent getFluent(int which) {
        return (Fluent)perceptions.get(which);
    }
    
    public int getNumFluents() {
        return SIZE;
    }
    
    /*return a tokenised version of the contents of the percep as a string*/
    public String toString() {
        String percepString = "[";
        for (int i = 0; i < SIZE; i++) {
            percepString += ((Fluent)getFluent(i)).toString();
            percepString += " ";
        }
        
        percepString += "]";
        
        return percepString;
    }
    
    public String translation() {
        String percepString = "";
        for (int i = 0; i < SIZE; i++) {
            percepString += ((Fluent)getFluent(i)).toString();
            percepString += "";
        }
        
        percepString += "";
        
        return percepString;
    }
    
    
    
    public void setBlockPainted(boolean bp) {
        PaintPaintedFluent ppf = (PaintPaintedFluent)getFluent(BLOCK_PAINTED);
        
        if (bp)
            ppf.setValue(PaintPaintedFluent.BLOCK_PAINTED);
        else
            ppf.setValue(PaintPaintedFluent.NOT_BLOCK_PAINTED);
    }   
    
     public void setGripperClean(boolean gc) {
        PaintCleanFluent gcf = (PaintCleanFluent)getFluent(GRIPPER_CLEAN);
        
        if (gc)
            gcf.setValue(PaintCleanFluent.GRIPPER_CLEAN);
        else
            gcf.setValue(PaintCleanFluent.NOT_GRIPPER_CLEAN);
    }   
    
    public void setGripperDry(boolean gd) {
        PaintDryFluent gdf = (PaintDryFluent)getFluent(GRIPPER_DRY);
        
        if (gd)
            gdf.setValue(PaintDryFluent.GRIPPER_DRY);
        else
            gdf.setValue(PaintDryFluent.NOT_GRIPPER_DRY);
    }
    
    public void setHoldingBlock(boolean hb) {
        PaintHoldingFluent phf = (PaintHoldingFluent)getFluent(HOLDING_BLOCK);
        
        if (hb)
            phf.setValue(PaintHoldingFluent.HOLDING_BLOCK);
        else
            phf.setValue(PaintHoldingFluent.NOT_HOLDING_BLOCK);
    }
    
      public void setReward(int rew) {
        PaintRewardFluent prf = (PaintRewardFluent)getFluent(REWARD);
        
        if (rew == 0)
            prf.setValue(PaintRewardFluent.NO_REWARD);
        else if (rew >0 )
            prf.setValue(PaintRewardFluent.POSITIVE_REWARD);
        else
            prf.setValue(PaintRewardFluent.NEGATIVE_REWARD);
    }
          
   
    
     public ClauseElements convertToClauseElements(boolean precursor) {
         ClauseElements clauseElements = new ClauseElements();
         for (int i = 0; i < this.getNumFluents(); i++) {
             switch(i) {
                 //BLOCK_PAINTED = 0, GRIPPER_CLEAN = 1, GRIPPER_DRY = 2, HOLDING_BLOCK = 3;
                 case 0:
                     Clause painted;
                     if (precursor == true)
                         painted = new Clause("Painted", 1);
                     else
                         painted = new Clause("Painted Next", 1);
                     Variable isPainted = new Variable();
                     isPainted.setType("Bool");
                     isPainted.setValue(this.getFluent(i).getValue());
                     painted.setPredicate(0, isPainted); 
                     clauseElements.add(painted);
                     break;
                 case 1:
                     Clause clean;
                     if (precursor == true)
                         clean = new Clause("Clean", 1);
                     else
                         clean = new Clause("Clean Next", 1);
                     Variable isClean = new Variable();
                     isClean.setType("Bool");
                     isClean.setValue(this.getFluent(i).getValue());
                     clean.setPredicate(0, isClean); 
                     clauseElements.add(clean);
                     break;
                 case 2:
                     Clause dry;
                     if (precursor == true)
                         dry = new Clause("Dry", 1);
                     else
                         dry = new Clause("Dry Next", 1);
                     Variable isDry = new Variable();
                     isDry.setType("Bool");
                     isDry.setValue(this.getFluent(i).getValue());
                     dry.setPredicate(0, isDry); 
                     clauseElements.add(dry);
                     break;
                 case 3:
                     Clause holding;
                     if (precursor == true)
                         holding = new Clause("Holding", 1);
                     else
                         holding = new Clause("Holding Next", 1);
                     Variable isHolding = new Variable();
                     isHolding.setType("Bool");
                     isHolding.setValue(this.getFluent(i).getValue());
                     holding.setPredicate(0, isHolding); 
                     clauseElements.add(holding);
                     break;
                 case 4:
                     Clause reward;
                     if (precursor == true)
                         holding = new Clause("Reward", 1);
                     else
                         holding = new Clause("Reward Next", 1);
                     Variable rewardVar = new Variable();
                     rewardVar.setType("Rew");
                     rewardVar.setValue(this.getFluent(i).getValue());
                     holding.setPredicate(0, rewardVar); 
                     clauseElements.add(holding);
                     break;
                 default:
                     break;
             }
                  
         }
             
         return clauseElements;
     }
     
     public boolean legalState() {
         return true;
     }
     
     public double reward() {
         switch (((Fluent)perceptions.get(4)).getValue()) {
             case PaintRewardFluent.NO_REWARD: return 0;
             case PaintRewardFluent.POSITIVE_REWARD: return 1.0f;
             case PaintRewardFluent.NEGATIVE_REWARD: return -10.0f;
         }
         return 1.0f;
     }
     
     public Percep convertFromClauseElements(ClauseElements clauseElements) {
        PaintAgentPercep pap = new PaintAgentPercep();
        if (clauseElements.size() > (getNumFluents() + 1)) {
            int hello = 0;
        }
        
        for (int i =0; i < clauseElements.size(); i++) { 
            int percepPos = 0;
            if ((((Clause)clauseElements.get(i)).getName().equals("Painted")) ||
                   (((Clause)clauseElements.get(i)).getName().equals("Painted Next")))
            {
                percepPos = 0;
            }
            else if ((((Clause)clauseElements.get(i)).getName().equals("Clean")) ||
                   (((Clause)clauseElements.get(i)).getName().equals("Clean Next")))
            {
                percepPos = 1;
            }
            else if ((((Clause)clauseElements.get(i)).getName().equals("Dry")) ||
                   (((Clause)clauseElements.get(i)).getName().equals("Dry Next")))
            {
                percepPos = 2;
            }
            else if ((((Clause)clauseElements.get(i)).getName().equals("Holding")) ||
                   (((Clause)clauseElements.get(i)).getName().equals("Holding Next")))
            {
                percepPos = 3;
            }    
            else if ((((Clause)clauseElements.get(i)).getName().equals("Reward")) ||
                   (((Clause)clauseElements.get(i)).getName().equals("Reward Next")))
            {
                percepPos = 4;
            } else {
                percepPos = 0;
                System.out.print("Error in percep pos. String not known");
                System.out.print(((Clause)clauseElements.get(i)).getName());
            }
            
            Fluent pf = pap.getFluent(percepPos);
            pf.convertFromClause((Clause)clauseElements.get(i));
        }
        
        return pap;
     }
    
     
     @Override
    public void readFromString(String str) {
    
    System.out.println("The target String (" + str + ") is " + str.length() + "chars long");
    //String out = "";
    for (int i = 0; i < str.length()-1; i++)
    {
//        switch(str.charAt(i)) {
//            case 'W' : p.setPercep(i, 2);
//                break;
//            case 'E' : p.setPercep(i, 0);
//                break;
//            case 'A' : p.setPercep(i, 1);
//                break;
//            // No relevant case found, ERROR is returned
//            default : p.setPercep(i, 4);
//                break;
//        }
        System.out.println(str.charAt(i));
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
