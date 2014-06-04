package EnvModel.KillPredatorModel;

/*
 * PredatorEnvState.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

import java.util.*;
import EnvModel.*;

/**
 *
 * @author  Chris Child
 * @version 
 * PredatorEnvState is a GridState and thus stores the environment as
 * a set of squares which can contain objects. 
 */
public class KillPredatorEnvState extends GridState {
    
    int killedState;
    public static int NO_KILL = 0, KILL = 1, ANTI_KILL = 2;

    public KillPredatorEnvState(){
        //everything is created by the parent
        killedState = NO_KILL; 
    }
        
    
    /** Creates new State */
    public KillPredatorEnvState(int xSize, int ySize) {
        ;
    }
        
    /*Example of creating an add object function*/
    //public void addSpanner(int xCoordinate, int yCoordinate) {
    //    SquareContents squareContents = getCreateSquareContents(xCoordinate, yCoordinate);
    //    squareContents.add(createObject("Spanner"));
    //}
    
    /*Add the predator agent body as an environment object at the given coordinates*/
    public KillPredatorAgentBody addKillPredatorAgentBody(int xCoordinate, int yCoordinate) {
        SquareContents squareContents = getCreateSquareContents(xCoordinate, yCoordinate);
        KillPredatorAgentBody agentBody = (KillPredatorAgentBody)createObject("Agent Body");
        squareContents.addEnvironmentObject(agentBody);
        return agentBody;
    }
    
    /*All environment objects must be crated through this function in
     *order to create unique object IDs*/
    protected EnvironmentObject createObject(String objectType) {
        if (objectType == "Agent Body") {
            return new KillPredatorAgentBody(getNextObjectID(),this);
        } //else if (objectType == "Spanner") {
          //  return new PickupSpanner(getNextObjectID());
        //} 
        else {
            //Should throw an exception here. NOT DONE
            System.out.print("\n tried to create a non-existent object type\n");
            return null;
        }    
        
    }  
    
    public void setKilledState(int killedVal) {
        killedState = killedVal;
    }
    
    public int getKilledState() {
        return killedState;
    }
}
