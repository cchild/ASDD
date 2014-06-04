
package EnvModel.PredatorModel;

/*
 * PredatorEnvironment.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

import EnvModel.*;
import java.util.*;
import EnvAgent.PredatorAgent.*;
import EnvAgent.*;

/**
 *
 * @author  Chris Child
 * @version 
 * PredatorEnvironment is a TickEnvironment in which the predator
 * and prey take turns to move. 
 */

public class PredatorEnvironment extends TickEnvironment
{   
  
    public PredatorEnvironment() {
        //constructor for turn based moves and turn based perceptions
        super(true, true);
        state = new PredatorEnvState();
    }
   
    
    public Agent addPredatorAgent(int xPosition, int yPosition, int role) {
        PredatorAgentBody agentBody = ((PredatorEnvState)state).addPredatorAgentBody(xPosition, yPosition);
        if (role == PredatorAgent.PREY) 
            agentBody.setRole(PredatorAgentBody.PREY);
        else
            agentBody.setRole(PredatorAgentBody.PREDATOR);
        agentBody.setPercep();
       
        Agent agent = new PredatorAgent(agentBody, role);
        agents.addAgent(agent);
        if (false) {
            System.out.print("WARNING: SET FOR ALTERNATING TURNS");
            if (role == PredatorAgent.PREY)
                agents.setAgentTurn(agent, true);
             else
                agents.setAgentTurn(agent, false);
        } else {
             System.out.print("WARNING: SET FOR SIMULATENOUS TURNS");
            if (role == PredatorAgent.PREY)
                agents.setAgentTurn(agent, false);
             else
                agents.setAgentTurn(agent, true);
        }
        return agent;
    }
    
    //Example of adding a spanner object
    //public void addSpanner(int xPosition, int yPosition) {
    //    ((PredatorEnvState)state).addSpanner(xPosition, yPosition);
    //}
}
      

