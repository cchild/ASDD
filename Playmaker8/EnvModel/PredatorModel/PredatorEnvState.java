package EnvModel.PredatorModel;

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
public class PredatorEnvState extends GridState {

    public PredatorEnvState(){
        //everything is created by the parent
        ; 
    }
        
    
    /** Creates new State */
    public PredatorEnvState(int xSize, int ySize) {
        ;
    }
        
    /*Example of creating an add object function*/
    //public void addSpanner(int xCoordinate, int yCoordinate) {
    //    SquareContents squareContents = getCreateSquareContents(xCoordinate, yCoordinate);
    //    squareContents.add(createObject("Spanner"));
    //}
    
    /*Add the predator agent body as an environment object at the given coordinates*/
    public PredatorAgentBody addPredatorAgentBody(int xCoordinate, int yCoordinate) {
        SquareContents squareContents = getCreateSquareContents(xCoordinate, yCoordinate);
        PredatorAgentBody agentBody = (PredatorAgentBody)createObject("Agent Body");
        squareContents.addEnvironmentObject(agentBody);
        return agentBody;
    }
    
    /*All environment objects must be crated through this function in
     *order to create unique object IDs*/
    protected EnvironmentObject createObject(String objectType) {
        if (objectType == "Agent Body") {
            return new PredatorAgentBody(getNextObjectID(),this);
        } //else if (objectType == "Spanner") {
          //  return new PickupSpanner(getNextObjectID());
        //} 
        else {
            //Should throw an exception here. NOT DONE
            System.out.print("\n tried to create a non-existent object type\n");
            return null;
        }    
        
    }   
}
