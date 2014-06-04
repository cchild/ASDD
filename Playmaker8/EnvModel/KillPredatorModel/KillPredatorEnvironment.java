
package EnvModel.KillPredatorModel;

/*
 * PredatorEnvironment.java
 *
 * Created on March 6, 2001, 6:24 PM
 */

import EnvModel.*;
import java.util.*;
import EnvAgent.KillPredatorAgent.*;
import EnvAgent.*;

/**
 *
 * @author  Chris Child
 * @version 
 * PredatorEnvironment is a TickEnvironment in which the predator
 * and prey take turns to move. 
 */

public class KillPredatorEnvironment extends TickEnvironment
{   
  
    public KillPredatorEnvironment() {
        //constructor for turn based moves and turn based perceptions
        super(true, true);
        state = new KillPredatorEnvState();
    }
   
    
    public Agent addKillPredatorAgent(int xPosition, int yPosition, int role) {
        KillPredatorAgentBody agentBody = ((KillPredatorEnvState)state).addKillPredatorAgentBody(xPosition, yPosition);
        if (role == KillPredatorAgent.PREY) 
            agentBody.setRole(KillPredatorAgentBody.PREY);
        else
            agentBody.setRole(KillPredatorAgentBody.PREDATOR);
        agentBody.setPercep();
        
       
        Agent agent = new KillPredatorAgent(agentBody, role);
        agents.addAgent(agent);
        if (false) {
            System.out.print("WARNING: SET FOR ALTERNATING TURNS");
            if (role == KillPredatorAgent.PREY)
                agents.setAgentTurn(agent, true);
             else
                agents.setAgentTurn(agent, false);
        } else {
             System.out.print("WARNING: SET FOR SIMULATENOUS TURNS");
            if (role == KillPredatorAgent.PREY)
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
      

