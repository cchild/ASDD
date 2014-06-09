package Model;


/**
 * Class AfterPercepRecord stores a list of transition states for each action state pair
 * 
 * @author (Rima Varsy) 
 * @date (27-09-02)
 */

import EnvModel.PredatorModel.*;
import EnvModel.*;
import java.io.*;

public class AfterPercepRecord implements Cloneable{

    private PredatorAgentPercep after;
    private Action action;
    private int frequency;
    private double reward = 0;
    private int CAPTURE = 0;
    private int EVASION = 1;
    
    public AfterPercepRecord(PredatorAgentPercep after, int goal){
        this.after = (PredatorAgentPercep)after.clone();
        frequency = 1;
        defineGoal(goal);
    }

    public void defineGoal(int goal){
        if (after.getPercep(4) == 1 && goal == CAPTURE){System.out.println(CAPTURE);
            reward = 1;
        }

        if (after.getPercep(4) != 1 && goal == CAPTURE){
           reward = -0.1;
        }

        if  (after.getPercep(4) != 1 && goal == EVASION)
            {reward = 1;}

        if  (after.getPercep(4) == 1 && goal == EVASION)
            {reward = -0.1;}
    }
    
    public Object clone() {
        try {
            AfterPercepRecord a = (AfterPercepRecord)super.clone(); // clone the record
            return a;               // return the clone
        } catch (CloneNotSupportedException e) {
            
            throw new InternalError();
        }
    }

    public PredatorAgentPercep getPercepAfter(){
        return after;
    }

    public void printRec(PrintWriter out){
        out.print("\"" +  getPercepAfter().toString() + "\"");
        out.print("\"" +  getFrequency() + "\"");
     
    }

    public double getReward(){
        return reward;
    } 

    public void setReward(int goal){
        defineGoal(goal);
    } 

    public int getFrequency(){  
        return frequency;
    }

    public void setFrequency(int x){
        frequency = frequency + x;
    }
}
