
package EnvModel.PaintModel;

/*
 * PredatorEnvironment.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

import EnvModel.*;
import java.util.*;
import EnvAgent.PaintAgent.*;
import EnvAgent.*;

/**
 *
 * @author  Chris Child
 * @version 
 * PredatorEnvironment is a TickEnvironment in which the predator
 * and prey take turns to move. 
 */

public class PaintEnvironment extends TickEnvironment
{   
  
    public PaintEnvironment() {
        //constructor for non turn based moves and non turn based perceptions
        super(false, false);
        state = new PaintEnvState();
    }
   
    
    public Agent addPaintAgent(int xPosition, int yPosition) {
        PaintAgentBody agentBody = ((PaintEnvState)state).addPaintAgentBody();
        
        Agent agent = new PaintAgent(agentBody, 0);
        agents.addAgent(agent);
        return agent;
    }
    
    public void addPaintBlock(int xPosition, int yPosition) {
        ((PaintEnvState)state).addPaintBlock(); 
    }
    
    //Example of adding a spanner object
    //public void addSpanner(int xPosition, int yPosition) {
    //    ((PredatorEnvState)state).addSpanner(xPosition, yPosition);
    //}
}
      

