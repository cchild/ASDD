/*
 *This class represents the date structure of the model.
 *@author Rima Varsy
 *@date 27-09-2002

*/

package Model;

import java.util.*;
import EnvModel.PredatorModel.*;
import EnvModel.*;
import java.lang.*;
import java.io.*;

public class SimModel{
    private int counter;
    private ArrayList actionStateRecords;
    private double DISCOUNT_FACTOR ;
    private int OFFLINE_LEARN_CYCLES;
    private int actionCount = 0;
    private int lifespanCounter = 0;
    private FileWriter lifespan = null;
    private int goal = 0;

    public SimModel(){
        actionStateRecords = new ArrayList();
        int counter = 0;
        
    }

    // adds an action state records to the model and keeps track of tbe life span of the prey
    public void addActionState(PredatorAgentPercep before, Action action, PredatorAgentPercep after){
        boolean add = true; int index = 0;
        
        if (after.getPercep(4) != 1){
            actionCount++;
        } else {
                writeLifeSpan(actionCount + "\r\n");
                actionCount = 0;
        }
        
        while (add != false && index < actionStateRecords.size()){
            // if the state action pair exists then send the transition state to the store
            if (samePercep((getActionState(index)).getPercepBefore(), before)){
                if (sameAction((getActionState(index)).getAction(), action)){
                    ((getActionState(index)).getAfterPercepStore()).addAfterPercepRecord(new AfterPercepRecord(after, goal));
                    add = false; 
                    refineUtility(index);
                    learnModel(OFFLINE_LEARN_CYCLES);
                }   
            } else {
                add = true; //if state action pair does not exist then allow addition tothe modelas new experience
            }
            index++;
        }

        if (add == true){
            AfterPercepRecord afterrecord = new AfterPercepRecord(after, goal);
            
            AfterPercepStore afterpercepstore = new AfterPercepStore(afterrecord);
            ActionStateRecord record1 = new ActionStateRecord(before, action, afterpercepstore); 
            ActionStateRecord record2 = (ActionStateRecord)record1.clone();        
            actionStateRecords.add(counter, record2);
            refineUtility(counter)  ;   
            learnModel(OFFLINE_LEARN_CYCLES);       
            counter++;
        }
    }

    public ActionStateRecord getActionState(int which){
        return (ActionStateRecord)actionStateRecords.get(which);
    }
    
    public void writeLifeSpan(String string){
        try{
           lifespan.write(string);
        } catch (IOException e){}
        
    }

    public void closeWriter(){
         try{
            lifespan.close();
        } catch (IOException e){}
    }
    
    public void openWriter(String fileName){
        try{
            lifespan = new FileWriter(fileName+ ".txt");
        } catch (IOException e){}
    }

    public void outputFile(String fileName){
        FileWriter writer = null;
        try{
            writer = new FileWriter(fileName);
            PrintWriter out = new PrintWriter(writer);
            out.print("\"before\"" + " " + "\"action\"" + " " + "\"after\"" + " " + "\"frequency\"" + " " + "\"utility\"");
            for (int i =0; i < actionStateRecords.size(); i++){
                for (int j = 0; j < (getActionState(i).getAfterPercepStore()).getSize(); j++){
                    out.println();
                    out.print("\"" + (getActionState(i).getPercepBefore()).getString() + "\"");
                    out.print(" \" " + (getActionState(i).getAction()).toString() + "\"");
                    ((getActionState(i).getAfterPercepStore()).getAfterPercepRecord(j)).printRec(out);
                    out.print(" \"" + getActionState(i).getUtility() + "\"");
                }
            }
            writer.close();
        } catch (IOException e){}
    }
    
    public void setSettings(double DISCOUNT_FACTOR, int OFFLINE_LEARN_CYCLES, int goal){
        this.DISCOUNT_FACTOR = DISCOUNT_FACTOR;
        this.OFFLINE_LEARN_CYCLES = OFFLINE_LEARN_CYCLES;
        this.goal = goal;
    }

    //performs the Bellman Equation as a utility update
    public void refineUtility(int index){
        double utility = 0;
        for (int afterStateIndex = 0; afterStateIndex < ((getActionState(index)).getAfterPercepStore()).getSize(); afterStateIndex ++){
            double highest = 0;
            for (int modelIndex = 1; modelIndex <actionStateRecords.size(); modelIndex++){
                if (samePercep((getActionState(modelIndex)).getPercepBefore(), (((getActionState(index)).getAfterPercepStore()).getAfterPercepRecord(afterStateIndex)).getPercepAfter())){
                    if (highest < ((ActionStateRecord)actionStateRecords.get(modelIndex)).getUtility())
                        highest = ((ActionStateRecord)actionStateRecords.get(modelIndex)).getUtility();
                }
            }
            double prob = (double)((((getActionState(index)).getAfterPercepStore()).getAfterPercepRecord(afterStateIndex)).getFrequency()) /  (double)(((getActionState(index)).getAfterPercepStore()).getFrequencySum());
            double reward = (double)(((getActionState(index).getAfterPercepStore()).getAfterPercepRecord(afterStateIndex)).getReward());
            utility = utility + (prob * (reward + (DISCOUNT_FACTOR * highest)));
       }
       (getActionState(index)).setUtility(utility) ;
    }
    
    //selects a random state, the a random action taken in that state
    public void learnModel(int times){
        for(int cycle = 0; cycle < times; cycle++){
            Vector actionindex = new Vector();//creates a temporary vector that stores the indexes of actions taken in a state
            int percepindex = (int)(Math.random() * actionStateRecords.size());
            PredatorAgentPercep randomPercep = (getActionState(percepindex)).getPercepBefore();
            int count = 0; int sampleindex = percepindex;
            for (int i = 0; i < actionStateRecords.size(); i++){
                if (samePercep((getActionState(i)).getPercepBefore(), randomPercep)){
                    Integer x = new Integer(i);
                    actionindex.add(count, x);
                    count++;
                }
            }
            if (actionindex.size() > 1)
                sampleindex = ((Integer)(actionindex.get((int)(Math.random() * actionindex.size())))).intValue();
           
            refineUtility(sampleindex);
        }
    }

    //returns an action with the highest utility in a given state
    public Action getGreedyAction(PredatorAgentPercep current){
        PredatorAction action = null; double utility = 0; 
        for (int i = 0 ; i < actionStateRecords.size(); i ++){
            if (samePercep((getActionState(i)).getPercepBefore(), current)){
                if ((getActionState(i)).getUtility() > utility){
                    utility = (getActionState(i)).getUtility();
                    action = (PredatorAction)(getActionState(i)).getAction();
                 }
            }
        }
        //if no such action exists this method then creates a new action.
        if (action == null){
            action = new PredatorAction();
            int direction = (int) (Math.random() * 4 + 1);
            action.setMove(direction);
        } 
        return action;
    }
     
    //allows the goals of the model to be changed and resest previous utilities calculated under the old goals to be reset
    public void changeGoal(int learnTimes){
        for (int index = 0 ; index < actionStateRecords.size(); index ++){
            (getActionState(index)).setUtility(0);
            for (int j = 0; j < (getActionState(index).getAfterPercepStore()).getSize(); j++)
            ((getActionState(index).getAfterPercepStore()).getAfterPercepRecord(j)).setReward(goal);
        }
        learnModel(learnTimes);
        
    }
    
    //checks whether the state exists in the model                
    public boolean samePercep(PredatorAgentPercep before, PredatorAgentPercep after){
        boolean samepercep = true; int i = 0;
        while (samepercep != false && i < 5){
            if (before.getPercep(i) == after.getPercep(i)){
                samepercep = true;
                i++;
            } else{
                samepercep = false;
            }
        }
        return samepercep;
    }

    //checks whether two given actions are the same
    public boolean sameAction(Action action, Action oldaction){
        boolean sameaction = false;
        if (((PredatorAction)action).getAction() == ((PredatorAction)oldaction).getAction() 
            && ((PredatorAction)action).getMoveDirection() == ((PredatorAction)oldaction).getMoveDirection())
            sameaction = true;
        return sameaction;
    }
    
    public Object clone() {
        try {
            SimModel model = (SimModel)super.clone();   // clone the record
            model.actionStateRecords = (ArrayList)actionStateRecords.clone();
            return model;               // return the clone
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen because Stack is Cloneable
            throw new InternalError();
        }
    } 
}