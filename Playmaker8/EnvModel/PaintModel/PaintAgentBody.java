package EnvModel.PaintModel;

/*
 * PredatorAgentBody.java
 *
 * Created on March 13, 2001, 2:50 PM
 */

import java.util.*;
import EnvModel.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * The agent body is an environment object and as such is part of the
 * environment itself. The body implements setting the percept from 
 * the current state of the environment. It also implements the actions
 * available to the agent by directly effecting the environment.
 */
public class PaintAgentBody extends AgentBody {
    
    boolean gripperDry;
    boolean holdingBlock;
    boolean cleanGripper;
    int reward;

    public static final boolean useNotCleanWithPickup = true;
    
    /** Creates new PredatorObject */
    public PaintAgentBody(int objectID, PaintEnvState paintEnvState) {
        super(objectID, paintEnvState);
        gripperDry = true;
        holdingBlock = false;
        cleanGripper = true;
        reward = 0;
    }

    
    public boolean getGripperDry() {
        return gripperDry;
    }
    
    public boolean getHoldingBlock() {
        return holdingBlock;
    }
    
    public boolean getCleanGripper() {
        return cleanGripper;
    }

    public Percep getPercep() {
        return super.getPercep();
    }
    
    public PaintAgentPercep getPaintPercep() {
        return (PaintAgentPercep)getPercep();
    }
    
    public PaintEnvState getPaintEnvState() {
        return (PaintEnvState)getState();
    }
    
    public PaintAction getPredatorAction() {
        return (PaintAction)getAction();
    }
        
 
    //agent body perceives the environment
    //percept includes agent's square as the position to view from
    public void setPercep() {
        if (getPercep() == null)
            setPercep(new PaintAgentPercep());
        Coordinates position = getPaintEnvState().whereIs(this);

      SquareContents contents = getPaintEnvState().getSquareContents(position.getX(), position.getY());
      if (contents != null) {
          boolean blockPainted = false;
                
          for (int j = 0; j < contents.size(); j++) {
            //check that we're not looking at ourselves
            if (contents.getEnvironmentObject(j) != this) {
                String name = contents.getEnvironmentObject(j).getName();

                if (name.equals("Paint block")) {
                    PaintBlock paintBlock = (PaintBlock)contents.getEnvironmentObject(j);
                    if (paintBlock.getState().equals("Block painted"))
                        getPaintPercep().setBlockPainted(true);
                    else
                        getPaintPercep().setBlockPainted(false);
                        
                        
                }
            }  
          }
      }
                
     
        getPaintPercep().setGripperDry(getGripperDry());
        getPaintPercep().setHoldingBlock(getHoldingBlock());
        getPaintPercep().setGripperClean(getCleanGripper());
        getPaintPercep().setReward(reward);
       
        //LogFile logfile = new LogFile();
        //logfile.print(getPredatorPercep().getString());
        //logfile.close();
    }
    
    //this function works directly on the environment
    //at present it will just pick up the first object in the square
    //which is not itself
    public void doPaint() {
        Coordinates positionMe = getPaintEnvState().whereIs(this);
        SquareContents squareContents = getPaintEnvState().getSquareContents(positionMe);
        
        //set the rward to nothing. Only get reard when get new block
        reward = 0;
        
        //itterate through the square contents
        for (int i = 0; i < squareContents.size(); i++)
        {
            //there should only be one block in the square
            EnvironmentObject obj = squareContents.getEnvironmentObject(i);
            if (obj != this) {    
                PaintBlock paintBlock = (PaintBlock)obj;
                
                if (holdingBlock)
                    paintBlock.setState(PaintBlock.BLOCK_PAINTED);
                else {
                    if (Math.random() < 0.1)
                        paintBlock.setState(PaintBlock.BLOCK_PAINTED);
                }
            }
        }
        
        if (getCleanGripper())
        {
            if (holdingBlock)
                cleanGripper = false;
            else {
                if (Math.random() < 0.2)
                    cleanGripper = false;
            }
        }
                
        
        
    }
    
    public void doDry() {
        //set the rward to nothing. Only get reard when get new block
        reward = 0;
        if (!gripperDry && Math.random() < 0.9)
            gripperDry = true;
    }
    
    public void doPickup() {
        //set the rward to nothing. Only get reard when get new block
        reward = 0;
    
        boolean theBlockPainted = false;
        Coordinates positionMe = getPaintEnvState().whereIs(this);
        SquareContents squareContents = getPaintEnvState().getSquareContents(positionMe);

        for (int i = 0; i < squareContents.size(); i++)
        {
            //make sure we don't pick ourselves up
            EnvironmentObject obj = squareContents.getEnvironmentObject(i);
            if (obj != this) {
                //we should remove a block and get a new one...
                //getPaintEnvState().remove(obj, positionMe);
                //but its easier just to set it to unpainted if it was paited
                PaintBlock paintBlock = (PaintBlock)obj;

                if (paintBlock.getStateID() == PaintBlock.BLOCK_PAINTED)
                     theBlockPainted = true;
                else
                     theBlockPainted = false;
            }
        }

       if (useNotCleanWithPickup) {
            if (!holdingBlock && theBlockPainted)
                if (Math.random() < 0.2)
                    cleanGripper = false;

            if (gripperDry) {
                if (!theBlockPainted) {
                    if (Math.random() < 0.95)
                        holdingBlock = true;
                } else {
                     if (Math.random() < 0.75)
                        holdingBlock = true;
                }
            } else {
                 if (!theBlockPainted) {
                     if (Math.random() < 0.15)
                         holdingBlock = true;
                 } else {
                    if (Math.random() < 0.05)
                        holdingBlock = true;
                 }
            }
        } else {
             if (gripperDry) {
                 if (Math.random() < 0.95)
                     holdingBlock = true;
             } else {
                 if (Math.random() < 0.5)
                     holdingBlock = true;
             }
        }
    }
    
    public void doNew() {
        Coordinates positionMe = getPaintEnvState().whereIs(this);
        SquareContents squareContents = getPaintEnvState().getSquareContents(positionMe);
        
        //itterate through the square contents
        for (int i = 0; i < squareContents.size(); i++)
        {
            //make sure we don't pick ourselves up
            EnvironmentObject obj = squareContents.getEnvironmentObject(i);
            if (obj != this) {    
                //we should remove a block and get a new one...
                //getPaintEnvState().remove(obj, positionMe);
                //but its easier just to set it to unpainted if it was paited
                PaintBlock paintBlock = (PaintBlock)obj;
                
                if (paintBlock.getStateID() == PaintBlock.BLOCK_PAINTED)
                    reward = 1;
                else
                    reward =-1;
                
                paintBlock.setState(PaintBlock.NOT_BLOCK_PAINTED);
                //probably need to increase a painted blocks counter if that's our reward
               // String statey = paintBlock.getState();
                //statey = statey;
            }
        }
        
        cleanGripper = true;
        holdingBlock = false;
        
     
        if (Math.random() < 0.7f)
            gripperDry = false;
        else
            gripperDry = true;

    }
    
    public String getName() {
        return ("Agent Body");
    }
    
    //advance the state of the agent by timeStep
    public void advanceStep() {
        /*LogFile logfile = new LogFile();
        logfile.print("\n");
        if (getRole() == PREDATOR) {
            logfile.print("Predator: ");
        }
        else {
            logfile.print("Prey: ");    
        }
        logfile.close();*/
        super.advanceStep();

        if (getAction() != null)
        {
            PaintAction paintAction = (PaintAction)getAction();
            switch (paintAction.getAction()) {
                case PaintAction.DRY: {
                    doDry();
                    break;
                }
                case PaintAction.NEW: {
                    doNew();
                    break;
                }
                case PaintAction.PAINT: {
                    doPaint();
                    break;
                }
                case PaintAction.PICKUP: {
                    doPickup();
                    break;
                }
            }

            //we have taken this action now. Clear action to noop;
            paintAction.clear();
        }
    }
}