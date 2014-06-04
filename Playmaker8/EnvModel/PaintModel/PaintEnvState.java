package EnvModel.PaintModel;

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
public class PaintEnvState extends GridState {

    public PaintEnvState(){
        //everything is created by the parent
        //Create a 1 by 1 grid which is probably enough for the agent which
        //only needs to see a single block at a time
        super(1, 1);
    }
        
    
    /** Creates new State */
    public PaintEnvState(int xSize, int ySize) {
        ;
    }
       
    
    /*Add the predator agent body as an environment object at the given coordinates*/
    public PaintAgentBody addPaintAgentBody() {
        //we are assuming a 1 swuare grid so we just add all objects to the only square
        SquareContents squareContents = getCreateSquareContents(0, 0);
        PaintAgentBody agentBody = (PaintAgentBody)createObject("Paint Body");
        squareContents.addEnvironmentObject(agentBody);
        agentBody.setPercep();
        return agentBody;
    }
    
    public void addPaintBlock() {
        SquareContents squareContents = getCreateSquareContents(0, 0);
        PaintBlock paintBlock = (PaintBlock)createObject("Block");
        squareContents.addEnvironmentObject(paintBlock);
    }
    
    /*All environment objects must be crated through this function in
     *order to create unique object IDs*/
    protected EnvironmentObject createObject(String objectType) {
        if (objectType == "Paint Body") {
            return new PaintAgentBody(getNextObjectID(),this);
        } else if (objectType == "Block") {
            return new PaintBlock(getNextObjectID());
        } 
        else {
            //Should throw an exception here. NOT DONE
            System.out.print("\n tried to create a non-existent object type\n");
            return null;
        }    
        
    }   
}
